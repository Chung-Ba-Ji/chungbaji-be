package edu.lgcns.team428.chungbaji_be.policy.domain.dto;

import lombok.*;
import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PolicyRequestDTO {
    private String title;
    private String policyDescription;
    private String supportContent;
    private Integer categoryMain;
    private String regionCode;
    private Integer minAge;
    private Integer maxAge;
    private LocalDate applyStartDate;
    private LocalDate applyEndDate;
    private String detailUrl;
}