package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.Users;

@Repository
public interface UsersRepo extends JpaRepository<Users, String> {

    Optional<Users> findByEmail(String username);
    
}
