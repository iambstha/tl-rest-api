package com.iambstha.tl_rest_api.service;

import com.iambstha.tl_rest_api.dto.CommentReqDto;
import com.iambstha.tl_rest_api.dto.CommentResDto;
import com.iambstha.tl_rest_api.entity.Comment;
import com.iambstha.tl_rest_api.exception.BadRequestException;
import com.iambstha.tl_rest_api.mapper.CommentMapper;
import com.iambstha.tl_rest_api.repository.CommentRepository;
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
    private final CommentMapper commentMapper;

    public CommentResDto save(CommentReqDto commentReqDto) {

        try{
            Comment comment = commentMapper.toEntity(commentReqDto);
            return commentMapper.toDto(commentRepository.save(comment));
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }

    }

    public List<CommentResDto> getAll() {
        try{
            return commentRepository.findAll().stream().map(commentMapper::toDto).toList();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }
}
