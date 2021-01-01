package com.anilaltunkan.security.repository;

import com.anilaltunkan.security.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
