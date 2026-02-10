package edu.lgcns.team428.chungbaji_be.region.repository;

import edu.lgcns.team428.chungbaji_be.region.domain.entity.RegionEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer> {
    Optional<RegionEntity> findByRegionName(String regionName);
}
