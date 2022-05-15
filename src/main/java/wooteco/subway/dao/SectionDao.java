package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Section;

public interface SectionDao {
    Long save(Section section);

    Long update(Long id, Section section);

    Section findById(Long id);

    List<Section> findAllByLineId(Long lineId);

    List<Section> findAllByStationId(Long stationId);

    Optional<Section> findByLineIdAndUpStationId(Long lineId, Long upStationId);

    Optional<Section> findByLineIdAndDownStationId(Long lineId, Long downStationId);

    void deleteById(Long id);

    void deleteAllByLineId(Long lineId);

    void deleteByLineIdAndStationId(Long lineId, Long stationId);
}
