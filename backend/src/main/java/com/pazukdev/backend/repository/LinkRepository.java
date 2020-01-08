package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

}
