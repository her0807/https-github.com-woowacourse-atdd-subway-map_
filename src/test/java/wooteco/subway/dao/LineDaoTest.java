package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
public class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 노선을 저장한다.")
    void save() {
        final Line line = new Line("신분당선", "bg-red-600");

        final Long id = lineDao.save(line);

        final Line foundLine = lineDao.findById(id).get();
        assertAll(() -> {
            assertThat(foundLine.getId()).isNotNull();
            assertThat(foundLine.getName()).isEqualTo(line.getName());
            assertThat(foundLine.getColor()).isEqualTo(line.getColor());
        });
    }

    @Test
    @DisplayName("같은 이름의 노선을 저장하는 경우, 예외가 발생한다.")
    void saveDuplicateName() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("신분당선", "bg-blue-700");
        lineDao.save(line1);

        assertThatThrownBy(() -> lineDao.save(line2))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("같은 색상의 노선을 저장하는 경우, 예외가 발생한다.")
    void saveDuplicateColor() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("분당선", "bg-red-600");
        lineDao.save(line1);

        assertThatThrownBy(() -> lineDao.save(line2))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("모든 지하철 노선을 조회한다.")
    void findAll() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("분당선", "bg-black-000");
        lineDao.save(line1);
        lineDao.save(line2);

        final List<Line> lines = lineDao.findAll();

        assertAll(() -> {
            assertThat(lines).hasSize(2);
            assertThat(lines.get(0).getName()).isEqualTo(line1.getName());
            assertThat(lines.get(0).getColor()).isEqualTo(line1.getColor());
            assertThat(lines.get(1).getName()).isEqualTo(line2.getName());
            assertThat(lines.get(1).getColor()).isEqualTo(line2.getColor());
        });
    }

    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void find() {
        final Line line = new Line("신분당선", "bg-red-600");
        final Long id = lineDao.save(line);

        final Line foundLine = lineDao.findById(id).get();

        assertAll(() -> {
            assertThat(foundLine.getName()).isEqualTo(line.getName());
            assertThat(foundLine.getColor()).isEqualTo(line.getColor());
        });
    }

    @Test
    @DisplayName("존재하지 않는 Id 조회 시, 예외를 발생한다.")
    void findNotExistId() {
        final long id = 1L;

        assertThatThrownBy(() -> lineDao.findById(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("노선 정보를 업데이트 한다.")
    void update() {
        final Line line = new Line("신분당선", "bg-red-600");
        final Long id = lineDao.save(line);

        final String updateName = "분당선";
        String updateColor = "bg-blue-900";
        lineDao.update(id, updateName, updateColor);

        final Line updatedLine = lineDao.findById(id).get();
        assertAll(() -> {
            assertThat(updatedLine.getName()).isEqualTo(updateName);
            assertThat(updatedLine.getColor()).isEqualTo(updateColor);
        });
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void delete() {
        final Line line = new Line("신분당선", "bg-red-600");
        final Long id = lineDao.save(line);

        lineDao.delete(id);

        assertThat(lineDao.findAll()).hasSize(0);
    }
}
