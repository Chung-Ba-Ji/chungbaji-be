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
    private String phone_num;
    private char gender;
    private LocalDate birth_date;
    private int region_code;
    private int education_code;
    private int job_code;
    private int major_code;
    private int income_code;
    private int special_code;
}
