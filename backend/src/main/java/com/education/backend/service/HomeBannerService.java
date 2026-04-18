package com.education.backend.service;

import com.education.backend.entity.HomeBanner;
import java.util.List;

public interface HomeBannerService {
    HomeBanner saveBanner(HomeBanner banner);

    void deleteBanner(Integer id);

    List<HomeBanner> getBanners();
}
