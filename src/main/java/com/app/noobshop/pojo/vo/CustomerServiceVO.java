package com.app.noobshop.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceVO {

    private Long id;

    private String nickname;

    private String avatar;

    private Boolean online;
}
