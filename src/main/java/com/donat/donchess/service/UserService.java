package com.donat.donchess.service;

import com.donat.donchess.domain.QUser;
import com.donat.donchess.domain.Role;
import com.donat.donchess.domain.User;
import com.donat.donchess.dto.User.RegisterDto;
import com.donat.donchess.dto.User.UserDto;
import com.donat.donchess.exceptions.InvalidException;
import com.donat.donchess.repository.RoleRepository;
import com.donat.donchess.repository.UserRepository;
import com.donat.donchess.security.UserDetailsImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private RoleRepository roleRepository;

    @Autowired
    private Provider<EntityManager> entityManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       EmailService emailService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = findByEmail(email);
        if (user == null || !user.isEnabled()) {
            throw new UsernameNotFoundException(email);
        }
        return new UserDetailsImpl(user);
    }


    public User findByEmail(String email) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QUser userFromQ = QUser.user;

        return query.selectFrom(userFromQ)
                .where(userFromQ.email.eq(email))
                .fetchOne();
    }

    public void registerUser(RegisterDto registerDto) {
        if (findByEmail(registerDto.getEmail()) != null) {
            throw new InvalidException("Already registered user!");
        }
        if (registerDto.getPassword().isEmpty()) {
            throw new InvalidException("Not valid password!");
        }
        if (registerDto.getFullName().isEmpty()) {
            throw new InvalidException("Not filled full name!");
        }

        User newUser = new User();
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        newUser.setFullname(registerDto.getFullName());

        Role role = roleRepository.findByRole("ROLE_USER");
        newUser.getRoles().add(role);
        newUser.setEnabled(false);
        newUser.setAuthenticationToken(RandomString.make(20));

        emailService.sendAuthenticatonMail(newUser);

        userRepository.saveAndFlush(newUser);

    }

    public void confirmUserByToken(String token) {
        if (token.isEmpty()) {
            throw new InvalidException("Invalid token");
        }
        User user = userRepository.findByAuthenticationToken(token).orElseThrow(() -> new InvalidException("Invalid token"));

        user.setAuthenticationToken(null);
        user.setEnabled(true);

        userRepository.saveAndFlush(user);
    }

    //TODO rendszeresen tisztítani az aktiválatlan regisztrációkat - ehhez kell a regisztráció ideje is

    public Set<UserDto> prepareList() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QUser userFromQ = QUser.user;

        List<User> users = query.selectFrom(userFromQ)
                .where(userFromQ.enabled.eq(true))
                .orderBy(userFromQ.fullname.asc())
                .fetch();

        Set<UserDto> userDtos = new HashSet<>();

        users.forEach(user -> {
            UserDto userDto = new UserDto();
            userDto.setFullName(user.getFullname());
            userDto.setId(user.getId());
            userDto.setRole(user.getRoles().toString());
            userDtos.add(userDto);
        });


        return userDtos;

    }
}
