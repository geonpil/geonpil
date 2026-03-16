package com.geonpil.service.admin;

import com.geonpil.domain.admin.Banners;
import com.geonpil.mapper.admin.BannerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerMapper bannerMapper;

    @Transactional
    public void saveBanner(Banners banners) {
        bannerMapper.insertBanner(banners);
    }

    public List<Banners> getAllBanners() {
        return bannerMapper.findAllBanners();
    }

    public Banners getBannerById(Long id) {
        return bannerMapper.findBannerById(id);
    }

    public void updateBanner(Banners banners) {
        bannerMapper.updateBanner(banners);
    }

    public void deleteBanner(Long bannerId) {
        bannerMapper.deleteBanner(bannerId);
    }

    public void validateVisibleLimit(Banners banner) {
        if (!banner.isVisible())
            return;

        int limit = banner.getType().equals("main") ? 1 : 4;
        int currentCount = bannerMapper.countVisibleByType(banner.getType());

        if (banner.getId() != null) {
            Banners before = bannerMapper.findBannerById(banner.getId());
            if (before != null && before.isVisible()) {
                currentCount--;
            }
        }

        if (currentCount >= limit) {
            throw new IllegalStateException(
                    banner.getType().equals("main")
                            ? "메인 배너는 1개만 노출할 수 있습니다."
                            : "서브 배너는 4개까지 노출할 수 있습니다.");
        }

    }

}