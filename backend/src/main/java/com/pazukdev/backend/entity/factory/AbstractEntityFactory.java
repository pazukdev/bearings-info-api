package com.pazukdev.backend.entity.factory;

import com.pazukdev.backend.entity.AbstractEntity;
import com.pazukdev.backend.tablemodel.TableModel;
import com.pazukdev.backend.tablemodel.TableModelFactory;
import com.pazukdev.backend.tablemodel.TableRow;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.pazukdev.backend.util.CategoryUtil.Parameter;

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
        entity.setName(tableRow.getData().get(Parameter.DescriptionIgnored.NAME));
    }

}
