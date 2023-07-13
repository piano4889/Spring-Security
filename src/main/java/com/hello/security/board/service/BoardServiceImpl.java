package com.hello.security.board.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hello.security.board.domain.Board;
import com.hello.security.board.domain.File;
import com.hello.security.board.mapper.BoardMapper;
import com.hello.security.board.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;
    private final FileMapper fileMapper;

    @Override
    public int insertBoard(Board board) {
        return boardMapper.insertBoard(board);
    }

    @Override
    public Page<Board> getBoardList(int pageNum) {
        PageHelper.startPage(pageNum, 5);
        return boardMapper.getBoardList();
    }

    @Override
    public Board getOneBoard(Long idx) {
        return boardMapper.getOneBoard(idx);
    }

    @Override
    public int updateBoard(Board vo) {
        return boardMapper.updateBoard(vo);
    }

    @Override
    public int deleteBoard(Long idx) {
        return boardMapper.deleteBoard(idx);
    }

    @Override
    public Map<String, Object> getOneBoard(String idx, List<?> list) {
        long boardIdx = Long.parseLong(idx);

        Map<String, Object> map = new LinkedHashMap<>();
        Optional<? extends List<?>> comment = Optional.ofNullable(list);

        //Optional 객체로 null 체크
        map.put("board", getOneBoard(boardIdx));

        //null이 아니면 map에 put()
        comment.ifPresent(commentVOS -> map.put("commentList",new PageInfo<>(list)));
        Optional<File> file = Optional.ofNullable(fileMapper.getOneFile(boardIdx));
        file.ifPresent(newFile -> map.put("fileInfo",file));

        return map;
    }

}
