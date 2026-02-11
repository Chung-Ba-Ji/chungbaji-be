package edu.lgcns.team428.chungbaji_be.schedule.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;
import edu.lgcns.team428.chungbaji_be.schedule.domain.entity.ScheduleEntity;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {

    // 이메일로 해당 사용자의 전체 일정 찾기
    List<ScheduleEntity> findAllByMember_Email(String email);


    //회원과 정책으로 특정 일정 하나 찾기
    Optional<ScheduleEntity> findByMemberAndPolicy(MemberEntity member, PolicyEntity policy);

    // 특정 유저, 정책, 일정의 상태 확인해서 일정 중복 체크
    boolean existsByMemberAndPolicyAndStatus(MemberEntity member, PolicyEntity policy, String status);
}
