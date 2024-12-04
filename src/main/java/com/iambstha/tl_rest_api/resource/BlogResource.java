package com.iambstha.tl_rest_api.resource;

import com.iambstha.tl_rest_api.domain.ApiResponse;
import com.iambstha.tl_rest_api.dto.BlogReqDto;
import com.iambstha.tl_rest_api.service.BlogService;
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

    @Operation(summary = "Add blog", description = "Add a blog.")
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> save(@RequestBody BlogReqDto blogReqDto){
        ApiResponse apiResponse = ApiResponse.builder()
                .data(service.save(blogReqDto))
                .statusCode(200)
                .message("creation.success")
                .build();

         return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

}
