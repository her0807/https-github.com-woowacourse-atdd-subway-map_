package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.LineDaoImpl;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.SectionDaoImpl;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.StationDaoImpl;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@JdbcTest
@Sql("/init.sql")
public class LineServiceTest {

    private StationDao stationDao;
    private LineDao lineDao;
    private SectionDao sectionDao;
    private LineService lineService;

    @Autowired
    public LineServiceTest(JdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDaoImpl(jdbcTemplate);
        this.lineDao = new LineDaoImpl(jdbcTemplate);
        this.sectionDao = new SectionDaoImpl(jdbcTemplate);
        this.lineService = new LineService(stationDao, lineDao, sectionDao);
    }

    @BeforeEach
    void beforeEach() {
        stationDao.insert(new Station("강남역"));
        stationDao.insert(new Station("역삼역"));
        stationDao.insert(new Station("정자역"));
        stationDao.insert(new Station("선릉역"));
    }

    @Test
    @DisplayName("지하철 노선을 저장할 수 있다.")
    void insertLine() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        LineResponse lineResponse = lineService.insertLine(lineRequest);

        Line line = lineDao.findById(lineResponse.getId());
        assertThat(line.getName()).isEqualTo("신분당선");
    }

    @Test
    @DisplayName("이름이 중복된 지하철 노선은 저장할 수 없다.")
    void insertLineDuplicateColor() {
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        LineRequest lineRequest2 = new LineRequest("신분당선", "bg-red-500", 3L, 4L, 5);
        lineService.insertLine(lineRequest1);

        assertThatThrownBy(() -> lineService.insertLine(lineRequest2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LineService.NAME_DUPLICATE_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("이름이 중복된 지하철 노선은 저장할 수 없다.")
    void insertLineDuplicateName() {
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        LineRequest lineRequest2 = new LineRequest("분당선", "bg-red-600", 3L, 4L, 5);
        lineService.insertLine(lineRequest1);

        assertThatThrownBy(() -> lineService.insertLine(lineRequest2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LineService.COLOR_DUPLICATE_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("지하철 노선들을 조회할 수 있다.")
    void findLines() {
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        LineRequest lineRequest2 = new LineRequest("분당선", "bg-red-500", 3L, 4L, 5);
        lineService.insertLine(lineRequest1);
        lineService.insertLine(lineRequest2);

        List<LineResponse> lines = lineService.findLines();
        List<String> lineNames = lines.stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());

        assertThat(lineNames).contains("신분당선", "분당선");
    }

    @Test
    @DisplayName("지하철 노선을 조회할 수 있다.")
    void findLine() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        LineResponse lineResponse = lineService.insertLine(lineRequest);

        LineResponse lineResponse1 = lineService.findLine(lineResponse.getId());

        assertThat(lineResponse1.getName()).isEqualTo("신분당선");
    }

    @Test
    @DisplayName("지하철 노선을 업데이트할 수 있다.")
    void updateLine() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        LineResponse lineResponse = lineService.insertLine(lineRequest);

        lineService.updateLine(lineResponse.getId(), new LineRequest("분당선", "bg-green-500"));
        Line line = lineDao.findById(lineResponse.getId());

        assertThat(line.getName()).isEqualTo("분당선");
    }

    @Test
    @DisplayName("지하철 노선을 지울 수 있다.")
    void deleteLine() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        LineResponse lineResponse = lineService.insertLine(lineRequest);

        lineService.deleteLine(lineResponse.getId());
        List<Line> lines = lineDao.findAll();

        assertThat(lines).isEmpty();
    }
}
