package edu.lgcns.team428.chungbaji_be.policy.domain.dto;

import lombok.*;
import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponseDTO {
    private Integer policyId; 
    private String title;
    private String policyDescription; 
    private String regionCode; 
    private Integer viewCount; 
    private LocalDate applyEndDate; 
    private String detailUrl; 
    private Long dDay;
}