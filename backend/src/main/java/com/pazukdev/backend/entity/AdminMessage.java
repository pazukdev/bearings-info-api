package com.pazukdev.backend.entity;

import com.pazukdev.backend.repository.AdminMessageRepository;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Siarhei Sviarkaltsau
 */
@Data
@Entity
@Table(name = "admin_message")
public class AdminMessage implements Serializable {

    private final static long serialVersionUID = 12343L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String text;
    @Column(name = "link_text")
    private String linkText;
    private String url;

    public static AdminMessage getMessage(final AdminMessageRepository repository) {
        return repository.findById(1L).orElse(new AdminMessage());
    }

    public static void save(final AdminMessage message, final AdminMessageRepository repository) {
        final AdminMessage oldMessage = getMessage(repository);
        oldMessage.setText(message.getText());
        oldMessage.setLinkText(message.getLinkText());
        oldMessage.setUrl(message.getUrl());
        repository.save(oldMessage);
    }

}
