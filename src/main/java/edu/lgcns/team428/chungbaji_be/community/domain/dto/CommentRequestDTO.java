package edu.lgcns.team428.chungbaji_be.community.domain.dto;

import edu.lgcns.team428.chungbaji_be.community.domain.entity.CommentEntity;
import edu.lgcns.team428.chungbaji_be.community.domain.entity.PostEntity;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommentRequestDTO {

    private Integer postId;
    private Integer memberId;
    private String content;
    private String status;

    public CommentEntity toEntity(PostEntity post, MemberEntity member) {
        return CommentEntity.builder()
                .post(post)
                .member(member)
                .content(content)
                .status(status)
                .build();
    }

}
