package com.example.AdrianoCoffee.Repository;

import com.example.AdrianoCoffee.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users,Long> {
    @Query("SELECT u from Users u WHERE u.email = ?1")
    Optional<Users> findUsersByEmail(String email);

    Optional<Users> findUsersByFirstNameAndSecondName(String firstname, String secondName);

    List<Users> findUsersByFirstName(String firstName);
    List<Users> findUsersBySecondName(String secondName);
}
