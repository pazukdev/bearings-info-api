package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {

    WishList findByName(final String name);

}
