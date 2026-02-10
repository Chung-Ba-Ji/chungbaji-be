package edu.lgcns.team428.chungbaji_be.policy.domain.dto;

import lombok.*;
import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PolicySaveDTO {
    private String title;             // plcyNm 
    private String policyDescription;  // plcyExplnCn 
    private String supportContent;     // plcySprtCn 
    private String categoryMain;       // lclsfNm (API 문자열 코드) 
    private String regionCode;         // pvsnInstGroupCd 
    private Integer minAge;            // sprtTrgtMinAge 
    private Integer maxAge;            // sprtTrgtMaxAge 
    private LocalDate applyStartDate;  // aplyYmd 파싱 결과
    private LocalDate applyEndDate;    // aplyYmd 파싱 결과
    private String detailUrl;          // aplyUrlAddr 
    
    // API에서 오는 코드들 (일단 String으로 받아서 서비스에서 INT ID로 바꿀 예정)
    private String jobCode;            // jobCd 
    private String educationCode;      // schoolCd 
}