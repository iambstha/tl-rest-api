package com.iambstha.tl_rest_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iambstha.tl_rest_api.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tl_document")
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_content_type")
    private String fileContentType;

    private Integer status = 0;

    private Boolean deleted = Boolean.FALSE;

    @Lob
    @Column(name = "content")
    private byte[] content;


}
