package com.iambstha.tl_rest_api.repository;

import com.iambstha.tl_rest_api.entity.Blog;
import com.iambstha.tl_rest_api.entity.Document;
import com.iambstha.tl_rest_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {



}
