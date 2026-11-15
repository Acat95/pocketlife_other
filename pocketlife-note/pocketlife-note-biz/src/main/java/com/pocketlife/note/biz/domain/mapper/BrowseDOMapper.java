package com.pocketlife.note.biz.domain.mapper;

import com.pocketlife.note.biz.model.vo.NoteItemRspVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BrowseDOMapper {
    record Choice(Long id, String name) {}
    record Tag(String name) {}
    record Counts(long likeTotal, long collectTotal, long commentTotal) {}

    @Select("SELECT name FROM t_note_tag WHERE note_id = #{noteId} ORDER BY position")
    List<Tag> noteTags(@Param("noteId") long noteId);

    @Select("""
            SELECT like_total, collect_total, comment_total
            FROM t_note_count WHERE note_id = #{noteId}
            """)
    Counts noteCounts(@Param("noteId") long noteId);

    @Select("SELECT id, name FROM t_channel WHERE is_deleted = 0 ORDER BY id")
    List<Choice> channels();

    @Select("""
            SELECT id, name FROM t_topic
            WHERE is_deleted = 0 AND LOCATE(#{keyword}, name) > 0
            ORDER BY id LIMIT 12
            """)
    List<Choice> topics(String keyword);

    @Select("SELECT EXISTS(SELECT 1 FROM t_channel WHERE id = #{id} AND is_deleted = 0)")
    boolean hasChannel(Long id);

    String FILTER = """
            FROM t_note n
            WHERE n.status = 1 AND (n.visible = 0 OR (#{kind} = 1 AND n.creator_id = #{viewerId}))
              AND (#{channelId} = 0 OR n.channel_id = #{channelId} OR
                (n.channel_id IS NULL AND EXISTS(SELECT 1 FROM t_channel_topic_rel r
                  WHERE r.channel_id = #{channelId} AND r.topic_id = n.topic_id)))
              AND (#{kind} = 0 OR (#{kind} = 1 AND n.creator_id = #{userId})
                OR (#{kind} = 2 AND EXISTS(SELECT 1 FROM t_note_collection c
                  WHERE c.user_id = #{userId} AND c.note_id = n.id AND c.status = 1))
                OR (#{kind} = 3 AND EXISTS(SELECT 1 FROM t_note_like l
                  WHERE l.user_id = #{userId} AND l.note_id = n.id AND l.status = 1)))
            """;

    @Select("SELECT COUNT(*) " + FILTER)
    long count(@Param("kind") int kind, @Param("userId") Long userId, @Param("viewerId") Long viewerId, @Param("channelId") long channelId);

    @Select("""
            SELECT n.id AS noteId, n.type, SUBSTRING_INDEX(n.img_uris, ',', 1) AS cover,
                   n.video_uri AS videoUri, n.title, n.creator_id AS creatorId,
                   (SELECT u.nickname FROM t_user u WHERE u.id = n.creator_id) AS nickname,
                   (SELECT u.avatar FROM t_user u WHERE u.id = n.creator_id) AS avatar,
                   CAST(COALESCE((SELECT c.like_total FROM t_note_count c WHERE c.note_id = n.id), 0) AS CHAR) AS likeTotal,
                   EXISTS(SELECT 1 FROM t_note_like l
                     WHERE l.user_id = #{viewerId} AND l.note_id = n.id AND l.status = 1) AS isLiked
            """ + FILTER + " ORDER BY n.id DESC LIMIT 10 OFFSET #{offset}")
    List<NoteItemRspVO> notes(@Param("kind") int kind, @Param("userId") Long userId, @Param("viewerId") Long viewerId, @Param("channelId") long channelId, @Param("offset") long offset);
}


