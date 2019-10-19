package com.pazukdev.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WishListDto extends AbstractDto {

    private final static long serialVersionUID = 12343L;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Long> itemIds = new HashSet<>();

}
