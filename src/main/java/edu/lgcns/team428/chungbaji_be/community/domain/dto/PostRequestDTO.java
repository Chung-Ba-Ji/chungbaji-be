package edu.lgcns.team428.chungbaji_be.community.domain.dto;

import edu.lgcns.team428.chungbaji_be.community.domain.entity.PostEntity;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostRequestDTO {

    private Integer postId;
    private Integer memberId;
    private Integer policyId;
    private Integer codeId;
    private String title;
    private String content;
    private String isAnonymous = "N";
    private String status = "CREATED";

}
