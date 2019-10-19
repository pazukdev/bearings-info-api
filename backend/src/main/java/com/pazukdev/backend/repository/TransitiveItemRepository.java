package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.TransitiveItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface TransitiveItemRepository extends JpaRepository<TransitiveItem, Long> {

    TransitiveItem findByName(final String name);

}
