package io.payflow.service;

import io.payflow.component.JwtTokenProvider;
import io.payflow.dto.LoginDTO;
import io.payflow.dto.LoginResponseDTO;
import io.payflow.exception.AuthenticationException;
import io.payflow.model.User;
import io.payflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponseDTO login(LoginDTO loginDTO) {
        User user  = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return new LoginResponseDTO(token);

    }
}
