package com.iambstha.tl_rest_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class CommentReqDto {

    @JsonIgnore
    private Long commentId;
    @JsonIgnore
    private Long userId;
    private Long blogId;
    private String comment;

}
