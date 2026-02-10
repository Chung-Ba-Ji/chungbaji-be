package edu.lgcns.team428.chungbaji_be.schedule.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;
import edu.lgcns.team428.chungbaji_be.schedule.dao.ScheduleRepository;
import edu.lgcns.team428.chungbaji_be.schedule.domain.dto.ScheduleResponseDTO;
import edu.lgcns.team428.chungbaji_be.schedule.domain.entity.ScheduleEntity;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;

    //북마크 일정 매핑 및 생성
    @Transactional
    public void createScheduleFromBookmark(MemberEntity member, PolicyEntity policy) {
        ScheduleEntity schedule = ScheduleEntity.builder()
                .member(member)
                .policy(policy)
                .startDate(policy.getApplyStartDate()) // 정책의 신청 시작일
                .endDate(policy.getApplyEndDate())     // 정책의 신청 종료일
                .isAlarm("N") // 기본값
                .status("UPCOMING")
                .createdAt(LocalDateTime.now())
                .build();
        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> getMySchedules(String email) {
        List<ScheduleEntity> entities = scheduleRepository.findAllByMember_Email(email);

        return entities.stream()
                .map(entity -> ScheduleResponseDTO.builder()
                        .scheduleId(entity.getScheduleId())
                        .policyId(entity.getPolicy().getPolicyId())
                        .policyTitle(entity.getPolicy().getTitle()) // 정책 제목 포함
                        .startDate(entity.getStartDate())
                        .endDate(entity.getEndDate())
                        .isAlarm(entity.getIsAlarm())
                        .status(entity.getStatus())
                        .createdAt(entity.getCreatedAt())
                        .build())
                .toList();
    }

}