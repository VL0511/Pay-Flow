package io.payflow.controller;

import io.payflow.dto.LoginDTO;
import io.payflow.dto.LoginResponseDTO;
import io.payflow.service.AuthService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles user authentication by validating the provided credentials and returning a JWT token.
     * The token is also stored as an HTTP-only cookie for secure client-server communication.
     *
     * @param loginDTO An object containing the user's login credentials (e.g., email and password).
     * @return A {@link ResponseEntity} containing a {@link LoginResponseDTO} with the JWT token and user details.
     *         The response also includes a "Set-Cookie" header to store the token as an HTTP-only cookie.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginResponseDTO response = authService.login(loginDTO);

            ResponseCookie jwtCookie = ResponseCookie.from("token", response.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

            return ResponseEntity.ok()
                    .header("Set-Cookie", jwtCookie.toString())
                    .body(response);

        } catch (AuthenticationServiceException e) {
            return ResponseEntity.status(401).body(new LoginResponseDTO("Erro de autenticação: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new LoginResponseDTO("Erro desconhecido: " + e.getMessage()));
        }
    }
}
