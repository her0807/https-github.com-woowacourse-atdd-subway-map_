package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.dto.StationDto;
import wooteco.subway.exception.station.DuplicatedStationNameException;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new FakeStationDao());
    }

    @DisplayName("추가하려는 역의 이름이 이미 존재하면 예외를 발생시킨다.")
    @Test
    void createStation_exception() {
        stationService.save("서울역");

        assertThatThrownBy(() -> stationService.save("서울역"))
                .isInstanceOf(DuplicatedStationNameException.class);
    }

    @DisplayName("새로운 역을 추가할 수 있다.")
    @Test
    void createStation_success() {
        StationDto stationDto = stationService.save("서울역");

        assertThat(stationDto.getName()).isEqualTo("서울역");
    }

    @Test
    @DisplayName("Id값에 해당 하는 지하철 역을 삭제한다.")
    void deleteById() {
        stationService.save("서울역");
        stationService.deleteById(1L);

        assertThat(stationService.findAll()).isEmpty();
    }

    @DisplayName("지하철 역의 id들을 받아, id 순서대로 StationResponse 객체들을 반환한다.")
    @Test
    void findByIds() {
        stationService.save("서울역");
        stationService.save("강남역");
        stationService.save("선릉역");

        List<StationDto> stations = stationService.findByIds(Arrays.asList(3L, 1L, 2L));

        assertAll(
                () -> assertThat(stations.get(0).getId()).isEqualTo(3L),
                () -> assertThat(stations.get(0).getName()).isEqualTo("선릉역"),
                () -> assertThat(stations.get(1).getId()).isEqualTo(1L),
                () -> assertThat(stations.get(1).getName()).isEqualTo("서울역"),
                () -> assertThat(stations.get(2).getId()).isEqualTo(2L),
                () -> assertThat(stations.get(2).getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void findAll() {
        stationService.save("서울역");
        stationService.save("선릉역");

        List<StationDto> stationResponses = stationService.findAll();

        assertAll(
                () -> assertThat(stationResponses.get(0).getName()).isEqualTo("서울역"),
                () -> assertThat(stationResponses.get(1).getName()).isEqualTo("선릉역"),
                () -> assertThat(stationResponses.get(0).getId()).isEqualTo(1L),
                () -> assertThat(stationResponses.get(1).getId()).isEqualTo(2L)
        );
    }
}
