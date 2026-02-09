package edu.lgcns.team428.chungbaji_be.bookmark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.lgcns.team428.chungbaji_be.bookmark.domain.entity.BookmarkEntity;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Integer>{

    
} 
