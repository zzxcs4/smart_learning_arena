package com.zzx.exam.service;

import com.zzx.exam.entity.Banner;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 轮播图服务接口
 */
public interface BannerService extends IService<Banner> {

    String uploadImage(MultipartFile file);

    void addBanner(Banner banner);

    void updateBanner(Banner banner);
} 