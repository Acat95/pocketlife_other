package com.pocketlife.oss.biz.controller;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRange;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MediaController {
    private static final String BUCKET = "pocketlife";
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "jpg", "image/jpeg", "jpeg", "image/jpeg", "png", "image/png", "webp", "image/webp",
            "gif", "image/gif", "avif", "image/avif", "mp4", "video/mp4", "webm", "video/webm",
            "mov", "video/quicktime");
    private final MinioClient minio;

    @GetMapping("/file/media/{object}")
    public void read(@PathVariable String object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!object.matches("[a-f0-9]{32}\\.[a-zA-Z0-9]{2,5}")) {
            response.sendError(404);
            return;
        }
        String type = CONTENT_TYPES.get(object.substring(33).toLowerCase(Locale.ROOT));
        if (type == null) {
            response.sendError(404);
            return;
        }
        long size;
        try {
            size = minio.statObject(StatObjectArgs.builder().bucket(BUCKET).object(object).build()).size();
        } catch (ErrorResponseException ex) {
            if (List.of("NoSuchKey", "NoSuchObject").contains(ex.errorResponse().code())) {
                response.sendError(404);
                return;
            }
            throw ex;
        }

        long start = 0;
        long length = size;
        String range = request.getHeader("Range");
        if (range != null) {
            try {
                List<HttpRange> ranges = HttpRange.parseRanges(range);
                if (ranges.size() != 1) throw new IllegalArgumentException();
                HttpRange bytes = ranges.get(0);
                start = bytes.getRangeStart(size);
                long end = bytes.getRangeEnd(size);
                if (start >= size || start > end) throw new IllegalArgumentException();
                length = end - start + 1;
                response.setStatus(206);
                response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + size);
            } catch (IllegalArgumentException ex) {
                response.setHeader("Content-Range", "bytes */" + size);
                response.sendError(416);
                return;
            }
        }
        response.setContentType(type);
        response.setContentLengthLong(length);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Cache-Control", "public, max-age=3600");
        if (!"HEAD".equals(request.getMethod()) && length > 0) {
            try (var stream = minio.getObject(GetObjectArgs.builder().bucket(BUCKET).object(object)
                    .offset(start).length(length).build())) {
                stream.transferTo(response.getOutputStream());
            }
        }
    }
}
