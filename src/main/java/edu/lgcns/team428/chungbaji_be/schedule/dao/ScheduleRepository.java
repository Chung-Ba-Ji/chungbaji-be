package edu.lgcns.team428.chungbaji_be.schedule.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;
import edu.lgcns.team428.chungbaji_be.schedule.domain.entity.ScheduleEntity;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {

    // Member 엔티티의 email 필드를 참조하여 일정 리스트를 가져온다.
    List<ScheduleEntity> findAllByMember_Email(String email);


    // 특정 유저와 정책으로 이미 등록된 일정이 있는지 확인 (중복 방지용)
    boolean existsByMemberAndPolicy(MemberEntity member, PolicyEntity policy);

    // 특정 유저와 정책에 해당하는 일정 삭제 (북마크 해제 연동용)
    void deleteByMemberAndPolicy(MemberEntity member, PolicyEntity policy);
}
