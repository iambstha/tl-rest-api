package com.iambstha.tl_rest_api.mapper;

import com.iambstha.tl_rest_api.dto.BlogReqDto;
import com.iambstha.tl_rest_api.dto.BlogResDto;
import com.iambstha.tl_rest_api.dto.CommentResDto;
import com.iambstha.tl_rest_api.entity.Blog;
import com.iambstha.tl_rest_api.entity.Comment;
import com.iambstha.tl_rest_api.entity.User;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "mapBlogCommentResDtos")
    BlogResDto toDto(Blog blog);

    @Mapping(target = "blogId", ignore = true)
    @Mapping(target = "user", source = "userId")
    Blog toEntity(BlogReqDto blogReqDto);

    @Mapping(target = "blogId", ignore = true)
    void updateBlogFromDto(BlogReqDto blogReqDto, @MappingTarget Blog existingBlog);


    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setUserId(userId);
        return user;
    }

    @Named("mapBlogCommentResDtos")
    default List<CommentResDto> mapBlogCommentResDtos(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream().map(comment -> {
            CommentResDto dto = new CommentResDto();
            dto.setBlogId(Optional.ofNullable(comment.getBlog())
                    .map(Blog::getBlogId)
                    .orElse(null));
            dto.setCommentId(comment.getCommentId());
            dto.setComment(comment.getComment());
            dto.setUserId(comment.getUser().getUserId());
            return dto;
        }).collect(Collectors.toList());
    }


}
