package com.hello.security.board.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hello.security.board.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import com.hello.security.board.domain.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

      private final CommentMapper commentMapper;

      @Override
      public Page<Comment> getCommentList(Long idx, int pageNum) {
            /*
            Long idx = 해당 게시글의 댓글 조회
            int pageNum = 페이지 번호
            pageSize = 한 페이지에 보여줄 갯수
             */
            PageHelper.startPage(pageNum, 5);
            return commentMapper.getCommentList(idx);
      }

      @Override
      public ResponseEntity<?> insertComment(Comment comment) {
            int result = commentMapper.insertComment(comment);
            if (result == 1) {
                  return ResponseEntity.ok("Insert Success");
            } else{
                  return ResponseEntity.ok("Fail to Insert");
            }
      }

      @Override
      public ResponseEntity<?> updateComment(Comment comment) {
            int result = commentMapper.updateComment(comment);
            if (result == 1) {
                  return ResponseEntity.ok("Update Success");
            } else{
                  return ResponseEntity.ok("Fail to Insert");
            }
      }

      @Override
      public ResponseEntity<?> deleteComment(Long commentIdx) {
            int result = commentMapper.deleteComment(commentIdx);
            if (result == 1) {
                  return ResponseEntity.ok("Delete Success");
            } else{
                  return ResponseEntity.ok("Fail to Delete");
            }
      }

}
