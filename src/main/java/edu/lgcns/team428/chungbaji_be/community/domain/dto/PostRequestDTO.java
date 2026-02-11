package edu.lgcns.team428.chungbaji_be.community.domain.dto;

import edu.lgcns.team428.chungbaji_be.code.domain.entity.CodeEntity;
import edu.lgcns.team428.chungbaji_be.community.domain.entity.PostEntity;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostRequestDTO {

    private Integer memberId;
    private Integer policyId;
    private Integer codeId;
    private String title;
    private String content;
    private String isAnonymous;
    private String status;

    public PostEntity toEntity(MemberEntity member, PolicyEntity policy, CodeEntity code) {
        return PostEntity.builder()
                .member(member)
                .policy(policy)
                .code(code)
                .title(title)
                .content(content)
                .isAnonymous(isAnonymous)
                .status(status)
                .build();
    }

}
