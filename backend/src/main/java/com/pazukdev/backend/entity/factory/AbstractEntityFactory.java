package com.pazukdev.backend.entity.factory;

import com.pazukdev.backend.entity.AbstractEntity;
import com.pazukdev.backend.service.AbstractService;
import com.pazukdev.backend.tablemodel.TableModel;
import com.pazukdev.backend.tablemodel.TableModelFactory;
import com.pazukdev.backend.tablemodel.TableRow;
import com.pazukdev.backend.util.CSVFileUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
public abstract class AbstractEntityFactory<Entity extends AbstractEntity> {

    public List<Entity> createEntitiesFromCSVFile() {
        return createEntitiesFromTableModel(getTableModelFromCSVFile());
    }

    public abstract Entity createEntity();

    protected abstract String[] getCSVFilesPaths();

    public Entity findByName(final String name) {
        return createEntitiesFromCSVFile()
                .stream()
                .filter(entity -> entity.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    protected void applyCharacteristics(final Entity entity, final TableRow tableRow) {
        applyName(entity, tableRow);
    }

    private TableModel getTableModelFromCSVFile() {
        final TableModelFactory factory = TableModelFactory.create();
        return factory.createTableModel(getCSVFilesPaths());
    }

    private List<Entity> createEntitiesFromTableModel(final TableModel tableModel) {
        List<Entity> entities = new ArrayList<>();

        for (final TableRow tableRow : tableModel.getTableRows()) {
            final Entity entity = createEntity(tableRow);
            entities.add(entity);
        }

        return entities;
    }

    private Entity createEntity(final TableRow tableRow) {
        return getEntityWithAppliedCharacteristics(tableRow);
    }

    private Entity getEntityWithAppliedCharacteristics(final TableRow tableRow) {
        final Entity entity = createEntity();
        applyCharacteristics(entity, tableRow);
        return entity;
    }

    protected void applyName(final AbstractEntity entity, final TableRow tableRow) {
        String name = tableRow.getData().get("Name");
        entity.setName(name);
    }

    @SuppressWarnings("unchecked")
    protected <T extends AbstractEntity> T getEntity(final String name,
                                                     final AbstractService service,
                                                     final AbstractEntityFactory<T> dtoFactory) {
        final T entity;
        if (service != null) {
            entity = (T) service.findFirstByName(name);
        } else {
            entity = CSVFileUtil.findByName(name, dtoFactory);
        }
        return entity;
    }

}
