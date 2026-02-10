package edu.lgcns.team428.chungbaji_be.bookmark.domain.dto;

import edu.lgcns.team428.chungbaji_be.bookmark.domain.entity.BookmarkEntity;
import edu.lgcns.team428.chungbaji_be.bookmark.domain.entity.BookmarkEntity.BookmarkStatus;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookmarkRequestDTO {
    private Integer memberId;
    private Integer policyId;
    private BookmarkStatus status;

    // 엔티티 변환 
    public BookmarkEntity toEntity(MemberEntity member, PolicyEntity policy) {
        return BookmarkEntity.builder()
                .member(member)
                .policy(policy)
                .build();
    }
}
