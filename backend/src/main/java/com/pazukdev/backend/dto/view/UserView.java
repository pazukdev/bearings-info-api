package com.pazukdev.backend.dto.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserView extends AbstractView {

    private String role;
    private String rating;
    private String email;
    private String country;
    private String oldPassword;
    private String newPassword;
    private String repeatedNewPassword;
    private String status;
    private String currentUserName;

}
