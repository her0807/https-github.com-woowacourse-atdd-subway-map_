package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundLineException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse createLine(LineRequest line) {
        Line newLine = Line.from(line);
        validateDuplicateName(newLine);

        Line createdLine = lineDao.save(newLine);

        sectionDao.save(createdLine.getId(),
                new Section(line.getUpStationId(), line.getDownStationId(), new Distance(line.getDistance())));

        return LineResponse.from(createdLine, getStationResponsesByLineId(createdLine.getId()));
    }

    private void validateDuplicateName(Line line) {
        boolean isExisting = lineDao.findByName(line.getName()).isPresent();

        if (isExisting) {
            throw new DuplicateNameException();
        }
    }

    public List<LineResponse> getAllLines() {
        return lineDao.findAll()
                .stream()
                .map(line -> LineResponse.from(line, getStationResponsesByLineId(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse getLineById(Long id) {
        return lineDao.findById(id)
                .map(line -> LineResponse.from(line, getStationResponsesByLineId(id)))
                .orElseThrow(NotFoundLineException::new);
    }

    public void update(Long id, LineUpdateRequest line) {
        Line newLine = new Line(id, line.getName(), line.getColor());
        validateExistById(id);
        lineDao.update(id, newLine);
    }

    public void delete(Long id) {
        validateExistById(id);
        lineDao.deleteById(id);
    }

    private void validateExistById(Long id) {
        boolean isExisting = lineDao.findById(id).isPresent();

        if (!isExisting) {
            throw new NotFoundLineException();
        }
    }

    private List<StationResponse> getStationResponsesByLineId(Long lineId) {
        List<Long> stationIds = getStationIdsByLineId(lineId);
        List<Station> stations = stationDao.findAllByIds(stationIds);

        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    private List<Long> getStationIdsByLineId(Long lineId) {
        List<Section> sections = sectionDao.findSectionsByLineId(lineId).getValue();

        return Stream.concat(
                sections.stream().map(Section::getDownStationId),
                sections.stream().map(Section::getUpStationId)
        ).distinct().collect(Collectors.toList());
    }
}
