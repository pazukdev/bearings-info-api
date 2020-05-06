package com.pazukdev.backend.converter;

import com.pazukdev.backend.dto.LinkDto;
import com.pazukdev.backend.entity.Link;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pazukdev.backend.entity.factory.LinkFactory.LinkType;

/**
 * @author Siarhei Sviarkaltsau
 */
public class LinkConverter {

    public static LinkDto convert(final Link link) {
        final LinkDto dto = new LinkDto();
        dto.setId(link.getId());
        dto.setUrl(link.getUrl());
        dto.setCountryCode(link.getCountryCode());
        return dto;
    }

    public static Link convert(final LinkDto dto) {
        final Link link = new Link();
        link.setId(dto.getId());
        link.setUrl(dto.getUrl());
        link.setCountryCode(dto.getCountryCode());
        link.setType(LinkType.BUY);
        return link;
    }

    public static Set<Link> convert(final List<LinkDto> dtos) {
        final Set<Link> links = new HashSet<>();
        for (final LinkDto dto : dtos) {
            links.add(convert(dto));
        }
        return links;
    }

    public static List<LinkDto> convert(final Set<Link> links) {
        final List<LinkDto> dtos = new ArrayList<>();
        for (final Link link : links) {
            dtos.add(convert(link));
        }
        return dtos;
    }

}
