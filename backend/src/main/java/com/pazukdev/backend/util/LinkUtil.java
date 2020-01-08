package com.pazukdev.backend.util;

import com.pazukdev.backend.dto.view.ItemView;
import com.pazukdev.backend.entity.Item;
import com.pazukdev.backend.entity.Link;
import com.pazukdev.backend.entity.TransitiveItem;
import com.pazukdev.backend.entity.factory.LinkFactory;

public class LinkUtil {

    public static void updateItemLinks(final Item target, final ItemView source) {
        final String wikiLink = source.getWikiLink();
        final Link wiki = getLink("wiki", target);
        if (wiki != null) {
            if (SpecificStringUtil.isEmpty(wikiLink)) {
                target.getLinks().remove(wiki);
            } else {
                wiki.setName(wikiLink);
            }
        } else {
            if (!SpecificStringUtil.isEmpty(wikiLink)) {
                final Link newWiki = LinkFactory.createWikiLink(wikiLink);
                target.getLinks().add(newWiki);
            }
        }

        final String websiteLink = source.getWebsiteLink();
        String websiteLang = source.getWebsiteLang();
        final Link website = getLink("website", target);
        if (website != null) {
            if (SpecificStringUtil.isEmpty(websiteLink)) {
                target.getLinks().remove(website);
            } else {
                website.setName(websiteLink);
                website.setLang(websiteLang);
            }
        } else {
            if (!SpecificStringUtil.isEmpty(websiteLink)) {
                final Link newWebsite = LinkFactory.createWebsiteLink(websiteLink, websiteLang);
                target.getLinks().add(newWebsite);
            }
        }
    }

    public static void setLinksToItemView(final ItemView target, final Item source) {
        final String defaultWebsiteLang = "all";
        target.setWebsiteLang(defaultWebsiteLang);

        for (final Link link : source.getLinks()) {
            final String linkType = link.getType();
            if (linkType.equalsIgnoreCase("wiki")) {
                target.setWikiLink(link.getName());
            }
            if (linkType.equalsIgnoreCase("website")) {
                target.setWebsiteLink(link.getName());
                target.setWebsiteLang(link.getLang());
            }

        }
    }

    public static void addLinksToItem(final Item target, final TransitiveItem source) {
        if (source.getWiki() != null) {
            target.getLinks().add(LinkFactory.createWikiLink(source.getWiki()));
        }
        if (source.getWebsite() != null) {
            target.getLinks().add(LinkFactory.createWebsiteLink(source.getWebsite(), source.getWebsiteLang()));
        }
    }

    private static Link getLink(final String linkType, final Item item) {
        for (final Link link : item.getLinks()) {
            if (link.getType().equalsIgnoreCase(linkType)) {
                return link;
            }
        }
        return null;
    }

}
