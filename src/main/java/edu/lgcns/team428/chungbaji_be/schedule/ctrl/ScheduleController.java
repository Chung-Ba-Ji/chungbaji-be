package edu.lgcns.team428.chungbaji_be.schedule.ctrl;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import edu.lgcns.team428.chungbaji_be.schedule.service.ScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
    
    private final ScheduleService scheduleService;

}