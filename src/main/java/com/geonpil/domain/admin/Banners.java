package com.geonpil.domain.admin;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class Banners {
    private Long id;
    private String type;
    private String imageUrl;
    private String linkUrl;
    private String altText;
    private int displayOrder;
    private boolean isVisible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
