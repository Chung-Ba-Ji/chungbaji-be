package edu.lgcns.team428.chungbaji_be.region.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "region")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Integer regionId;

    @Column(name = "region_code", length = 5, nullable = false, unique = true)
    private String regionCode;

    @Column(name = "region_name", length = 20, nullable = false)
    private String regionName;

    @Column(nullable = false)
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_region_code", referencedColumnName = "region_code")
    private RegionEntity parentRegion;
}
