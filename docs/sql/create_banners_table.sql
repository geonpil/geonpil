-- 메인/서브 배너 관리용 테이블
-- type: 'main' = 메인 배너 1개, 'sub' = 서브 배너 (여러 개, display_order로 정렬)

CREATE TABLE banners (
    banners_id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type            VARCHAR(10)  NOT NULL COMMENT 'main | sub',
    image_url       VARCHAR(500) NOT NULL COMMENT '이미지 경로 또는 URL',
    link_url        VARCHAR(1000)         DEFAULT NULL COMMENT '클릭 시 이동 URL',
    alt_text        VARCHAR(200)          DEFAULT NULL COMMENT '대체 텍스트(접근성)',
    display_order   INT          NOT NULL DEFAULT 0 COMMENT '노출 순서(작을수록 앞)',
    is_visible      TINYINT   NOT NULL DEFAULT 1 COMMENT '0: 비노출, 1: 노출',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_banners_type_order (type, display_order),
    INDEX idx_banners_visible (is_visible)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='메인/서브 배너';
