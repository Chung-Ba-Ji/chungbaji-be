package edu.lgcns.team428.chungbaji_be.region.repository;

import edu.lgcns.team428.chungbaji_be.region.domain.entity.RegionEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer> {
    // 레벨1(시/도): level=1 AND region_name=?
    Optional<RegionEntity> findByLevelAndRegionName(Integer level, String regionName);

    Optional<RegionEntity> findByLevelAndParentRegion_RegionCodeAndRegionName(
            Integer level,
            String parentRegionCode,
            String regionName);

}
