package com.hello.security.board.service;

import com.github.pagehelper.Page;
import com.hello.security.board.domain.Board;

import java.util.List;
import java.util.Map;


public interface BoardService {

    //CRUD
    int insertBoard(Board board);

    Page<Board> getBoardList(int pageNum);
    Board getOneBoard(Long idx);

    int updateBoard(Board vo);

    int deleteBoard(Long boardIdx);

    Map<String, Object> getOneBoard(String idx, List<?> list);
}
