package com.hello.security.board.controller;

import com.hello.security.board.domain.Board;
import com.hello.security.board.domain.File;
import com.hello.security.board.service.BoardService;
import com.hello.security.board.service.CommentService;
import com.hello.security.board.service.FileService;
import com.hello.security.jwt.config.JwtAuthorizationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Business Controller", description = "게시글+댓글 관련 API")
public class MainController {
    private final BoardService boardService;
    private final CommentService commentService;
    private final FileService fileService;
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);


    @GetMapping(value = "/board/{boardIdx}/{pageNum}")
    @Operation(summary = "getBoard + Comment", description = "게시글 + 댓글 조회, pageNum = 댓글 페이징")
    public Map<?, ?> getOneBoardWithComment(
            @PathVariable("boardIdx") String boardIdx,
            @PathVariable(required = false) Integer pageNum) {
        //pageNum default 설정
        if (pageNum == null) {
            pageNum = 1;
        }
        return boardService.getOneBoard(boardIdx
                , commentService.getCommentList(Long.parseLong(boardIdx), pageNum));
    }

    @PostMapping(value = "/boardMake")
    @Operation(summary = "insertBoard + File", description = "게시글 작성, 파일이 있을 때 파일 업로드")
    public ResponseEntity<?> insertBoardWithFile(
            Board board,
            @RequestParam(value = "filename") MultipartFile file,
            Principal principal) {

        board.setWriter(principal.getName());
        boardService.insertBoard(board);
        System.out.println(board.getIdx());
        logger.info("board:{}", board);
        logger.info("file:{}", file);
        logger.info("principal:{}", principal);


        if (!file.isEmpty()) {
            File fileVO = fileService.insertFiles(board.getIdx(), file);
            return ResponseEntity.ok().body("게시글 및 파일 업로드 완료");
        }
        return ResponseEntity.ok().body("게시글 업로드 완료");
    }

}
