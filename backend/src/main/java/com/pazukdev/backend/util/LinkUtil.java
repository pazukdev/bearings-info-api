package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Link;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.service.ItemService;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.Set;

import static com.pazukdev.backend.entity.factory.LinkFactory.createLink;
import static com.pazukdev.backend.entity.factory.LinkFactory.createWebsiteLink;
import static com.pazukdev.backend.util.UserActionUtil.ActionType;
import static com.pazukdev.backend.util.UserActionUtil.processLinkAction;

public class LinkUtil {

    public static class LinkType {
        public static final String DRAWINGS = "drawings";
        public static final String IMG = "img";
        public static final String MANUAL = "manual";
        public static final String PARTS_CATALOG = "parts catalog";
        public static final String WEBSITE = "website";
        public static final String WIKI = "wiki";
    }

    public static void updateItemLinks(final Item target,
                                       final ItemView source,
                                       final UserEntity user,
                                       final ItemService service) {
        updateLink(source.getWikiLink(), LinkType.WIKI, target, user, service);
        updateLink(source.getManualLink(), LinkType.MANUAL, target, user, service);
        updateLink(source.getPartsCatalogLink(), LinkType.PARTS_CATALOG, target, user, service);
        updateLink(source.getDrawingsLink(), LinkType.DRAWINGS, target, user, service);

        final String websiteLink = source.getWebsiteLink();
        String websiteLang = source.getWebsiteLang();
        final Link websiteLinkUrl = getLink(LinkType.WEBSITE, target.getLinks());
        if (websiteLinkUrl != null) {
            if (SpecificStringUtil.isEmpty(websiteLink)) {
                target.getLinks().remove(websiteLinkUrl);
                processLinkAction(ActionType.DELETE, LinkType.WEBSITE, target, user, service);
            } else {
                if (websiteLinkUrl.getName() == null || !websiteLinkUrl.getName().equals(websiteLink)) {
                    websiteLinkUrl.setName(websiteLink);
                    processLinkAction(ActionType.UPDATE, LinkType.WEBSITE, target, user, service);
                }
                websiteLinkUrl.setLang(websiteLang);
            }
        } else {
            if (!SpecificStringUtil.isEmpty(websiteLink)) {
                final Link newWebsite = createWebsiteLink(websiteLink, websiteLang);
                target.getLinks().add(newWebsite);
                processLinkAction(ActionType.ADD, LinkType.WEBSITE, target, user, service);
            }
        }
    }

    private static void updateLink(final String linkUrl,
                                   final String linkType,
                                   final Item target,
                                   final UserEntity user,
                                   final ItemService service) {
        final Link link = getLink(linkType, target.getLinks());
        if (link != null) {
            if (SpecificStringUtil.isEmpty(linkUrl)) {
                target.getLinks().remove(link);
                processLinkAction(ActionType.DELETE, linkType, target, user, service);
            } else {
                if (link.getName() == null || !link.getName().equals(linkUrl)) {
                    link.setName(linkUrl);
                    processLinkAction(ActionType.UPDATE, linkType, target, user, service);
                }
            }
        } else {
            if (!SpecificStringUtil.isEmpty(linkUrl)) {
                final Link newLink = createLink(linkType, linkUrl);
                target.getLinks().add(newLink);
                processLinkAction(ActionType.ADD, linkType, target, user, service);
            }
        }
    }

    public static void setLinksToItemView(final ItemView target, final Item source) {
        final String defaultWebsiteLang = "all";
        target.setWebsiteLang(defaultWebsiteLang);

        for (final Link link : source.getLinks()) {
            final String linkType = link.getType();
            switch (linkType) {
                case LinkType.WIKI:
                    target.setWikiLink(link.getName());
                    break;
                case LinkType.MANUAL:
                    target.setManualLink(link.getName());
                    break;
                case LinkType.PARTS_CATALOG:
                    target.setPartsCatalogLink(link.getName());
                    break;
                case LinkType.DRAWINGS:
                    target.setDrawingsLink(link.getName());
                    break;
                case LinkType.WEBSITE:
                    target.setWebsiteLink(link.getName());
                    target.setWebsiteLang(link.getLang());
                    break;
            }

        }
    }

    public static void addLinksToItem(final Item target, final TransitiveItem source) {
        if (source.getWiki() != null) {
            target.getLinks().add(createLink(LinkType.WIKI, source.getWiki()));
        }
        if (source.getWebsite() != null) {
            target.getLinks().add(createWebsiteLink(source.getWebsite(), source.getWebsiteLang()));
        }
        if (source.getManual() != null) {
            target.getLinks().add(createLink(LinkType.MANUAL, source.getManual()));
        }
        if (source.getParts() != null) {
            target.getLinks().add(createLink(LinkType.PARTS_CATALOG, source.getParts()));
        }
        if (source.getDrawings() != null) {
            target.getLinks().add(createLink(LinkType.DRAWINGS, source.getDrawings()));
        }
    }

    public static String getLink(final String linkType, final Item item) {
        final Link link = LinkUtil.getLink(linkType, item.getLinks());
        return link != null ? link.getName() : null;
    }

    public static Link getLink(final String linkType, final Set<Link> itemLinks) {
        for (final Link link : itemLinks) {
            if (link != null && link.getType().equalsIgnoreCase(linkType)) {
                return link;
            }
        }
        return null;
    }

    public static boolean isUrl(final String s) {
        return UrlValidator.getInstance().isValid(s);
    }

}
