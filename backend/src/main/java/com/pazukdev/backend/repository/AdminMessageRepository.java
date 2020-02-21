package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.AdminMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface AdminMessageRepository extends JpaRepository<AdminMessage, Long> {
}
