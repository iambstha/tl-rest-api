package com.iambstha.tl_rest_api.repository;

import com.iambstha.tl_rest_api.entity.Blog;
import com.iambstha.tl_rest_api.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository  extends JpaRepository<Comment, Long>  {

    List<Comment> findByBlog(Blog blog);
}
