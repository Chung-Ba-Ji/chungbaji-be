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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // DTO로 변환
    public static BookmarkResponseDTO fromEntity(BookmarkEntity entity) {
        return BookmarkResponseDTO.builder()
                .bookmarkId(entity.getBookmarkId())
                .memberId(entity.getMember().getMemberId())
                .policyId(entity.getPolicy().getPolicyId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
