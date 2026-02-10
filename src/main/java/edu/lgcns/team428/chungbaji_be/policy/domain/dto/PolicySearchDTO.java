package edu.lgcns.team428.chungbaji_be.policy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor
public class PolicySearchDTO {
    private String regionCode;      // 필수: 지역
    private Integer age;            // 필수: 연령 
    private Integer jobCode;        // 필수: 취업상태 ID (우리 DB의 INT PK) 
    private Integer educationCode;  // 필수: 학력상태 ID (우리 DB의 INT PK) 

    private Integer majorCode;      // 선택: 전공 ID 
    private Integer incomeCode;     // 선택: 소득 ID 
    private Integer specialCode;    // 선택: 특화 ID 
}