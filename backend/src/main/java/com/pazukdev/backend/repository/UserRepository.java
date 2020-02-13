package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findFirstByName(final String name);

    UserEntity findFirstByEmail(final String email);

    UserEntity findFirstByNameAndStatus(final String name, final String status);

}
