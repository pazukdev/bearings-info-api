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

    UserAction findByName(final String name);

//    List<UserAction> findFirst2ByActionTypeAndItemCategory(final String actionType, final String itemCategory);

    Page<UserAction> findFirst10ByActionTypeAndItemCategory(final String actionType,
                                                            final String itemCategory,
                                                            final Pageable pageable);

    Page<UserAction> findFirst10ByActionTypeAndItemType(final String actionType,
                                                        final String itemType,
                                                        final Pageable pageable);

}
