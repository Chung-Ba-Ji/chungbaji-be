package edu.lgcns.team428.chungbaji_be.member.domain.dto;

import java.time.LocalDate;

import edu.lgcns.team428.chungbaji_be.code.domain.entity.CodeEntity;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.region.domain.entity.RegionEntity;
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
    private String gender;
    private LocalDate birth_date;

    // 프론트에서 드롭다운 형식으로 입력받도록 강제 
    private String region;
    private String education;
    private String job;
    private String major;
    private String income;
    private String special;

}
