package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class StationService {

    private static final String STATION_DUPLICATION_EXCEPTION_MESSAGE = "[ERROR] 중복되는 지하철역이 존재합니다.";
    private static final String NO_SUCH_STATION_EXCEPTION_MESSAGE = "[ERROR] 해당 ID의 지하철역이 존재하지 않습니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse createStation(StationRequest stationRequest) {
        if (stationDao.findByName(stationRequest.getName()).isPresent()) {
            throw new IllegalArgumentException(STATION_DUPLICATION_EXCEPTION_MESSAGE);
        }
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> showStations() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(it -> new StationResponse(it.getId(), it.getName()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStation(Long id) {
        Station station = stationDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_STATION_EXCEPTION_MESSAGE));
        stationDao.delete(station);
    }
}
