package com.hello.security.board.controller;

import com.github.pagehelper.PageInfo;
import com.hello.security.board.domain.Board;
import com.hello.security.board.service.BoardService;
import com.hello.security.board.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.security.Principal;
import java.util.Map;

@Tag(name = "Board API", description = "게시글 API")
@RestController
@RequiredArgsConstructor
@Log4j2
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    //게시글 작성
    @PostMapping(value = "/board/insert")
    @Operation(summary = "insertBoard", description = "게시글 작성")
    public int saveBoard(@RequestBody Board vo, Principal principal) {

        vo.setWriter(principal.getName());
        log.info("접속 중인 유저: {}", principal.getName());
        return boardService.insertBoard(vo);
    }

    //전체 게시글 조회
    @GetMapping(value = "/boards/{pageNum}")
    @Operation(summary = "getBoardList", description = "전체 게시글 가져오기, pageNum = 현재 페이지")
    public PageInfo<Board> getBoardList(@PathVariable(required = false) Integer pageNum) {

        if (pageNum == null) {
            pageNum = 1;
        }
        return boardService.getBoardList(pageNum).toPageInfo();
    }

    //게시글 detail
    @GetMapping(value = "/board/{boardIdx}")
    @Operation(summary = "getBoard", description = "boardIdx = 게시글 번호, pageNum = 현재 페이지")
    public Map<?, ?> getOneBoardWithComment(
            @PathVariable("boardIdx") String boardIdx,
            @PathVariable(required = false) Integer pageNum) {

        //pageNum default 설정
        if (pageNum == null) {
            pageNum = 1;
        }
        return boardService.getOneBoard(boardIdx,
                commentService.getCommentList(Long.parseLong(boardIdx), pageNum));
    }

    //게시글 수정
    @PatchMapping(value = "/board/{boardIdx}")
    @Operation(summary = "updateBoard", description = "boardIdx를 이용하여 update")
    public int updateBoard(@PathVariable("boardIdx") String boardIdx, @RequestBody Board vo) {
        vo.setIdx(Long.parseLong(boardIdx));
        return boardService.updateBoard(vo);
    }

    //게시글 삭제
    @DeleteMapping(value = "/board/{boardIdx}")
    @Operation(summary = "deleteBoard", description = "boardIdx를 이용하여 delete")
    public int deleteBoard(@PathVariable("boardIdx") String idx) {
        return boardService.deleteBoard(Long.parseLong(idx));
    }


}
