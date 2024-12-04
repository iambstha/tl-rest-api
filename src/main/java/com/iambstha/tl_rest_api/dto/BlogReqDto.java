package com.iambstha.tl_rest_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BlogReqDto {

    @JsonIgnore
    private Long blogId;
    @JsonIgnore
    private Long userId;
    private String title;
    private String subTitle;
    private String post;

    private ThumbnailDetails thumbnailDetails;

}
