package com.pazukdev.backend.service;

import com.pazukdev.backend.constant.Status;
import com.pazukdev.backend.converter.abstraction.EntityDtoConverter;
import com.pazukdev.backend.dto.AbstractDto;
import com.pazukdev.backend.entity.abstraction.AbstractEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.List;

import static com.pazukdev.backend.util.SpecificStringUtil.replaceEmptyWithDash;

/**
 * @author Siarhei Sviarkaltsau
 */
@RequiredArgsConstructor
public abstract class AbstractService<Entity extends AbstractEntity, Dto extends AbstractDto> {

    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

    @Getter
    protected final JpaRepository<Entity, Long> repository;
    protected final EntityDtoConverter<Entity, Dto> converter;

    @Transactional
    public List<Entity> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Entity findOne(final Long id) throws EntityExistsException {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public Entity findFirst(final Long id) throws EntityExistsException {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public Entity create(final Dto dto) {
        dto.setName(replaceEmptyWithDash(dto.getName()));
        return repository.save(converter.convertToEntity(dto));
    }

    @Transactional
    public void softDelete(final Long id) {
        final Entity entity = findOne(id);
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }

    @Transactional
    public void hardDelete(final Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void softDeleteAll(final List<Long> ids) {
        for (final Long id : ids) {
            softDelete(id);
        }
    }

    @Transactional
    public Entity update(final Entity entity) {
        return repository.save(entity);
    }

    @Transactional
    public abstract Entity findFirstByName(final String name);

}
















