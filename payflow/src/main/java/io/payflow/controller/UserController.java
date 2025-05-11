package io.payflow.controller;

import io.payflow.dto.UserDTO;
import io.payflow.exception.InternalServerErrorException;
import io.payflow.exception.MethodNotAllowedException;
import io.payflow.exception.ResourceNotFoundException;
import io.payflow.model.User;
import io.payflow.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found.");
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id, HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            throw new MethodNotAllowedException("GET method not allowed here.");
        }

        Optional<User> user = userService.findUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found."));
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody  UserDTO user) {
        try {

            User savedUser = userService.save(user);
            return ResponseEntity.status(201).body(savedUser);
        } catch (Exception e) {
            throw new InternalServerErrorException("Unexpected error occurred while creating user.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User updatedUser = userService.update(id, userDTO);

        if (updatedUser == null) {
            throw new ResourceNotFoundException("User #" + id);
        }

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
