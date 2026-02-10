package edu.lgcns.team428.chungbaji_be.schedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import edu.lgcns.team428.chungbaji_be.schedule.dao.ScheduleRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;

}