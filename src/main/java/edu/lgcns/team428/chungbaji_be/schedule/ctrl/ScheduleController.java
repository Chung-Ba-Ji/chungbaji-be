package edu.lgcns.team428.chungbaji_be.schedule.ctrl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import edu.lgcns.team428.chungbaji_be.schedule.domain.dto.ScheduleResponseDTO;
import edu.lgcns.team428.chungbaji_be.schedule.service.ScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 내 일정 전체 조회
    @GetMapping("/list")
    public ResponseEntity<List<ScheduleResponseDTO>> getMySchedules(@RequestParam String email) {
        // 실제 운영 시에는 @AuthenticationPrincipal 등을 통해 토큰에서 이메일을 추출한다.
        return ResponseEntity.ok(scheduleService.getMySchedules(email));
    }

    // 일정 상세 조회 (정책 요약 정보 포함)
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> getDetail(@PathVariable Integer id) {
        // TODO: 일정 단건 조회 로직 필요
        return ResponseEntity.ok().build(); 
    }
}