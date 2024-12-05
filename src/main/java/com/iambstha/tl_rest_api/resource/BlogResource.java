package com.iambstha.tl_rest_api.resource;

import com.iambstha.tl_rest_api.domain.ApiResponse;
import com.iambstha.tl_rest_api.dto.BlogReqDto;
import com.iambstha.tl_rest_api.dto.DocumentResDto;
import com.iambstha.tl_rest_api.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Locale;

@Tag(name = "Blog Management")
@RestController
@RequestMapping("v1/api/blog")
@RequiredArgsConstructor
public class BlogResource {

    @Autowired
    private final BlogService service;

    @Autowired
    @Qualifier("blog")
    private MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();


    @Operation(summary = "Add blog", description = "Add a blog.")
    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> save(@ModelAttribute BlogReqDto blogReqDto){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.save(blogReqDto))
                .statusCode(200)
                .message("creation.success")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(summary = "Add comment", description = "Add a comment.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> getAll(){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.getAll())
                .statusCode(200)
                .message("creation.success")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(summary = "Fetch logged in user blog posts")
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> getByLoggedInUser(){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.getLoggedInUserBlogs())
                .statusCode(200)
                .message("creation.success")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(summary = "Fetch blog post by id")
    @GetMapping("/{blogId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> getByBlogId(@PathVariable("blogId") Long blogId){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.getBlogActualById(blogId))
                .statusCode(200)
                .message("creation.success")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


    @Operation(summary = "Update blog")
    @PutMapping("/{blogId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> update(@PathVariable("blogId") Long blogId, @ModelAttribute BlogReqDto blogReqDto){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.update(blogId, blogReqDto))
                .statusCode(200)
                .message("creation.success")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


    @Operation(summary = "Soft delete blog")
    @DeleteMapping("/{blogId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> softDelete(@PathVariable("blogId") Long blogId){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.softDelete(blogId))
                .statusCode(200)
                .message("creation.success")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


}
