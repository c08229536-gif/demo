package com.education.backend.repository;
import com.education.backend.entity.HomeBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface HomeBannerRepository extends JpaRepository<HomeBanner, Integer> {
    List<HomeBanner> findByIsActiveOrderBySortOrderAsc(Integer isActive);
}