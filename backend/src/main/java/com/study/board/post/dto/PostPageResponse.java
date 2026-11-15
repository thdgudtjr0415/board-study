package com.study.board.post.dto;

import java.util.List;

public record PostPageResponse(List<PostResponse> posts,
                               int page,
                               int size,
                               long totalCount,
                               int totalPages) {
}
