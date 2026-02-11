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

    @Transactional
    public void createScheduleFromBookmark(MemberEntity member, PolicyEntity policy) {
        //이미 'ACTIVE'인 일정이 있는지 확인 (중복 방지)
        if (scheduleRepository.existsByMemberAndPolicyAndStatus(member, policy, "ACTIVE")) {
            return;
        }

        // 'DELETED'로 있는 일정이 있다면 다시 'ACTIVE'로 변경, 없으면 새로 생성
        scheduleRepository.findByMemberAndPolicy(member, policy).ifPresentOrElse(
            existing -> existing.setStatus("ACTIVE"), // 기존 데이터 있으면 상태만 변경
            () -> {
                // 없으면 새로 생성
                ScheduleEntity schedule = ScheduleEntity.builder()
                        .member(member)
                        .policy(policy)
                        .startDate(policy.getApplyStartDate())
                        .endDate(policy.getApplyEndDate())
                        .isAlarm("N")
                        .status("ACTIVE")
                        .build();
                scheduleRepository.save(schedule);
            }
        );
    }

    // 북마크 해제 시 일정 상태 변경
    @Transactional
    public void deleteScheduleFromBookmark(MemberEntity member, PolicyEntity policy) {
        scheduleRepository.findByMemberAndPolicy(member, policy).ifPresent(schedule -> {
            schedule.setStatus("DELETED");
        });
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> getMySchedules(String email) {
        //회원이 가진 모든 일정 리스트 불러오기
        List<ScheduleEntity> entities = scheduleRepository.findAllByMember_Email(email);

        return entities.stream()
                .filter(s -> "ACTIVE".equals(s.getStatus())) //삭제된 일정 제외
                .map(this::convertToResponseDTO)
                .toList();
    }

    // DTO 변환 로직 공통화 및 D-DAY 계산
    private ScheduleResponseDTO convertToResponseDTO(ScheduleEntity entity) {
        //null 체크 추가
        long dDay = 0;
        if (entity.getEndDate() != null) {
            dDay = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), entity.getEndDate());
        }
        
        return ScheduleResponseDTO.builder()
                .scheduleId(entity.getScheduleId())
                .policyId(entity.getPolicy().getPolicyId())
                .policyTitle(entity.getPolicy().getTitle())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isAlarm(entity.getIsAlarm())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .dDay(dDay)
                .build();
    }

    //상세 조회
    @Transactional(readOnly = true)
    public ScheduleResponseDTO getScheduleDetail(Integer id) {
        ScheduleEntity entity = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 없습니다. id=" + id));

        return convertToResponseDTO(entity);
    }

    @Transactional
    public void deleteAllSchedulesByMember(MemberEntity member) {
        // 해당 멤버의 모든 일정을 찾아서 상태를 DELETED로 변경
        List<ScheduleEntity> schedules = scheduleRepository.findAllByMember_Email(member.getEmail());
        schedules.forEach(schedule -> schedule.setStatus("DELETED"));
    }
}