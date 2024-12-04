package com.iambstha.tl_rest_api.service;

import com.iambstha.tl_rest_api.dto.CommentReqDto;
import com.iambstha.tl_rest_api.dto.CommentResDto;
import com.iambstha.tl_rest_api.entity.Blog;
import com.iambstha.tl_rest_api.entity.Comment;
import com.iambstha.tl_rest_api.exception.BadRequestException;
import com.iambstha.tl_rest_api.exception.RecordNotFoundException;
import com.iambstha.tl_rest_api.mapper.CommentMapper;
import com.iambstha.tl_rest_api.repository.CommentRepository;
import com.iambstha.tl_rest_api.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {


    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final BlogService blogService;

    @Autowired
    private final CommentMapper commentMapper;

    public CommentResDto save(CommentReqDto commentReqDto) {

        try{
            Comment comment = commentMapper.toEntity(commentReqDto);
            comment.setUser(userService.getUserActualById(UserUtil.getUserId()));
            return commentMapper.toDto(commentRepository.save(comment));
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }

    }

    public List<CommentResDto> getAll() {
        try{
            return commentRepository.findAll()
                    .stream()
                    .filter(comment -> !comment.isDeleted())
                    .map(commentMapper::toDto)
                    .toList();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    public List<CommentResDto> getAllForBlogId(Long blogId) {
        try{
            Blog blog = blogService.getBlogActualById(blogId);
            List<Comment> comments = commentRepository.findByBlog(blog);
            return comments.stream()
                    .filter(comment -> !comment.isDeleted())
                    .map(commentMapper::toDto)
                    .toList();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    public boolean softdelete(Long commentId) {
        try{
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RecordNotFoundException("Comment with id " + commentId + " not found"));
            comment.setDeleted(true);
            commentRepository.save(comment);
            return true;
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

}
