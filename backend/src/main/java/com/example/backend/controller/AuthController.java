package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.model.dto.ProfileUpdateRequest;
import com.example.backend.model.dto.UserDto;
import com.example.backend.security.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.backend.service.UserService;
import org.springframework.web.multipart.MultipartFile;
import com.example.backend.service.FileStorageService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
 // Ajusta según tu frontend
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final FileStorageService fileStorageService;

    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService, FileStorageService fileStorageService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
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


    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("No autorizado");
        }

        String email = authentication.getName();  // extrae el email del token
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        User user = userOpt.get();

        return ResponseEntity.ok(Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "profileImagePath",
                user.getProfileImagePath() != null
                        ? user.getProfileImagePath().replace("\\", "/")
                        : "images/default-avatar.png"
        ));
    }


    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateCurrentUser(
            @RequestPart("name") String name,
            @RequestPart("phone") String phone,
            @RequestPart("email") String email,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        return userService.findByEmail(currentEmail)
                .map(user -> {
                    user.setName(name);
                    user.setPhone(phone);
                    user.setEmail(email);

                    if (profileImage != null && !profileImage.isEmpty()) {
                        String path = fileStorageService.save(profileImage);
                        user.setProfileImagePath(path);
                    }

                    User savedUser = userService.saveUser(user);

                    UserDto dto = new UserDto(
                            savedUser.getName(),
                            savedUser.getEmail(),
                            savedUser.getPhone(),
                            savedUser.getProfileImagePath()
                    );

                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }






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
