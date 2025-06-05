package com.geonpil.validator;

public class NicknameValidator {
    private static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{2,12}$";

    public static boolean isValid(String nickname) {
        return nickname != null && nickname.matches(NICKNAME_REGEX);
    }
}
