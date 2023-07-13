package com.hello.security.board.service;

import com.github.pagehelper.Page;
import com.hello.security.board.domain.Comment;
import org.springframework.http.ResponseEntity;

public interface CommentService {
      //CRUD

      ResponseEntity<?> insertComment(Comment comment);

      Page<Comment> getCommentList(Long idx, int pageNo);

      ResponseEntity<?> updateComment(Comment comment);

      ResponseEntity<?> deleteComment(Long commentIdx);



}
