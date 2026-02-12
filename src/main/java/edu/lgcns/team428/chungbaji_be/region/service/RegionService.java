package edu.lgcns.team428.chungbaji_be.region.service;

import edu.lgcns.team428.chungbaji_be.region.domain.dto.RegionResponseDTO;
import edu.lgcns.team428.chungbaji_be.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public List<RegionResponseDTO> getSidoList() {
        return regionRepository.findByLevelOrderByRegionCodeAsc(1)
                .stream()
                .map(RegionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RegionResponseDTO> getSigunguList(String sidoCode) {
        return regionRepository.findByParentRegion_RegionCodeOrderByRegionCodeAsc(sidoCode)
                .stream()
                .map(RegionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
