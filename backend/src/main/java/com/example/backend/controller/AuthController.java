package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.backend.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
 // Ajusta según tu frontend
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        System.out.println("🟡 Intentando login con:");
        System.out.println("    Usuario: " + request.getUsername());
        System.out.println("    Contraseña: " + request.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            System.out.println("🟢 Autenticación exitosa");
            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException ex) {
            System.out.println("🔴 Credenciales inválidas");
            return ResponseEntity.status(401).body("Credenciales inválidas");
        } catch (Exception ex) {
            System.out.println("🔴 Error inesperado: " + ex.getMessage());
            return ResponseEntity.status(500).body("Error en el servidor");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("No autorizado");
        }
        // Puedes devolver el principal (user details)
        return ResponseEntity.ok(authentication.getPrincipal());
    }



    /*
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        return jwtUtil.generateToken(request.getUsername());
    }

     */

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("phone") String phone,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        // Aquí puedes manejar la lógica de guardado del usuario.
        // Puedes validar si ya existe el email, guardar el archivo si lo necesitas, etc.

        // Por simplicidad, vamos a delegar en tu servicio
        User newUser = userService.registerUser(name, email, password, phone, profileImage);

        // Opcional: generar token después del registro
        String token = jwtUtil.generateToken(email);

        return ResponseEntity.ok().body(Map.of("token", token, "user", newUser));
    }

    // DTO para login
    public static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
