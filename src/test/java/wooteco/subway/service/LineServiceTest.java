package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineDao lineDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineService lineService;

    private Line line;

    @BeforeEach
    void setUp() {
        line = lineDao.save(new Line("2호선", "bg-green-600"));
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void lineCreateTest() {
        final Station upStation = stationDao.save(new Station("아차산역"));
        final Station downStation = stationDao.save(new Station("군자역"));

        final LineRequest lineRequest = new LineRequest("5호선", "bg-purple-600", upStation.getId(), downStation.getId(), 3);
        final LineResponse savedLine = lineService.create(lineRequest);

        assertAll(
                () -> assertThat(savedLine.getId()).isNotNull(),
                () -> assertThat(savedLine.getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(savedLine.getColor()).isEqualTo(lineRequest.getColor())
        );
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void queryAllTest() {
        lineDao.save(new Line("5호선", "bg-purple-600"));

        final List<LineResponse> lines = lineService.getAll();

        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void queryByIdTest() {
        final LineResponse foundLine = lineService.getById(line.getId());

        assertAll(
                () -> assertThat(line.getId()).isEqualTo(foundLine.getId()),
                () -> assertThat(line.getName()).isEqualTo(foundLine.getName()),
                () -> assertThat(line.getColor()).isEqualTo(foundLine.getColor())
        );
    }

    @DisplayName("특정 노선을 수정한다.")
    @Test
    void modifyTest() {
        lineService.modify(line.getId(), new LineRequest("5호선", "bg-green-600"));

        final LineResponse foundLine = lineService.getById(line.getId());

        assertAll(
                () -> assertThat(foundLine.getId()).isEqualTo(line.getId()),
                () -> assertThat(foundLine.getName()).isEqualTo("5호선"),
                () -> assertThat(foundLine.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("존재하지 않는 노선을 수정하려고 하면 예외가 발생한다.")
    @Test
    void modifyWithExceptionTest() {
        assertThatThrownBy(() -> lineService.modify(100L, new LineRequest("5호선", "bg-purple-600")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선이 존재하지 않습니다.");
    }

    @DisplayName("특정 노선을 삭제한다.")
    @Test
    void removeTest() {
        lineService.remove(line.getId());

        assertThatThrownBy(() -> lineService.getById(line.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선이 존재하지 않습니다.");
    }

    @DisplayName("존재하지 않는 노선을 삭제하려고 하면 예외가 발생한다.")
    @Test
    void removeWithExceptionTest() {
        assertThatThrownBy(() -> lineService.remove(100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선이 존재하지 않습니다.");
    }
}
