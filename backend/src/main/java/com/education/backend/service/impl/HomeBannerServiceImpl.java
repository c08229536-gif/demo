package com.education.backend.service.impl;

import com.education.backend.entity.HomeBanner;
import com.education.backend.repository.HomeBannerRepository;
import com.education.backend.service.HomeBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HomeBannerServiceImpl implements HomeBannerService {

    @Autowired
    private HomeBannerRepository bannerRepository;

    @Override
    @Transactional
    public HomeBanner saveBanner(HomeBanner banner) {
        return bannerRepository.save(banner);
    }

    @Override
    @Transactional
    public void deleteBanner(Integer id) {
        bannerRepository.deleteById(id);
    }

    @Override
    public List<HomeBanner> getBanners() {
        return bannerRepository.findAll();
    }
}
