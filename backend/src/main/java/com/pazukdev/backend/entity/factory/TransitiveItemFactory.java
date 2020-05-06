package com.pazukdev.backend.entity.factory;

import com.pazukdev.backend.dto.LinkDto;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.entity.abstraction.AbstractEntity;
import com.pazukdev.backend.tablemodel.TableRow;
import com.pazukdev.backend.util.FileUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.pazukdev.backend.util.CategoryUtil.Parameter.DescriptionIgnored.*;
import static com.pazukdev.backend.util.CategoryUtil.isDescriptionIgnored;
import static com.pazukdev.backend.util.SpecificStringUtil.isEmpty;
import static com.pazukdev.backend.validator.CodeValidator.isCountryCodeValid;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Component
public class TransitiveItemFactory extends AbstractEntityFactory<TransitiveItem> {

    @Override
    protected String[] getCSVFilesPaths() {
        return FileUtil.getItemsDataGoogleSheetsIds(true);
    }

    @Override
    public TransitiveItem createEntity() {
        return new TransitiveItem();
    }

    @Override
    protected void applyCharacteristics(final TransitiveItem item, final TableRow tableRow) {
        super.applyCharacteristics(item, tableRow);

        applyCategory(item, tableRow);
//        final boolean vehicle = item.getCategory().equals(Category.VEHICLE);

        applyStatus(item, tableRow);
        applyImage(item, tableRow);
        applyDescription(item, tableRow);
        applyReplacers(item, tableRow);
        applyLinks(item, tableRow);
    }

    private void applyCategory(final TransitiveItem item, final TableRow tableRow) {
        final String category = tableRow.getData().get(CATEGORY);
        item.setCategory(category);
    }

    private void applyImage(final TransitiveItem item, final TableRow tableRow) {
        item.setImage(tableRow.getData().get(IMAGE));
    }

    protected void applyStatus(final AbstractEntity entity, final TableRow tableRow) {
        final String status = tableRow.getData().get(STATUS);
        entity.setStatus(status != null ? status : "active");
    }

    private void applyDescription(final TransitiveItem item, final TableRow tableRow) {
        String description = "";
        for (final Map.Entry<String, String> entry : tableRow.getData().entrySet()) {
            final String key = entry.getKey();
            if (isDescriptionIgnored(key)) {
                continue;
            }
            description = description + entry.getKey() + ":" + entry.getValue() + ";;";
        }
        item.setDescription(description.replaceAll(";;;", ";;"));
    }

    private void applyReplacers(final TransitiveItem item, final TableRow tableRow) {
        final String replacer = tableRow.getData().get(REPLACER);
        item.setReplacer(replacer != null ? replacer : "-");
    }

    private void applyLinks(final TransitiveItem item, final TableRow tableRow) {
        item.setManual(tableRow.getData().get(MANUAL));
        item.setParts(tableRow.getData().get(PARTS_CATALOG));
        item.setDrawings(tableRow.getData().get(DRAWINGS));
        item.setWiki(tableRow.getData().get(WIKI));
        item.setWebsite(tableRow.getData().get(WEBSITE));

        for (final Map.Entry<String, String> entry : tableRow.getData().entrySet()) {
            final String key = entry.getKey();
            if (key == null || isEmpty(entry.getValue())) {
                continue;
            }
            if (key.length() == 2 && isCountryCodeValid(key)) {
                final LinkDto link = new LinkDto();
                link.setCountryCode(key);
                link.setUrl(entry.getValue());
                item.getBuyLinksDto().add(link);
            }
        }
    }

}
