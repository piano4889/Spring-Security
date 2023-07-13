package com.hello.security.board.mapper;

import com.github.pagehelper.Page;
import com.hello.security.board.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

      int insertComment(Comment comment);

      Page<Comment> getCommentList(Long idx);

      int updateComment(Comment comment);

      int deleteComment(Long commentIdx);

      //test
}
