package edu.lgcns.team428.chungbaji_be.schedule.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.lgcns.team428.chungbaji_be.schedule.domain.entity.ScheduleEntity;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {
}
