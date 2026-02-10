package edu.lgcns.team428.chungbaji_be.community.dao;

import edu.lgcns.team428.chungbaji_be.community.domain.entity.CommentEntity;
import edu.lgcns.team428.chungbaji_be.community.domain.entity.PostEntity;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    List<CommentEntity> findByMember(MemberEntity member);

    List<CommentEntity> findByMember_MemberId(Integer memberId);

    List<CommentEntity> findByPost(PostEntity post);

    List<CommentEntity> findByPost_PostId(Integer postId);
}
