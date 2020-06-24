package com.pazukdev.backend.util;

import com.pazukdev.backend.converter.LinkConverter;
import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.*;

import javax.annotation.Nullable;
import java.util.*;

import static com.pazukdev.backend.converter.LinkConverter.convert;
import static com.pazukdev.backend.entity.factory.LinkFactory.LinkType;
import static com.pazukdev.backend.entity.factory.LinkFactory.createLink;
import static com.pazukdev.backend.util.SpecificStringUtil.isEmpty;
import static com.pazukdev.backend.util.UserActionUtil.ActionType;
import static com.pazukdev.backend.util.UserActionUtil.createAction;

/**
 * @author Siarhei Sviarkaltsau
 */
public class LinkUtil {

    public static void updateLinks(final Item target,
                                   final ItemView source,
                                   final UserEntity user,
                                   final List<UserAction> actions) {

        final Map<String, String> linksData = new HashMap<>();
        linksData.put(LinkType.WIKI, source.getWikiLink());
        linksData.put(LinkType.MANUAL, source.getManualLink());
        linksData.put(LinkType.PARTS_CATALOG, source.getPartsCatalogLink());
        linksData.put(LinkType.DRAWINGS, source.getDrawingsLink());
        linksData.put(LinkType.WEBSITE, source.getWebsiteLink());
        linksData.put(LinkType.IMG, source.getImg());

        linksData.forEach((key, value) -> updateLink(key, value, target, user, actions));

        final Set<Link> links = convert(source.getBuyLinks());
        for (final Link link : links) {
            String actionDetails = "";
            if (link.getId() == null) {
                actions.add(createAction(ActionType.ADD, actionDetails, target, link, user, false));
            } else {
                for (final Link oldLink : target.getBuyLinks()) {
                    if (link.getId().equals(oldLink.getId())) {
                        final String newUrl = link.getUrl();
                        final String oldUrl = oldLink.getUrl();
                        final String newCountryCode = link.getCountryCode();
                        final String oldCountryCode = oldLink.getCountryCode();
                        final boolean urlChanged = !Objects.equals(newUrl, oldUrl);
                        final boolean countryChanged = !Objects.equals(newCountryCode, oldCountryCode);
                        if (urlChanged) {
                            actionDetails += "new url=" + newUrl;
                        }
                        if (countryChanged) {
                            actionDetails += "new countryCode=" + newCountryCode;
                        }
                        if (urlChanged || countryChanged) {
                            actions.add(createAction(ActionType.UPDATE, actionDetails, target, oldLink, user, false));
                        }
                    }
                }
            }
        }

        final List<Link> linksToDelete = new ArrayList<>();

        for (final Link oldLink : target.getBuyLinks()) {
            boolean delete = true;
            for (final Link newLink : links) {
                if (oldLink.getId().equals(newLink.getId())) {
                    delete = false;
                }
            }
            if (delete) {
                linksToDelete.add(oldLink);
            }
        }


        target.getBuyLinks().clear();
        target.getBuyLinks().addAll(links);

        for (final Link link : linksToDelete) {
            actions.add(createAction(ActionType.DELETE, "", target, link, user, false));
        }
    }

    public static void addLinksToItem(final Item target,
                                      final TransitiveItem source,
                                      final List<UserAction> actions) {
        final UserEntity user = null;

        final Map<String, String> linksData = new HashMap<>();
        linksData.put(LinkType.WIKI, source.getWiki());
        linksData.put(LinkType.MANUAL, source.getManual());
        linksData.put(LinkType.PARTS_CATALOG, source.getParts());
        linksData.put(LinkType.DRAWINGS, source.getDrawings());
        linksData.put(LinkType.WEBSITE, source.getWebsite());

        linksData.forEach((key, value) -> {
            if (value != null) {
                updateLink(key, value, target, user, actions);
            }
        });

        target.getBuyLinks().clear();
        target.getBuyLinks().addAll(LinkConverter.convert(new ArrayList<>(source.getBuyLinksDto())));
    }

    public static void updateLink(final String linkType,
                                  final String newUrl,
                                  final Item target,
                                  @Nullable final UserEntity user,
                                  @Nullable final List<UserAction> actions) {

        final Link link = getLink(linkType, target.getLinks());
        final boolean addAction = actions != null;
        String actionDetails = "";

        if (link != null) {
            if (isEmpty(newUrl)) {
                target.getLinks().remove(link);
                if (addAction) {
                    actions.add(createAction(ActionType.DELETE, actionDetails, target, link, user, false));
                }
            } else {
                final String oldUrl = link.getUrl();
                final boolean urlChanged = !Objects.equals(newUrl, oldUrl);
                if (urlChanged) {
                    actionDetails += "new url: " + newUrl;
                    if (addAction) {
                        actions.add(createAction(ActionType.UPDATE, actionDetails, target, link, user, false));
                    }
                    link.setUrl(newUrl);
                }
            }
        } else {
            if (!isEmpty(newUrl)) {
                final Link newLink = createLink(linkType, newUrl, "-");
                target.getLinks().add(newLink);
                if (addAction) {
                    actions.add(createAction(ActionType.ADD, actionDetails, target, newLink, user, false));
                }
            }
        }

    }

    public static void setLinksToItemView(final ItemView target, final Item source) {
        target.setBuyLinks(convert(source.getBuyLinks()));
        for (final Link link : source.getLinks()) {
            final String linkType = link.getType();
            switch (linkType) {
                case LinkType.WIKI:
                    target.setWikiLink(link.getUrl());
                    break;
                case LinkType.WEBSITE:
                    target.setWebsiteLink(link.getUrl());
                    break;
                case LinkType.MANUAL:
                    target.setManualLink(link.getUrl());
                    break;
                case LinkType.PARTS_CATALOG:
                    target.setPartsCatalogLink(link.getUrl());
                    break;
                case LinkType.DRAWINGS:
                    target.setDrawingsLink(link.getUrl());
                    break;
            }
        }
    }

    public static String getLink(final String linkType, final Item item) {
        final Link link = LinkUtil.getLink(linkType, item.getLinks());
        return link != null ? link.getUrl() : null;
    }

    public static Link getLink(final String linkType, final Set<Link> itemLinks) {
        for (final Link link : itemLinks) {
            if (link == null) {
                continue;
            }
            if (link.getType() != null && link.getType().equalsIgnoreCase(linkType)) {
                return link;
            }
        }
        return null;
    }

}
