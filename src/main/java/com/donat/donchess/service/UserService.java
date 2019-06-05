package com.donat.donchess.service;

import com.donat.donchess.domain.User;
import com.donat.donchess.dto.RegisterDto;
import com.donat.donchess.repository.UserRepository;
import com.donat.donchess.security.UserDetailsImpl;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email);
        if (user == null) {
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
        newUser.setEnabled(false);
        newUser.setAuthenticationToken(RandomString.make(20));
        //TODO ezt a RandomString cuccot megnézni

        emailService.sendAuthenticatonMail(newUser);

        userRepository.saveAndFlush(newUser);

    }

    //TODO autentikációs szervízt megírni

    private boolean validPassword(String password) {
        //TODO password validációt kidolgozni (PIR-es példa), esetleg mező annotációval alkalmazni
        if (password.isEmpty()) {
            return false;
        }
        return true;
    }
}
