package com.pazukdev.backend.entity.factory;

import com.pazukdev.backend.config.ContextData;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.service.TransitiveItemService;
import com.pazukdev.backend.tablemodel.TableRow;
import com.pazukdev.backend.util.FileUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Component
public class TransitiveItemFactory extends AbstractEntityFactory<TransitiveItem> {

    private final TransitiveItemService service;

    @Override
    protected String[] getCSVFilesPaths() {
        return FileUtil.getCSVFilesPaths();
    }

    @Override
    public TransitiveItem createEntity() {
        return new TransitiveItem();
    }

    @Override
    protected void applyCharacteristics(TransitiveItem item, TableRow tableRow) {
        super.applyCharacteristics(item, tableRow);

        applyCategory(item, tableRow);
        applyImage(item, tableRow);
        applyDescription(item, tableRow);
        applyReplacers(item, tableRow);
        applyLinks(item, tableRow);
    }

    private void applyCategory(final TransitiveItem item, final TableRow tableRow) {
        final String category = tableRow.getData().get("Category");
        item.setCategory(category);
    }

    private void applyImage(final TransitiveItem item, final TableRow tableRow) {
        item.setImage(tableRow.getData().get("Image"));
    }

    private void applyDescription(final TransitiveItem item, final TableRow tableRow) {
        String description = "";
        for (final Map.Entry<String, String> entry : tableRow.getData().entrySet()) {
            final String key = entry.getKey();
            if (ContextData.isDescriptionIgnored(key)) {
                continue;
            }

            final String value = entry.getValue();

            if (value.contains("; ")) {
                int count = 1;
                for (final String subValue : Arrays.asList(value.split("; "))) {
                    description = description + key + " " + count++ + ":" + subValue + ";;";
                }
            } else {
                description = description + key + ":" + value + ";;";
            }
        }
        item.setDescription(description);
    }

    private void applyReplacers(final TransitiveItem item, final TableRow tableRow) {
        final String replacer = tableRow.getData().get("Replacer");
        item.setReplacer(replacer != null ? replacer : "-");
    }

    private void applyLinks(final TransitiveItem item, final TableRow tableRow) {
        item.setWiki(tableRow.getData().get("Wiki"));
        item.setWebsite(tableRow.getData().get("Website"));
        item.setWebsiteLang(tableRow.getData().get("Website lang"));
    }

}
