package com.iambstha.tl_rest_api.dto;

import lombok.Data;

@Data
public class CommentResDto {

    private Long userId;
    private Long blogId;
    private Long commentId;
    private String comment;

}
