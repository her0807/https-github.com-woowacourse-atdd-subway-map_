package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.exception.SubwayException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class LineDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        this.lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("노선 추가 성공 테스트")
    @Test
    void successSaveTest() {
        assertDoesNotThrow(() ->
                lineDao.save(new Line("신분당선", "black"))
        );
    }

    @DisplayName("노선 추가 실패 테스트")
    @Test
    void failSaveTest() {
        lineDao.save(new Line("신분당선", "black"));

        assertThatThrownBy(() -> {
            lineDao.save(new Line("신분당선", "red"));
        }).isInstanceOf(SubwayException.class);
    }

    @DisplayName("line 비어있는 리스트 전체 조회 테스트")
    @Test
    void findAllTestWhenEmpty() {
        List<Line> lines = lineDao.findAll();

        assertThat(lines).isEmpty();
    }

    @DisplayName("line 전체 조회 테스트")
    @Test
    void findAllLineTest() {
        lineDao.save(new Line("신분당선", "black"));
        lineDao.save(new Line("2호선", "white"));
        List<Line> lines = lineDao.findAll();
        assertThat(lines).hasSize(2)
                .containsExactly(
                        new Line(1L, "신분당선", "black"),
                        new Line(2L, "2호선", "white")
                );
    }

    @DisplayName("line 단일 조회 실패 테스트")
    @Test
    void failFindLineByIdTest() {
        assertThatThrownBy(() -> {
            lineDao.find(1L);
        }).isInstanceOf(SubwayException.class);
    }

    @DisplayName("line 단일 조회 성공 테스트")
    @Test
    void findLineByIdTest() {
        lineDao.save(new Line("신분당선", "black"));
        assertDoesNotThrow(() -> lineDao.find(1L));
    }

    @DisplayName("line 삭제 실패 테스트")
    @Test
    void failDeleteLineTest() {
        assertThatThrownBy(() -> {
            lineDao.delete(1L);
        }).isInstanceOf(SubwayException.class);
    }

    @DisplayName("line 삭제 성공 테스트")
    @Test
    void deleteLineTest() {
        lineDao.save(new Line("신분당선", "black"));
        assertDoesNotThrow(() -> lineDao.delete(1L));
    }

    @DisplayName("존재하지 않는 line 수정 실패 테스트")
    @Test
    void failModifyLineNotExistsTest() {
        assertThatThrownBy(
                () -> lineDao.modify(1L, new Line())
        ).isInstanceOf(SubwayException.class);
    }

    @DisplayName("중복되는 line 수정 실패 테스트")
    @Test
    void failModifiedLineSameIdTest() {
        lineDao.save(new Line("신분당선", "black"));
        lineDao.save(new Line("2호선", "black"));
        assertThatThrownBy(
                () -> lineDao.modify(2L, new Line("신분당선", "red"))
        ).isInstanceOf(SubwayException.class);
    }

    @DisplayName("line 수정 성공 테스트")
    @Test
    void modifyLineTest() {
        lineDao.save(new Line("신분당선", "black"));
        assertDoesNotThrow(
                () -> lineDao.modify(1L, new Line("2호선", "black"))
        );
    }
}