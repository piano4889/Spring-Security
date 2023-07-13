package com.hello.security.board.mapper;

import com.github.pagehelper.Page;
import com.hello.security.board.domain.Board;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

    int insertBoard(Board board);

    Page<Board> getBoardList();

    Board getOneBoard(Long idx);

    int updateBoard(Board vo);

    int deleteBoard(Long idx);
}


