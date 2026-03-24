package com.example.board.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    private Long boardId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 2, max = 200, message = "제목은 2자 이상 200자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(min = 5, message = "내용은 5자 이상이어야 합니다.")
    private String content;

    @NotBlank(message = "작성자명은 필수입니다.")
    @Size(min = 2, max = 50, message = "작성자명은 2자 이상 50자 이하여야 합니다.")
    private String author;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private Integer viewCount;

}
