package edu.lgcns.team428.chungbaji_be.bookmark.domain.dto;

import java.time.LocalDateTime;

import edu.lgcns.team428.chungbaji_be.bookmark.domain.entity.BookmarkEntity;
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
public class BookmarkResponseDTO {
    private Integer bookmarkId;
    private Integer memberId;
    private Integer policyId;
    private BookmarkEntity.BookmarkStatus status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    // DTO로 변환
    public static BookmarkResponseDTO fromEntity(BookmarkEntity entity) {
        return BookmarkResponseDTO.builder()
                .bookmarkId(entity.getBookmark_id())
                .memberId(entity.getMember().getMember_id())
                .policyId(entity.getPolicy().getPolicyId())
                .status(entity.getStatus())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .build();
    }
}
