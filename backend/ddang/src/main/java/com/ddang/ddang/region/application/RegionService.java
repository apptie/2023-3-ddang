package com.ddang.ddang.region.application;

import com.ddang.ddang.auction.application.exception.UninitializedRegionException;
import com.ddang.ddang.region.application.dto.response.ReadRegionDto;
import com.ddang.ddang.region.application.exception.RegionNotFoundException;
import com.ddang.ddang.region.domain.InitializationRegionProcessor;
import com.ddang.ddang.region.domain.Region;
import com.ddang.ddang.region.domain.repository.RegionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;
    private final InitializationRegionProcessor regionProcessor;

    @Transactional
    public void createRegions() {
        final List<Region> regions = regionProcessor.requestRegions();

        regionRepository.saveAll(regions);
    }

    public List<ReadRegionDto> readAllFirst() {
        final List<Region> firstRegions = regionRepository.findFirstAll();

        if (firstRegions.isEmpty()) {
            throw new UninitializedRegionException("등록된 지역이 없습니다.");
        }

        return firstRegions.stream()
                           .map(ReadRegionDto::from)
                           .toList();
    }

    public List<ReadRegionDto> readAllSecondByFirstRegionId(final Long firstRegionId) {
        final List<Region> secondRegions = regionRepository.findSecondAllByFirstRegionId(firstRegionId);

        if (secondRegions.isEmpty()) {
            throw new RegionNotFoundException("지정한 지역이 없습니다.");
        }

        return secondRegions.stream()
                            .map(ReadRegionDto::from)
                            .toList();
    }

    public List<ReadRegionDto> readAllThirdByFirstAndSecondRegionId(
            final Long firstRegionId,
            final Long secondRegionId
    ) {
        final List<Region> thirdRegions = regionRepository.findThirdAllByFirstAndSecondRegionId(
                firstRegionId,
                secondRegionId
        );

        if (thirdRegions.isEmpty()) {
            throw new RegionNotFoundException("지정한 지역이 없습니다.");
        }

        return thirdRegions.stream()
                           .map(ReadRegionDto::from)
                           .toList();
    }
}
