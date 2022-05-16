package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> create(@PathVariable final Long lineId, @RequestBody final SectionRequest sectionRequest) {
        sectionService.create(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> delete(@PathVariable final Long lineId, @RequestParam final Long stationId) {
        sectionService.deleteSectionByStationId(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
