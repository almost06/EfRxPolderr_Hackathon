package com.example.demo.repository;

import com.example.demo.entity.Donor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
