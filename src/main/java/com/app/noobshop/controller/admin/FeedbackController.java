package com.app.noobshop.controller.admin;


import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.FeedbackDTO;
import com.app.noobshop.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "反馈管理")
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;

    @PostMapping("/feedback/add")
    @Operation(summary = "用户提交反馈")
    public Result<Object> addFeedback(@RequestBody @NotNull FeedbackDTO feedbackDTO){
       return feedbackService.addressService(feedbackDTO);

    }

}
