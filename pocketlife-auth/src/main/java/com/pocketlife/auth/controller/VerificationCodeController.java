package com.pocketlife.auth.controller;

import com.pocketlife.framework.common.response.Response;
import com.pocketlife.auth.model.vo.verificationcode.SendVerificationCodeReqVO;
import com.pocketlife.auth.service.VerificationCodeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 犬小哈
 * @date: 2024/4/4 13:22
 * @version: v1.0.0
 * @description: 验证码
 **/
@RestController
@Slf4j
public class VerificationCodeController {

    @Resource
    private VerificationCodeService verificationCodeService;

    @PostMapping("/verification/code/send")
    public Response<?> send(@Validated @RequestBody SendVerificationCodeReqVO sendVerificationCodeReqVO) {
        return verificationCodeService.send(sendVerificationCodeReqVO);
    }

}
