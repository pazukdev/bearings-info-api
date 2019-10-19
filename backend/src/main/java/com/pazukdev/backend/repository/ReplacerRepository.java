package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.Replacer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface ReplacerRepository extends JpaRepository<Replacer, Long> {

    Replacer findByName(final String name);

}
