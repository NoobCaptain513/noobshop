package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.BannerDTO;
import com.app.noobshop.pojo.dto.BannerSortDTO;
import com.app.noobshop.pojo.dto.BannerStatusDTO;
import com.app.noobshop.pojo.entity.Banner;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 20589
 * @description 针对表【banner(首页轮播图表)】的数据库操作Service
 * @createDate 2025-12-26 20:32:21
 */
public interface BannerService extends IService<Banner> {
    Result<List<Banner>> getBannerList();

    Result<List<Banner>> getBannerListAdmin(Integer pageNum,Integer pageSize);

    Result addBanner(BannerDTO bannerDTO);

    Result updateBanner(BannerDTO bannerDTO);

    Result deleteBanner(Long id);

    Result updateSort(BannerSortDTO bannerSortDTO);

    Result updateStatus(BannerStatusDTO bannerStatusDTO);
}

