package edu.lgcns.team428.chungbaji_be.schedule.domain.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScheduleResponseDTO {
    private Integer scheduleId;
    private Integer policyId;
    private String policyTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String isAlarm;
    private String status;
    private LocalDateTime createdAt;
    private Long dDay;
}