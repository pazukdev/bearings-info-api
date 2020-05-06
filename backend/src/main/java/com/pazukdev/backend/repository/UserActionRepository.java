package com.pazukdev.backend.repository;

import com.pazukdev.backend.entity.UserAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Siarhei Sviarkaltsau
 */
@Repository
public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    Page<UserAction> findFirst10ByActionTypeAndNote(final String actionType,
                                                    final String note,
                                                    final Pageable pageable);

}
