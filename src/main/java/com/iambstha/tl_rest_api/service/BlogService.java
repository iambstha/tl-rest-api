package com.iambstha.tl_rest_api.service;

import com.iambstha.tl_rest_api.dto.BlogReqDto;
import com.iambstha.tl_rest_api.dto.BlogResDto;
import com.iambstha.tl_rest_api.entity.Blog;
import com.iambstha.tl_rest_api.exception.BadRequestException;
import com.iambstha.tl_rest_api.exception.RecordNotFoundException;
import com.iambstha.tl_rest_api.mapper.BlogMapper;
import com.iambstha.tl_rest_api.repository.BlogRepository;
import com.iambstha.tl_rest_api.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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


    public BlogResDto save(BlogReqDto blogReqDto) {

        try{
            Blog blog = blogMapper.toEntity(blogReqDto);
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

}
