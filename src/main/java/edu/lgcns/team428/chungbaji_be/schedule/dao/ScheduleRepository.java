package edu.lgcns.team428.chungbaji_be.schedule.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.lgcns.team428.chungbaji_be.schedule.domain.entity.ScheduleEntity;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {

    // Member 엔티티의 email 필드를 참조하여 일정 리스트를 가져온다.
    List<ScheduleEntity> findAllByMember_Email(String email);

}
