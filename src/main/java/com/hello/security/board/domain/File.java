package com.hello.security.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class File {
    private Long fileIdx;
    private Long idx;
    private String originName;
    private String saveName;
    private String uploadPath;
    private String folderPath;

    @Builder
    public File(Long idx, String originName, String saveName, String uploadPath, String folderPath) {
        this.idx = idx;
        this.originName = originName;
        this.saveName = saveName;
        this.uploadPath = uploadPath;
        this.folderPath = folderPath;
    }
}
