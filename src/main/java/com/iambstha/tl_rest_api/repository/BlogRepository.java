package com.iambstha.tl_rest_api.repository;

import com.iambstha.tl_rest_api.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

}
