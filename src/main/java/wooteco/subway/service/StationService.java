package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class StationService {

    private static final String STATION_DUPLICATION = "이미 등록된 지하철 역입니다.";
    private final JdbcStationDao stationDao;
    private final JdbcSectionDao sectionDao;

    public StationService(JdbcStationDao stationDao, JdbcSectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        validateDuplication(stationRequest.getName());

        String name = stationRequest.getName();
        return new StationResponse(stationDao.save(name), name);
    }

    private void validateDuplication(String name) {
        if (stationDao.isExistStation(name)) {
            throw new IllegalArgumentException(STATION_DUPLICATION);
        }
    }

    public StationResponse getStation(Long id) {
        Station station = stationDao.findById(id);
        return new StationResponse(station.getId(), station.getName());
    }

    public List<StationResponse> getStations() {
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return stationResponses;
    }

    public boolean deleteStation(Long id) {
        if (sectionDao.isExistByStationId(id)) {
            throw new IllegalArgumentException("구간이 등록되어 있기 때문에 역을 삭제할 수 없습니다.");
        }
        return stationDao.deleteById(id);
    }
}
