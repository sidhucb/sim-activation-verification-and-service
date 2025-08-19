package com.example.demo;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/users")
public class UserController {
	
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
    
    public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    
    @GetMapping("/admin")
    public ResponseEntity<List<User>> getAllUsers(){
    	List<User> users = userService.getAllUsers();
    	return ResponseEntity.ok(users);
    }
    
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.login(request, passwordEncoder, jwtUtil);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

}
