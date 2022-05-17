package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
class JdbcLineDaoTest {

    private JdbcLineDao jdbcLineDao;
    private Long id;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcLineDao = new JdbcLineDao(jdbcTemplate);
        id = jdbcLineDao.save(new Line("신분당선", "bg-red-600"));
    }

    @DisplayName("지하철 노선을 등록한다.")
    @Test
    void save() {
        Long id = jdbcLineDao.save(new Line("분당선", "bg-green-600"));
        assertThat(id).isNotNull();
    }

    @DisplayName("전체 지하철 노선을 조회한다.")
    @Test
    void findAll() {
        jdbcLineDao.save(new Line("분당선", "bg-green-600"));
        List<Line> lines = jdbcLineDao.findAll();

        assertThat(lines).containsExactly(new Line("신분당선", "bg-red-600"), new Line("분당선", "bg-green-600"));
    }

    @DisplayName("단일 지하철 노선을 조회한다.")
    @Test
    void findById() {
        Line line = jdbcLineDao.findById(id).get();

        assertThat(line).isEqualTo(new Line("신분당선", "bg-red-600"));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateById() {
        boolean isUpdated = jdbcLineDao.updateById(id, new Line("분당선", "bg-green-600"));
        assertThat(isUpdated).isTrue();
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteById() {
        boolean isDeleted = jdbcLineDao.deleteById(id);
        assertThat(isDeleted).isTrue();
    }
}
