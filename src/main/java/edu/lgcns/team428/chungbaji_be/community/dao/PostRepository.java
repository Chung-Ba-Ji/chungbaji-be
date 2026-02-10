package edu.lgcns.team428.chungbaji_be.community.dao;

import edu.lgcns.team428.chungbaji_be.community.domain.entity.PostEntity;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {

    List<PostEntity> findByMember(MemberEntity member);

    List<PostEntity> findByMember_MemberId(Integer memberId);
}
