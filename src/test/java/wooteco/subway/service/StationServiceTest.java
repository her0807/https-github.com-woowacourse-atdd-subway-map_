package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.StationDaoImpl;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@JdbcTest
public class StationServiceTest {

    private StationDao stationDao;
    private StationService stationService;

    @Autowired
    public StationServiceTest(JdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDaoImpl(jdbcTemplate);
        this.stationService = new StationService(stationDao);
    }

    @Test
    @DisplayName("지하철 역을 저장할 수 있다.")
    void insert() {
        StationRequest stationRequest = new StationRequest("강남역");
        StationResponse stationResponse = stationService.insertStation(stationRequest);

        assertThat(stationResponse.getName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("이름이 중복된 지하철 역은 저장할 수 없다.")
    void insertDuplicateName() {
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("강남역");
        stationService.insertStation(stationRequest1);

        assertThatThrownBy(() -> stationService.insertStation(stationRequest2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(StationService.DUPLICATE_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("지하철 역들을 조회할 수 있다.")
    void findStations() {
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("선릉역");
        stationService.insertStation(stationRequest1);
        stationService.insertStation(stationRequest2);

        List<StationResponse> stationResponses = stationService.findStations();
        List<String> stationNames = stationResponses.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(stationNames).contains("강남역", "선릉역");
    }

    @Test
    @DisplayName("지하철 역을 지울 수 있다.")
    void deleteStation() {
        StationRequest stationRequest = new StationRequest("강남역");
        StationResponse stationResponse = stationService.insertStation(stationRequest);
        Long id = stationResponse.getId();

        stationService.deleteStation(id);
        List<Station> stations = stationDao.findAll();

        assertThat(stations).isEmpty();
    }
}
