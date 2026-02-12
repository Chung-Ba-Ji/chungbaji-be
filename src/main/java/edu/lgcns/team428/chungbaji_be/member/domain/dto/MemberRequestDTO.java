package edu.lgcns.team428.chungbaji_be.member.domain.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDTO {
    private String email;
    private String password;
    private String nickname;
    private String phoneNum;
    private String gender;
    private LocalDate birthDate;

    // 프론트에서 드롭다운 형식으로 입력받도록 강제
    private String regionSido; // 레벨1: "서울특별시"
    private String regionSigungu; // 레벨2: "마포구"

    private String educationCode;
    private String jobCode;
    private String majorCode;
    private String incomeCode;
    private String specialCode;

}
