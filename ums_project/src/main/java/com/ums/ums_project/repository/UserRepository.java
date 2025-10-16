package com.ums.ums_project.repository;

import com.ums.ums_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCollegeId(String collegeId);
}
