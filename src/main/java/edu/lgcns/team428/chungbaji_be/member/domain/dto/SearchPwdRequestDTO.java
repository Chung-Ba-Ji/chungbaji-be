package edu.lgcns.team428.chungbaji_be.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchPwdRequestDTO {
    private String email ;
    private String phone_num ;
}
