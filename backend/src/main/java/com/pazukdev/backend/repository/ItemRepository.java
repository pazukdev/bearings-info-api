package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Item findFirstByName(final String name);

    Item findFirstByCategory(final String category);

    Item findFirstByCategoryAndName(final String category, final String name);

    Item findFirstByCategoryAndNameAndStatus(final String category, final String name, final String status);

    Page<Item> findFirst10ByCategory(final String category, final Pageable pageable);

}
