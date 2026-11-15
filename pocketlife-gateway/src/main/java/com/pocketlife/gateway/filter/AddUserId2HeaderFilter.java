package com.pocketlife.gateway.filter;

import com.pocketlife.framework.common.constant.GlobalConstants;
import com.pocketlife.gateway.constant.RedisKeyConstants;
import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class AddUserId2HeaderFilter implements GlobalFilter {
    private static final String BEARER = "Bearer ";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var clean = exchange.mutate().request(request ->
                request.headers(headers -> headers.remove(GlobalConstants.USER_ID))).build();
        String authorization = clean.getRequest().getHeaders().getFirst("Authorization");
        if (authorization == null || !authorization.startsWith(BEARER)) {
            return chain.filter(clean);
        }
        String key = RedisKeyConstants.SA_TOKEN_TOKEN_KEY_PREFIX + authorization.substring(BEARER.length());
        return Mono.fromCallable(() -> redisTemplate.opsForValue().get(key))
                .subscribeOn(Schedulers.boundedElastic())
                .map(String::valueOf)
                .filter(id -> id.matches("[1-9][0-9]{0,18}"))
                .map(id -> clean.mutate().request(request ->
                        request.headers(headers -> headers.set(GlobalConstants.USER_ID, id))).build())
                .defaultIfEmpty(clean)
                .flatMap(chain::filter);
    }
}
