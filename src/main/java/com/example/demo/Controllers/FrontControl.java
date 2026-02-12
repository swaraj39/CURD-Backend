package com.example.demo.Controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Entity.User;
import com.example.demo.Entity.Users;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Repository.UsersRepo;
@RestController
@CrossOrigin(origins = "https://crud-frontend-yb8t.vercel.app", allowCredentials = "true")
public class FrontControl {

    @Autowired
    private UsersRepo userRepo;
    @Autowired
    private UserRepo UserRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = Logger.getLogger("FrontControl.class");

    // Endpoint to return the currently authenticated user details
    @GetMapping("/test")
    public ResponseEntity<Users> home(Authentication authentication) {
        logger.info(authentication.getName());
        return ResponseEntity.ok(
                userRepo.findByEmail(authentication.getName()).orElseThrow());
    }

    // Endpoint to register a new user (sign up)
    @PostMapping("/signin")
    public String newUser(@RequestBody Users user) {
        logger.info("Received user: " + user);
        //System.out.println("Received user: " + user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return "Saved";
    }

    // Endpoint to handle logout requests
    @RequestMapping("/logout")
    public ResponseEntity<String> logout() {
        logger.info("Logout");
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }


    // Endpoint to fetch the currently authenticated user
    @GetMapping("/user")
    public ResponseEntity<Users> getUser(Authentication authentication) {
        logger.info("Getting all users by : " + authentication.getName());
        return ResponseEntity.ok(userRepo.findByEmail(authentication.getName()).orElseThrow());
    }


    // Endpoint to add a new user to the system
    @PostMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestBody User user) {

        // Check if email already exists
        if (UserRepo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email already exists"));
        }
        // Validate Date of Birth
        Date today = new Date();
        if (user.getDOB() != null && today.before(user.getDOB())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid Date of Birth"));
        }
        // ⚠️ IMPORTANT: Encode password in real projects
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserRepo.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "User created successfully"));

    }

    // Endpoint to update an existing user by email
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        Optional<User> optionalUser = UserRepo.findById(id);
        // if user is not there
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found with id: " + id);
        }
        // update the user
        User u = optionalUser.get();

        u.setName(user.getName());
        u.setDOB(user.getDOB());
        u.setPassword(user.getPassword());
        u.setPhone(user.getPhone());
        u.setEmail(user.getEmail());

        UserRepo.save(u);

        return ResponseEntity.ok(UserRepo.findAll());
    }


    // Endpoint to delete a user by email
    @DeleteMapping("/users/{email}")
    public ResponseEntity<List<User>> deleteUser(@PathVariable String email) {
        UserRepo.findByEmail(email).ifPresent(user -> UserRepo.delete(user));
        return ResponseEntity.ok(UserRepo.findAll());
    }

    // Endpoint to fetch all users
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        System.out.println("Fetching all users");
        return ResponseEntity.ok(UserRepo.findAll());
    }
    
}
