package edu.lgcns.team428.chungbaji_be.community.dao;

import edu.lgcns.team428.chungbaji_be.community.domain.entity.PostEntity;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {

    List<PostEntity> findByMember(MemberEntity member);

    List<PostEntity> findByMember_MemberId(Integer memberId);
    
    // 제목 또는 내용에 키워드가 포함된 게시글 검색
    Page<PostEntity> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
