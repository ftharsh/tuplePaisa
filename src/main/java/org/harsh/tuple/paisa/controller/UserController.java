package org.harsh.tuple.paisa.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.harsh.tuple.paisa.model.User;
import org.harsh.tuple.paisa.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j  // This annotation enables logging
public class UserController {

    private final UserService userService;

    // !User Registration
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        log.info("Attempting to register user: {}", user.getUsername());

            User registeredUser = userService.registerUser(user);
            log.info("User registered successfully: {}", registeredUser.getUsername());
            return ResponseEntity.ok(registeredUser);
//        }
    }

    // !User Login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        log.info("Attempting to log in user: {}", username);
            // Call the UserService loginUser method to get the token
            String token = userService.loginUser(username, password);
            log.info("User logged in successfully: {}", username);


            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("Received request to delete user with id: {}", id);
            userService.deleteUser(id);
            log.info("Successfully deleted user and wallet with id: {}", id);
            return ResponseEntity.noContent().build();

    }
}
