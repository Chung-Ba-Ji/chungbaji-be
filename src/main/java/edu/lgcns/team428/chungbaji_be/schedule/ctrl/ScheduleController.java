package edu.lgcns.team428.chungbaji_be.schedule.ctrl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.schedule.domain.dto.ScheduleResponseDTO;
import edu.lgcns.team428.chungbaji_be.schedule.domain.entity.ScheduleEntity;
import edu.lgcns.team428.chungbaji_be.schedule.service.ScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 내 일정 전체 조회
    @GetMapping("/list")
    public ResponseEntity<List<ScheduleResponseDTO>> getMySchedules(
        @AuthenticationPrincipal UserDetails userDetails // 토큰에서 이메일 추출
    ) {
        return ResponseEntity.ok(scheduleService.getMySchedules(userDetails.getUsername()));
    }

    // 일정 상세 조회 (정책 요약 정보 포함)
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> getDetail(@PathVariable Integer id) {
        
        return ResponseEntity.ok(scheduleService.getScheduleDetail(id));
    }

}