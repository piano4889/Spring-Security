package com.hello.security.board.domain;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Comment {
      private Long commentIdx;
      private Long idx;
      private String writer;
      private String content;
      private Date regdate;

      @Builder
      public Comment(Long idx, String writer, String content) {
            this.idx = idx;
            this.writer = writer;
            this.content = content;
      }
}
