package com.pazukdev.backend.entity.factory;

import com.pazukdev.backend.entity.Link;

/**
 * @author Siarhei Sviarkaltsau
 */
public class LinkFactory {

    public static class LinkType {
        public static final String BUY = "buy";
        public static final String DRAWINGS = "drawings";
        public static final String IMG = "img";
        public static final String MANUAL = "manual";
        public static final String PARTS_CATALOG = "parts catalog";
        public static final String WEBSITE = "website";
        public static final String WIKI = "wiki";
    }

    public static Link createLink(final String linkType,
                                  final String url,
                                  final String countryCode) {
        final Link linkObj = new Link();
        linkObj.setType(linkType);
        linkObj.setUrl(url);
        linkObj.setCountryCode(countryCode == null ? "-" : countryCode);
        return linkObj;
    }

}
