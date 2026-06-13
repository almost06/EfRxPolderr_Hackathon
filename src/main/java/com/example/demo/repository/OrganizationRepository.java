package com.example.demo.repository;

import com.example.demo.entity.Organization;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    List<Organization> findByOrderByRecentFundingReceivedEurAsc();

    Optional<Organization> findByContactEmailIgnoreCase(String contactEmail);

    boolean existsByContactEmailIgnoreCase(String contactEmail);
}
