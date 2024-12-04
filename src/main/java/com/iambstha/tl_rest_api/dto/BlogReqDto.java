package com.iambstha.tl_rest_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class BlogReqDto {

    @JsonIgnore
    private Long blogId;
    private Long userId;
    private String title;
    private String subTitle;
    private String post;

}
