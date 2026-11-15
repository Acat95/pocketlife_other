package com.pocketlife.note.biz.controller;

import com.pocketlife.framework.biz.context.holder.LoginUserContextHolder;
import com.pocketlife.framework.common.response.PageResponse;
import com.pocketlife.framework.common.response.Response;
import com.pocketlife.note.biz.domain.mapper.BrowseDOMapper;
import com.pocketlife.note.biz.model.vo.NoteItemRspVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class BrowseController {
    private final BrowseDOMapper mapper;

    public record TopicRequest(@Size(max = 80) String keyword) {}
    public record DiscoverRequest(@PositiveOrZero Long channelId, @Min(1) @Max(10000) Integer pageNo) {}
    public record ProfileRequest(@Min(1) @Max(3) int type, @NotNull @Positive Long userId,
                                 @Min(1) @Max(10000) Integer pageNo) {}

    @PostMapping("/channel/list")
    public Response<List<BrowseDOMapper.Choice>> channels() {
        return Response.success(mapper.channels());
    }

    @PostMapping("/topic/list")
    public Response<List<BrowseDOMapper.Choice>> topics(@Valid @RequestBody TopicRequest request) {
        String keyword = request.keyword() == null ? "" : request.keyword().strip();
        return Response.success(mapper.topics(keyword));
    }

    @PostMapping("/discover/note/list")
    public PageResponse<NoteItemRspVO> discover(@Valid @RequestBody DiscoverRequest request) {
        return page(0, null, request.channelId() == null ? 0 : request.channelId(), request.pageNo());
    }

    @PostMapping("/profile/note/list")
    public Response<List<NoteItemRspVO>> profile(@Valid @RequestBody ProfileRequest request) {
        if (request.type() != 1 && !Objects.equals(request.userId(), LoginUserContextHolder.getUserId())) {
            return Response.fail("只能查看自己的收藏和点赞");
        }
        return page(request.type(), request.userId(), 0, request.pageNo());
    }

    private PageResponse<NoteItemRspVO> page(int kind, Long userId, long channelId, Integer requestedPage) {
        int page = requestedPage == null ? 1 : requestedPage;
        Long viewerId = LoginUserContextHolder.getUserId();
        long total = mapper.count(kind, userId, viewerId, channelId);
        long offset = (page - 1L) * 10;
        return PageResponse.success(offset < total ? mapper.notes(kind, userId, viewerId, channelId, offset) : List.of(),
                page, total);
    }
}
