package com.geonpil.dto.user;


import lombok.Data;

@Data
public class PasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
