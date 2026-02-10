package edu.lgcns.team428.chungbaji_be.region.repository;

import edu.lgcns.team428.chungbaji_be.region.domain.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
}
