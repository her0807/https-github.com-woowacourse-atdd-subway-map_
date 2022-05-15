package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {

    @Mock
    private JdbcStationDao jdbcStationDao;

    @Mock
    private JdbcSectionDao jdbcSectionDao;

    @InjectMocks
    private StationService stationService;

    @DisplayName("지하철역을 등록한다.")
    @Test
    void createStation() {
        doReturn(1L)
                .when(jdbcStationDao).save("강남역");
        StationResponse stationResponse = stationService.createStation(new StationRequest("강남역"));
        assertAll(
                () -> stationResponse.getId().equals(1L),
                () -> stationResponse.getName().equals("강남역")
        );

    }

    @DisplayName("지하철역 목록들을 조회한다.")
    @Test
    void getStations() {
        doReturn(List.of(new Station("강남역"), new Station("잠실역")))
                .when(jdbcStationDao).findAll();

        List<StationResponse> stationResponses = stationService.getStations();

        assertThat(stationResponses.size()).isEqualTo(2);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        Long stationId = 1L;
        doReturn(false)
                .when(jdbcSectionDao).isExistByStationId(stationId);

        doReturn(true)
                .when(jdbcStationDao).deleteById(stationId);
        boolean isDeleted = stationService.deleteStation(stationId);
        assertThat(isDeleted).isTrue();
    }

    @DisplayName("지하철역이 구간에 등록되어 있으면 지하철역을 삭제할 수 없다.")
    @Test
    void deleteStation_exception() {
        Long stationId = 1L;
        doReturn(true)
                .when(jdbcSectionDao).isExistByStationId(stationId);

        assertThatThrownBy(() -> stationService.deleteStation(stationId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철역을 단일 조회한다.")
    @Test
    void getStation() {
        doReturn(new Station(1L, "강남역"))
                .when(jdbcStationDao).findById(1L);

        StationResponse stationResponse = stationService.getStation(1L);

        assertAll(
                () -> stationResponse.getId().equals(1L),
                () -> stationResponse.getName().equals("강남역")
        );
    }
}
