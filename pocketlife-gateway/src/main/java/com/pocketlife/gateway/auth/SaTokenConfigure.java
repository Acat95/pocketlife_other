package com.pocketlife.gateway.auth;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: 犬小哈
 * @date: 2024/6/13 14:48
 * @version: v1.0.0
 * @description: [Sa-Token 权限认证] 配置类
 **/
@Configuration
@Slf4j
public class SaTokenConfigure {
    // 注册 Sa-Token全局过滤器
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    log.info("==================> SaReactorFilter, Path: {}", SaHolder.getRequest().getRequestPath());
                    // 登录校验
                    SaRouter.match("/**") // 拦截所有路由
                            .notMatch("/auth/login") // 排除登录接口
                            .notMatch("/auth/verification/code/send") // 排除验证码发送接口
                            .notMatch("/user/user/profile") // 排除用户主页查看
                            .notMatch("/note/channel/list")
                            .notMatch("/note/topic/list")
                            .notMatch("/note/discover/note/list")
                            .notMatch("/note/profile/note/list")
                            .notMatch("/note/note/detail")
                            .notMatch("/comment/comment/list")
                            .notMatch("/comment/comment/child/list")
                            .notMatch("/search/search/note")
                            .notMatch("/search/search/user")
                            .notMatch("/oss/file/media/**")
                            .check(r -> StpUtil.checkLogin()) // 校验是否登录
                    ;
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    // 手动抛出异常，抛给全局异常处理器
                    if (e instanceof NotLoginException) {
                        throw new NotLoginException(e.getMessage(), null, null);
                    } else if (e instanceof NotPermissionException || e instanceof NotRoleException) {
                        throw new NotPermissionException(e.getMessage());
                    } else {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                ;
    }
}

