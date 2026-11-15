package com.pocketlife.search.biz.service;

import com.pocketlife.framework.common.response.PageResponse;
import com.pocketlife.framework.common.response.Response;
import com.pocketlife.dto.RebuildNoteDocumentReqDTO;
import com.pocketlife.search.biz.model.vo.SearchNoteReqVO;
import com.pocketlife.search.biz.model.vo.SearchNoteRspVO;

/**
 * @author: 犬小哈
 * @date: 2024/4/7 15:41
 * @version: v1.0.0
 * @description: 笔记搜索业务
 **/
public interface NoteService {

    /**
     * 搜索笔记
     * @param searchNoteReqVO
     * @return
     */
    PageResponse<SearchNoteRspVO> searchNote(SearchNoteReqVO searchNoteReqVO);

    /**
     * 重建笔记文档
     * @param rebuildNoteDocumentReqDTO
     * @return
     */
    Response<Long> rebuildDocument(RebuildNoteDocumentReqDTO rebuildNoteDocumentReqDTO);
}
