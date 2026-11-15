package com.pocketlife.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.pocketlife.framework.common.exception.BizException;
import com.pocketlife.framework.common.response.Response;
import com.pocketlife.auth.constant.RedisKeyConstants;
import com.pocketlife.auth.enums.ResponseCodeEnum;
import com.pocketlife.auth.model.vo.verificationcode.SendVerificationCodeReqVO;
import com.pocketlife.auth.service.VerificationCodeService;
import com.pocketlife.auth.sms.AliyunSmsHelper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author: 犬小哈
 * @date: 2024/4/7 15:41
 * @version: v1.0.0
 * @description: TODO
 **/
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private ObjectProvider<AliyunSmsHelper> smsHelpers;

    /**
     * 发送短信验证码
     *
     * @param sendVerificationCodeReqVO
     * @return
     */
    @Override
    public Response<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO) {
        AliyunSmsHelper smsHelper = smsHelpers.getIfAvailable();
        if (smsHelper == null) {
            return Response.fail("短信发送尚未配置");
        }

        // 手机号
        String phone = sendVerificationCodeReqVO.getPhone();

        // 构建验证码 redis key
        String key = RedisKeyConstants.buildVerificationCodeKey(phone);

        // 判断是否已发送验证码
        boolean isSent = redisTemplate.hasKey(key);
        if (isSent) {
            // 若之前发送的验证码未过期，则提示发送频繁
            throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_SEND_FREQUENTLY);
        }

        // 生成 6 位随机数字验证码
        String verificationCode = RandomUtil.randomNumbers(6);

        // 调用第三方短信发送服务
        threadPoolTaskExecutor.submit(() -> {
            String signName = "阿里云短信测试";
            String templateCode = "SMS_154950909";
            String templateParam = String.format("{\"code\":\"%s\"}", verificationCode);
            smsHelper.sendMessage(signName, templateCode, phone, templateParam);
        });

        // 存储验证码到 redis, 并设置过期时间为 3 分钟
        redisTemplate.opsForValue().set(key, verificationCode, 3, TimeUnit.MINUTES);

        return Response.success();
    }
}
