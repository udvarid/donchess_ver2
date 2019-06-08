package com.donat.donchess.service;

import com.donat.donchess.domain.Role;
import com.donat.donchess.domain.User;
import com.donat.donchess.dto.RegisterDto;
import com.donat.donchess.dto.UserDto;
import com.donat.donchess.repository.RoleRepository;
import com.donat.donchess.repository.UserRepository;
import com.donat.donchess.security.UserDetailsImpl;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       EmailService emailService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email);
        if (user == null || !user.isEnabled()) {
            throw new UsernameNotFoundException(email);
        }
        return new UserDetailsImpl(user);
    }


    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void registerUser(RegisterDto registerDto) throws Exception {
        //TODO egyedi exceptionok gyártása, melyek alapján a SPRING dobjon megkülönböztetett HTTP választ
        if (findByEmail(registerDto.getEmail()) != null) {
            throw new Exception("Already registered user!");
        }
        if (!validPassword(registerDto.getPassword())) {
            throw new Exception("Not valid password!");
        }
        if (registerDto.getFullName().isEmpty()) {
            throw new Exception("Not filled full name!");
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

    private boolean validPassword(String password) {
        //TODO password validációt kidolgozni (PIR-es példa), esetleg mező annotációval alkalmazni
        if (password.isEmpty()) {
            return false;
        }
        return true;
    }

    public void confirmUserByToken(String token) throws Exception {
        if (token.isEmpty()) {
            throw new Exception("Invalid token");
        }
        User user = userRepository.findByAuthenticationToken(token).orElseThrow(()-> new Exception("Invalid token"));

        user.setAuthenticationToken(null);
        user.setEnabled(true);

        userRepository.saveAndFlush(user);
    }

    //TODO rendszeresen tisztítani az aktiválatlan regisztrációkat - ehhez kell a regisztráció ideje is

    public Set<UserDto> prepareList() {
        List<User> users = userRepository.findAll();
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
