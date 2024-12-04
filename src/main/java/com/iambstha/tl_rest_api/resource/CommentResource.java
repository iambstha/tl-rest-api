package com.iambstha.tl_rest_api.resource;

import com.iambstha.tl_rest_api.domain.ApiResponse;
import com.iambstha.tl_rest_api.dto.CommentReqDto;
import com.iambstha.tl_rest_api.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Tag(name = "Comment Management")
@RestController
@RequestMapping("v1/api/comment")
@RequiredArgsConstructor
public class CommentResource {

    @Autowired
    private final CommentService service;

    @Autowired
    @Qualifier("comment")
    private MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();


    @Operation(summary = "Add comment", description = "Add a comment.")
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> save(@RequestBody CommentReqDto commentReqDto){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.save(commentReqDto))
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

    @Operation(summary = "Add comment", description = "Add a comment.")
    @GetMapping("/blog/{blogId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> getAllForBlogId(@PathVariable("blogId") Long blogId){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.getAllForBlogId(blogId))
                .statusCode(200)
                .message("creation.success")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(summary = "Update comment")
    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> update(@PathVariable("commentId") Long commentId, @RequestBody CommentReqDto commentReqDto){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.update(commentId, commentReqDto))
                .statusCode(200)
                .message("creation.success")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(summary = "Soft delete comment")
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> softDelete(@PathVariable("commentId") Long commentId){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.softDelete(commentId))
                .statusCode(200)
                .message("creation.success")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


}

