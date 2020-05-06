package com.pazukdev.backend.entity;

import com.pazukdev.backend.constant.security.Role;
import com.pazukdev.backend.entity.abstraction.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_table")
public class UserEntity extends AbstractEntity {

    private Integer rating = 0;
    private String email;
    private String password;
    private String img = "-";
    private String country = "-";
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wishlist_id")
    private WishList wishList = new WishList();


    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", name=" + name +
                ", status=" + status +
                ", rating=" + rating +
                ", email=" + email +
                ", country=" + country +
                '}';
    }
}
