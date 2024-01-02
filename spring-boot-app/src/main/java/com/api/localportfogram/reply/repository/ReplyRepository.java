package com.api.localportfogram.reply.repository;

import com.api.localportfogram.comment.entity.CommentEntity;
import com.api.localportfogram.reply.entity.ReplyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
    Page<ReplyEntity> findAllByComment(@Param("comment") CommentEntity commentEntity, Pageable pageable);

}
