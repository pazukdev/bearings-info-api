package com.pazukdev.backend.entity;

import com.pazukdev.backend.entity.abstraction.AbstractEntity;
import com.pazukdev.backend.entity.abstraction.Typeable;
import com.pazukdev.backend.listener.AuditListener;
import com.pazukdev.backend.util.UserActionUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * @author Siarhei Sviarkaltsau
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@EntityListeners(AuditListener.class)
@Table(name = "link")
public class Link extends AbstractEntity implements Typeable {

    private String type;
    private String url;
    @Column(name = "country_code")
    private String countryCode;

    @Override
    public String toString() {
        return "link "
                + " id=" + id
                + " type=" + type
                + " url=" + url
                + " countryCode=" + countryCode;
    }

    @Override
    public String getValuationType() {
        return UserActionUtil.ValuationType.LINK;
    }
}
