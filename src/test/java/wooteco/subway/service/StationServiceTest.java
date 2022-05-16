package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {

    private static final String FIRST_STATION_NAME = "신설동역";
    private static final String SECOND_STATION_NAME = "성수역";

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest test = new StationRequest(FIRST_STATION_NAME);
        given(stationDao.findByName(FIRST_STATION_NAME))
            .willReturn(Optional.empty());
        given(stationDao.save(any()))
            .willReturn(new Station(1L, test.getName()));
        // when
        StationResponse stationResponse = stationService.createStation(test);
        // then
        assertAll(
            () -> assertThat(stationResponse.getId()).isEqualTo(1L),
            () -> assertThat(stationResponse.getName()).isEqualTo(FIRST_STATION_NAME)
        );
    }

    @DisplayName("지하철 역 생성 시 이름이 중복된다면 에러를 응답한다.")
    @Test
    void createStation_duplication_exception() {
        // given
        StationRequest test = new StationRequest(FIRST_STATION_NAME);
        given(stationDao.findByName(FIRST_STATION_NAME))
            .willReturn(Optional.of(new Station(1L, test.getName())));
        // then
        assertThatThrownBy(() -> stationService.createStation(test))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("중복되는 지하철역이 존재합니다.");
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        given(stationDao.findAll())
            .willReturn(List.of(new Station(1L, FIRST_STATION_NAME), new Station(2L, SECOND_STATION_NAME)));
        // when
        List<StationResponse> responses = stationService.showStations();
        // then
        assertAll(
            () -> assertThat(responses.size()).isEqualTo(2),
            () -> assertThat(responses.get(0).getId()).isEqualTo(1L),
            () -> assertThat(responses.get(0).getName()).isEqualTo(FIRST_STATION_NAME),
            () -> assertThat(responses.get(1).getId()).isEqualTo(2L),
            () -> assertThat(responses.get(1).getName()).isEqualTo(SECOND_STATION_NAME)
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        Station station = new Station(1L, FIRST_STATION_NAME);
        // given
        given(stationDao.findById(1L))
            .willReturn(Optional.of(station));
        // when
        stationService.deleteStation(1L);
        // then
        verify(stationDao).delete(station);
    }

    @DisplayName("삭제 요청 시 ID에 해당하는 지하철역이 없다면 에러를 응답한다.")
    @Test
    void deleteStation_noExistStation_exception() {
        // given
        given(stationDao.findById(1L))
            .willReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> stationService.deleteStation(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당 ID의 지하철역이 존재하지 않습니다.");
    }
}
