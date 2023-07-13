package com.hello.security.board.domain;

import lombok.*;

import java.util.Date;

//너무 많은 DTO,Layer 분리는 유지보수가 힘듦

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Board {
    private Long idx;
    private String title;
    private String content;
    private String writer;
    private Date regdate;

    @Builder
    public Board(String title, String content, String writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }
}
