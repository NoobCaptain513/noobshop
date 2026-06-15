package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.FeedbackDTO;
import com.app.noobshop.pojo.entity.Feedback;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotNull;

public interface FeedbackService extends IService<Feedback> {
    Result<Object> addressService( @NotNull FeedbackDTO feedbackDTO);
}

