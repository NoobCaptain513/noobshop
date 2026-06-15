package com.app.noobshop.pojo.dto;

import com.app.noobshop.pojo.emums.BannerStatus;
import lombok.Data;

@Data
public class BannerStatusDTO {

    String id;

    BannerStatus status;
}
