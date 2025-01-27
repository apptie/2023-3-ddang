package com.ddang.ddang.region.presentation;

import com.ddang.ddang.region.application.RegionService;
import com.ddang.ddang.region.application.dto.response.ReadRegionDto;
import com.ddang.ddang.region.presentation.dto.response.ReadRegionInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    @Cacheable(cacheNames = "firstRegion")
    public ResponseEntity<List<ReadRegionInfoResponse>> readAllFirst() {
        final List<ReadRegionDto> readRegionDtos = regionService.readAllFirst();
        final List<ReadRegionInfoResponse> readRegionResponses = readRegionDtos.stream()
                                                                               .map(ReadRegionInfoResponse::from)
                                                                               .toList();

        return ResponseEntity.ok(readRegionResponses);
    }

    @GetMapping("/{firstId}")
    @Cacheable(cacheNames = "secondRegion")
    public ResponseEntity<List<ReadRegionInfoResponse>> readAllSecond(@PathVariable final Long firstId) {
        final List<ReadRegionDto> readRegionDtos = regionService.readAllSecondByFirstRegionId(firstId);
        final List<ReadRegionInfoResponse> readRegionResponses = readRegionDtos.stream()
                                                                               .map(ReadRegionInfoResponse::from)
                                                                               .toList();

        return ResponseEntity.ok(readRegionResponses);
    }

    @GetMapping("/{firstId}/{secondId}")
    @Cacheable(cacheNames = "thirdRegion")
    public ResponseEntity<List<ReadRegionInfoResponse>> readAllThird(
            @PathVariable final Long firstId,
            @PathVariable final Long secondId
    ) {
        final List<ReadRegionDto> readRegionDtos = regionService.readAllThirdByFirstAndSecondRegionId(
                firstId,
                secondId
        );
        final List<ReadRegionInfoResponse> readRegionResponses = readRegionDtos.stream()
                                                                               .map(ReadRegionInfoResponse::from)
                                                                               .toList();

        return ResponseEntity.ok(readRegionResponses);
    }
}
