package com.iambstha.tl_rest_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class BlogResDto {

    private Long blogId;
    private Long userId;
    private String title;
    private String subTitle;
    private String post;
    private List<CommentResDto> comments;

}
