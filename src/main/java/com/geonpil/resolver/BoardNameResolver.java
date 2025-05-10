package com.geonpil.resolver;

public class BoardNameResolver {
    public static String resolve(int boardCode) {
        return switch (boardCode) {
            case 1 -> "커뮤니티 게시판";
            case 2 -> "도전건필 게시판";
            case 3 -> "공모전 게시판";
            default -> "알 수 없는 게시판";
        };
    }
}