package com.hello.security.board.mapper;

import com.hello.security.board.domain.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {
    File getOneFile(Long fileIdx);
    int insertFiles(File vo);
}
