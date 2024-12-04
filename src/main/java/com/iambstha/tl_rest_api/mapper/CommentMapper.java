package com.iambstha.tl_rest_api.mapper;


import com.iambstha.tl_rest_api.dto.CommentReqDto;
import com.iambstha.tl_rest_api.dto.CommentResDto;
import com.iambstha.tl_rest_api.entity.Blog;
import com.iambstha.tl_rest_api.entity.Comment;
import com.iambstha.tl_rest_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "blogId", source = "blog.blogId")
    CommentResDto toDto(Comment comment);

    @Mapping(target = "commentId", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "blog", source = "blogId")
    Comment toEntity(CommentReqDto commentReqDto);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setUserId(userId);
        return user;
    }

    default Blog mapBlog(Long blogId) {
        if (blogId == null) {
            return null;
        }
        Blog blog = new Blog();
        blog.setBlogId(blogId);
        return blog;
    }

}
