package io.payflow.service;

import io.payflow.dto.UserDTO;
import io.payflow.exception.ResourceNotFoundException;
import io.payflow.model.User;
import io.payflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public User save(UserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = converToEntity(userDTO);
        return userRepository.save(user);
    }

    public User update(Long id, UserDTO userDTO) {
        User existing = getUserOrThrow(id);
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            userDTO.setPassword(existing.getPassword());
        }
        updateUserFields(existing, userDTO);
        return userRepository.save(existing);
    }

    public User findById(Long id) {
        return getUserOrThrow(id);
    }

    public Optional<User> findByIdOptional(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        User existing = getUserOrThrow(id);
        userRepository.delete(existing);
    }

    public User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User converToEntity(UserDTO userDTO) {
        return User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .build();
    }

    public void updateUserFields(User existing, UserDTO userDTO) {
        existing.setName(userDTO.getName());
        existing.setEmail(userDTO.getEmail());
        existing.setPassword(userDTO.getPassword());
    }
}
