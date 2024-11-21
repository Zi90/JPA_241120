package com.ezen.boot_JPA.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
    private Long cno;
    private Long bno;
    private String writer;
    private String content;
    private LocalDateTime regAt;
    private LocalDateTime modAt;
}
