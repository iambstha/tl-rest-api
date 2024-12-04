package com.iambstha.tl_rest_api.service;

import com.iambstha.tl_rest_api.dto.BlogReqDto;
import com.iambstha.tl_rest_api.dto.BlogResDto;
import com.iambstha.tl_rest_api.dto.DocumentResDto;
import com.iambstha.tl_rest_api.dto.ThumbnailDetails;
import com.iambstha.tl_rest_api.entity.Blog;
import com.iambstha.tl_rest_api.entity.Document;
import com.iambstha.tl_rest_api.exception.*;
import com.iambstha.tl_rest_api.mapper.BlogMapper;
import com.iambstha.tl_rest_api.repository.BlogRepository;
import com.iambstha.tl_rest_api.repository.DocumentRepository;
import com.iambstha.tl_rest_api.util.FileUtility;
import com.iambstha.tl_rest_api.util.GeneralUtil;
import com.iambstha.tl_rest_api.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BlogService {

    @Autowired
    private final BlogRepository blogRepository;

    @Autowired
    private final BlogMapper blogMapper;

    @Autowired
    private final UserService userService;

    @Autowired
    private final DocumentService documentService;


    public BlogResDto save(BlogReqDto blogReqDto) {

        try{
            Blog blog = blogMapper.toEntity(blogReqDto);
            blogRepository.save(blog);
            if(blogReqDto.getThumbnailDetails() != null){
                blog.setDocument(documentService.handleDocument(blogReqDto.getThumbnailDetails(), new Document()));
            }
            blog.setUser(userService.getUserActualById(UserUtil.getUserId()));
            return blogMapper.toDto(blogRepository.save(blog));
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }

    }

    public List<BlogResDto> getAll() {
        try{
            return blogRepository.findAll()
                    .stream()
                    .filter(blog -> !blog.isDeleted())
                    .map(blogMapper::toDto)
                    .toList();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    public Blog getBlogActualById(Long blogId) {
        try{
            return blogRepository.findById(blogId)
                    .orElseThrow(() -> new RecordNotFoundException("Blog with id " + blogId + " not found"));
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    public List<BlogResDto> getLoggedInUserBlogs() {
        try{
            List<Blog> blogs = blogRepository.findByUser(userService.getUserActualById(UserUtil.getUserId()));
            return blogs.stream()
                    .filter(blog -> !blog.isDeleted())
                    .map(blogMapper::toDto)
                    .toList();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    public BlogResDto update(Long blogId, BlogReqDto blogReqDto) {

        Blog existingBlog = getBlogActualById(blogId);
        blogMapper.updateBlogFromDto(blogReqDto, existingBlog);
        existingBlog.setModifiedBy(UserUtil.getUserId());
        existingBlog.setModifiedTs(GeneralUtil.getCurrentTs());

        return blogMapper.toDto(blogRepository.save(existingBlog));

    }

    public boolean softDelete(Long blogId) {
        try{
            Blog blog =getBlogActualById(blogId);
            if(UserUtil.isAdmin() || blog.getUser() == userService.getUserActualById(UserUtil.getUserId())){
                blog.setDeleted(true);
                blogRepository.save(blog);
                return true;
            }else{
                throw new BadRequestException("Cannot delete the blog");
            }
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

}
