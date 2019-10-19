package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.ChildItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface ChildItemRepository extends JpaRepository<ChildItem, Long> {

    ChildItem findByName(final String name);

}
