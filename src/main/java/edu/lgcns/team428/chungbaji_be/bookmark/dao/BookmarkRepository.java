package edu.lgcns.team428.chungbaji_be.bookmark.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.lgcns.team428.chungbaji_be.bookmark.domain.entity.BookmarkEntity;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Integer> {
    // 북마크 저장/해제
    Optional<BookmarkEntity> findByMemberAndPolicy(MemberEntity member, PolicyEntity policy);

    
    Optional<BookmarkEntity> findByMemberMemberIdAndPolicyPolicyId(Integer memberId, Integer policyId) ;

    // 정책 리스트업
    List<BookmarkEntity> findAllByMemberAndStatus(MemberEntity member, BookmarkEntity.BookmarkStatus status);
}
