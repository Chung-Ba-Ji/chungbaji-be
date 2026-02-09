package edu.lgcns.team428.chungbaji_be.bookmark.domain.dto;

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
    private Integer policy_id ;
}
