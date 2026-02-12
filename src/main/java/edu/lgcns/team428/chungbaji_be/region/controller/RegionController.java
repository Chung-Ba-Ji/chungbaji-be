package edu.lgcns.team428.chungbaji_be.region.controller;

import edu.lgcns.team428.chungbaji_be.region.domain.dto.RegionResponseDTO;
import edu.lgcns.team428.chungbaji_be.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    /**
     * 모든 시도 코드(level 1) 조회
     * @return 시도 코드 리스트
     */
    @GetMapping("/sido")
    public ResponseEntity<List<RegionResponseDTO>> getSidoList() {
        return ResponseEntity.ok(regionService.getSidoList());
    }

    /**
     * 시도 코드(level 1)를 기반으로 시군구 코드(level 2) 조회
     * @param sidoCode 시도 코드
     * @return 시군구 코드 리스트
     */
    @GetMapping("/sigungu/{sidoCode}")
    public ResponseEntity<List<RegionResponseDTO>> getSigunguList(@PathVariable String sidoCode) {
        return ResponseEntity.ok(regionService.getSigunguList(sidoCode));
    }
}