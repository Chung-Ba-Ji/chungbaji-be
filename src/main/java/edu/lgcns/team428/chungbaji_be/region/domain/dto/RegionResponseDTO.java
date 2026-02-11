package edu.lgcns.team428.chungbaji_be.region.domain.dto;

import edu.lgcns.team428.chungbaji_be.region.domain.entity.RegionEntity;
import lombok.*;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegionResponseDTO {

    private Integer regionId;
    private String regionCode;
    private String regionName;
    private Integer level;
    private String parentRegionCode;

    public static RegionResponseDTO fromEntity(RegionEntity entity) {
        return RegionResponseDTO.builder()
                .regionId(entity.getRegionId())
                .regionCode(entity.getRegionCode())
                .regionName(entity.getRegionName())
                .level(entity.getLevel())
                .parentRegionCode(entity.getParentRegion().getRegionCode())
                .build();
    }
}
