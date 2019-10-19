package com.pazukdev.backend.service;

import com.pazukdev.backend.converter.abstraction.EntityDtoConverter;
import com.pazukdev.backend.dto.AbstractDto;
import com.pazukdev.backend.entity.AbstractEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pazukdev.backend.util.SpecificStringUtil.replaceBlankWithDash;

/**
 * @author Siarhei Sviarkaltsau
 */
@RequiredArgsConstructor
public abstract class AbstractService<Entity extends AbstractEntity, Dto extends AbstractDto> {

    @Getter
    protected final JpaRepository<Entity, Long> repository;
    protected final EntityDtoConverter<Entity, Dto> converter;

    @Transactional
    public List<Entity> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Entity getOne(final Long id) throws EntityExistsException {
        //checkEntityExists(id);
        return repository.getOne(id);
    }

    @Transactional
    public Entity create(final Dto dto) {
        dto.setName(replaceBlankWithDash(dto.getName()));
        return repository.save(converter.convertToEntity(dto));
    }

    @Transactional
    public Set<Entity> createAll(final Set<Dto> dtos) {
        final Set<Entity> entities = new HashSet<>();
        for (final Dto dto : dtos) {
            entities.add(create(dto));
        }
        return entities;
    }

    @Transactional
    public Entity update(final Long id, final Dto dto) {
        dto.setId(id);
        dto.setName(replaceBlankWithDash(dto.getName()));
        return repository.save(converter.convertToEntity(dto));
    }

    @Transactional
    public Entity update(final Entity entity) {
        return repository.save(entity);
    }

    @Transactional
    public Entity delete(final Long id) throws EntityExistsException {
        final Entity entity = getOne(id);
        repository.deleteById(entity.getId());
        return entity;
    }

    @Transactional
    public List<Entity> deleteAll(final List<Long> ids) {
        final List<Entity> entities = new ArrayList<>();
        for (final Long id : ids) {
            entities.add(getOne(id));
            delete(id);
        }
        return entities;
    }

    public Boolean entityExists(final Long id) throws EntityExistsException {
        if (!repository.existsById(id)) {
            throw new EntityExistsException("entity under id == " + id + " is not exist");
        }
        return true;
    }

    private void checkEntityExists(final Long id) throws EntityExistsException {
        if (!repository.existsById(id)) {
            throw new EntityExistsException("entity under id == " + id + " is not exist");
        }
    }

    @Transactional
    public List<Entity> search(final List<Long> ids) {
        final List<Entity> entities;
        if (ids == null || ids.isEmpty()) {
            entities = repository.findAll();
        } else {
            entities = repository.findAllById(ids);
        }
        return entities;
    }

    @Transactional
    public abstract Entity findByName(final String name);

}
















