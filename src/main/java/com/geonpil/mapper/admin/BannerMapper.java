package com.geonpil.mapper.admin;


import com.geonpil.domain.admin.Banners;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BannerMapper {
    void insertBanner(Banners banners);

    void updateBanner(Banners banners);

    void deleteBanner(Long bannerId);

    List<Banners> findAllBanners();

    Banners findBannerById(Long id);

    int countVisibleByType(String type);
}
