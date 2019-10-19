package com.pazukdev.backend.entity;

import com.pazukdev.backend.constant.security.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "user")
public class UserEntity extends AbstractEntity {

    private Integer rating = 0;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wishlist_id")
    private WishList wishList = new WishList();
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "likelist_id")
    private LikeList likeList = new LikeList();

}
