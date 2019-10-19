package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    UserAction findByName(final String name);

}
