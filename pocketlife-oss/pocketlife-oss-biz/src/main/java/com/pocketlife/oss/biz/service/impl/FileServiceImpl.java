package com.pocketlife.oss.biz.service.impl;

import com.pocketlife.framework.common.response.Response;
import com.pocketlife.oss.biz.service.FileService;
import com.pocketlife.oss.biz.strategy.FileStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: 犬小哈
 * @date: 2024/4/11 17:12
 * @version: v1.0.0
 * @description: TODO
 **/
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Resource
    private FileStrategy fileStrategy;

    private static final String BUCKET_NAME = "pocketlife";

    @Override
    public Response<?> uploadFile(MultipartFile file) {
        // 上传文件
        String url = fileStrategy.uploadFile(file, BUCKET_NAME);

        return Response.success(url);
    }
}
