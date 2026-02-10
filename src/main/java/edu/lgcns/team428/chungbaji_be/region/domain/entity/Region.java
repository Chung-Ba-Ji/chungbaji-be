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
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer regionId;

    @Column(length = 5, nullable = false, unique = true)
    private String regionCode;

    @Column(length = 20, nullable = false)
    private String regionName;

    @Column(nullable = false)
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "regionCode")
    private Region parentRegionCode;
}
