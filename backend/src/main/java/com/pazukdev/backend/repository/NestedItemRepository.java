package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.NestedItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface NestedItemRepository extends JpaRepository<NestedItem, Long> {

    NestedItem findByName(final String name);

    Page<NestedItem> findByType(final String type, final Pageable pageable);

}
