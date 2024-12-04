package com.iambstha.tl_rest_api.dto;

import com.iambstha.tl_rest_api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentResDto {

    private Long documentId;
    private User user;
    private String originalFileName;
    private String fileName;
    private String fileContentType;
    private Integer status;
    private Boolean deleted;

}
