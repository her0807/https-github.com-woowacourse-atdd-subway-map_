package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private static final String LINE_DUPLICATION_NAME_EXCEPTION_MESSAGE = "[ERROR] 중복되는 이름의 지하철 노선이 존재합니다.";
    private static final String LINE_DUPLICATION_COLOR_EXCEPTION_MESSAGE = "[ERROR] 중복되는 색깔의 지하철 노선이 존재합니다.";
    private static final String NO_SUCH_LINE_EXCEPTION_MESSAGE = "[ERROR] 해당하는 ID의 지하철 노선이 존재하지 않습니다.";
    private static final String NO_SUCH_STATION_EXCEPTION_MESSAGE = "[ERROR] 존재하지 않는 지하철역입니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse createLine(@RequestBody LineRequest lineRequest) {
        if (isDuplicateName(lineRequest.getName())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_NAME_EXCEPTION_MESSAGE);
        }
        if (isDuplicateColor(lineRequest.getColor())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_COLOR_EXCEPTION_MESSAGE);
        }
        Line createdLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        sectionDao.save(new Section(createdLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(),
            lineRequest.getDistance()));
        List<StationResponse> stationResponses = generateStationResponses(lineRequest);
        return new LineResponse(createdLine.getId(), createdLine.getName(), createdLine.getColor(), stationResponses);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        List<Line> lines = lineDao.findAll();
        List<Station> stations = stationDao.findAll();
        return lines.stream()
            .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(),
                generateStationResponses(line, stations)))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse showLine(Long id) {
        Line line = findLineById(id);
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = generateStationResponses(line, stations);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line originLine = findLineById(id);
        validateUpdateDuplicateLine(originLine, lineRequest);
        lineDao.update(originLine, new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = findLineById(id);
        sectionDao.deleteByLineId(id);
        lineDao.delete(line);
    }

    private List<StationResponse> generateStationResponses(LineRequest lineRequest) {
        Optional<Station> upStation = stationDao.findById(lineRequest.getUpStationId());
        Optional<Station> downStation = stationDao.findById(lineRequest.getDownStationId());
        if (upStation.isEmpty() || downStation.isEmpty()) {
            throw new IllegalArgumentException(NO_SUCH_STATION_EXCEPTION_MESSAGE);
        }
        return List.of(new StationResponse(upStation.get().getId(), upStation.get().getName()),
            new StationResponse(downStation.get().getId(), downStation.get().getName()));
    }

    private List<StationResponse> generateStationResponses(Line line, List<Station> stations) {
        Sections sections = sectionDao.findByLineId(line.getId());
        List<Long> stationsIds = sections.getStationIds();
        return stations.stream()
            .filter(station -> stationsIds.contains(station.getId()))
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    private Line findLineById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_LINE_EXCEPTION_MESSAGE));
    }

    private void validateUpdateDuplicateLine(Line originLine, LineRequest lineRequest) {
        if (isDuplicateName(lineRequest.getName()) && isNotSameName(originLine.getName(), lineRequest.getName())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_NAME_EXCEPTION_MESSAGE);
        }
        if (isDuplicateColor(lineRequest.getColor()) && isNotSameColor(originLine.getColor(), lineRequest.getColor())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_COLOR_EXCEPTION_MESSAGE);
        }
    }

    private boolean isDuplicateName(String name) {
        return lineDao.findByName(name).isPresent();
    }

    private boolean isDuplicateColor(String color) {
        return lineDao.findByColor(color).isPresent();
    }

    private boolean isNotSameName(String originName, String updateName) {
        return !originName.equals(updateName);
    }

    private boolean isNotSameColor(String originColor, String updateColor) {
        return !originColor.equals(updateColor);
    }
}
