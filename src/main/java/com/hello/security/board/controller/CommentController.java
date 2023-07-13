package com.hello.security.board.controller;

import com.hello.security.board.domain.Comment;
import com.hello.security.board.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Controller
@Log4j2
@RestController
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "댓글 API")
public class CommentController {

    private final CommentService commentService;

   @GetMapping(value = "/comments/{boardIdx}/{pageNum}")
   @Operation(summary = "getCommentList", description = "댓글 리스트 불러오기")
    public ResponseEntity<?> getCommentList(
            @PathVariable("boardIdx") Long boardIdx,
            @PathVariable("pageNum") int pageNum) {

        //header 설정 (Json,UTF-8 인코딩)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return new ResponseEntity<>(commentService.getCommentList(boardIdx, pageNum).toPageInfo(), headers, HttpStatus.OK);
    }

    //댓글 작성
    @PostMapping(value = "/comment/{boardIdx}")
    @Operation(summary = "insertComment", description = "댓글 작성")
    public ResponseEntity<?> insertComment(
            @PathVariable("boardIdx") String boardIdx,
            @RequestBody Comment vo) {

        vo.setIdx(Long.parseLong(boardIdx));
        return commentService.insertComment(vo);
    }

    //댓글 수정
    @PatchMapping(value = "/comment/{commentIdx}")
    @Operation(summary = "updateComment", description = "댓글 수정")
    public ResponseEntity<?> updateComment(
            @PathVariable("boardIdx") String idx,
            @PathVariable("commentIdx") String commentIdx,
            @RequestBody Comment vo) {
        vo.setCommentIdx(Long.parseLong(commentIdx));
        return commentService.updateComment(vo);
    }
    //TODO 컨트롤러 분리

    //댓글 삭제
    @DeleteMapping(value = "/comment/{commentIdx}")
    @Operation(summary = "deleteComment", description = "댓글 삭제")
    public ResponseEntity<?> deleteComment(
            @PathVariable("boardIdx") String idx,
            @PathVariable("commentIdx") String commentIdx) {
        return commentService.deleteComment(Long.parseLong(commentIdx));
    }
}
