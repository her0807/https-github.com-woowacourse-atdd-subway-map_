package wooteco.subway.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateLineException;
import wooteco.subway.exception.NoSuchLineException;

@JdbcTest
class LineJdbcDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineJdbcDao dao;

    @BeforeEach
    void setUp() {
        dao = new LineJdbcDao(jdbcTemplate);
    }

    @DisplayName("새로운 노선을 저장한다")
    @Test
    void saveLine() {
        // given
        String name = "line";
        String color = "color";
        Line line = new Line(name, color);

        // when
        Long savedId = dao.save(line);

        // then
        Line findLine = dao.findById(savedId);
        assertAll(
                () -> assertThat(findLine.getName()).isEqualTo(name),
                () -> assertThat(findLine.getColor()).isEqualTo(color)
        );
    }

    @DisplayName("같은 이름의 노선이 있는 경우 예외를 던진다")
    @Test
    void throwExceptionWhenHasDuplicateName() {
        // given
        String name = "line";
        dao.save(new Line(name, "color"));

        // when & then
        assertThatThrownBy(() -> dao.save(new Line(name, "color2")))
                .isInstanceOf(DuplicateLineException.class);
    }

    @DisplayName("같은 색깔의 노선이 있는 경우 예외를 던진다")
    @Test
    void throwExceptionWhenHasDuplicateColor() {
        // given
        String color = "red";
        dao.save(new Line("test", color));

        // when & then
        assertThatThrownBy(() -> dao.save(new Line("line", color)))
                .isInstanceOf(DuplicateLineException.class);
    }

    @DisplayName("노선 목록을 조회한다")
    @Test
    void findAll() {
        // given
        dao.save(new Line("line1", "color1"));
        dao.save(new Line("line2", "color2"));
        dao.save(new Line("line3", "color3"));

        // when
        List<Line> lines = dao.findAll();

        // then
        assertThat(lines).hasSize(3);
    }

    @DisplayName("id로 노선을 조회한다")
    @Test
    void findById() {
        // given
        dao.save(new Line("line1", "color1"));
        Line line = new Line("line2", "color2");
        Long savedId = dao.save(line);

        // when
        Line findLine = dao.findById(savedId);

        // then
        assertAll(
                () -> assertThat(findLine.getName()).isEqualTo(line.getName()),
                () -> assertThat(findLine.getColor()).isEqualTo(line.getColor())
        );
    }

    @DisplayName("존재하지 않는 id로 노선을 조회하면 예외가 발생한다")
    @Test
    void throwExceptionWhenTargetLineDoesNotExist() {
        assertThatThrownBy(() -> dao.findById(1L))
                .isInstanceOf(NoSuchLineException.class);
    }

    @DisplayName("노선 정보를 수정한다")
    @Test
    void update() {
        // given
        Long savedId = dao.save(new Line("line", "color"));
        String changedName = "changedName";
        String changedColor = "changedColor";

        // when
        Long updateId = dao.update(savedId, new Line(changedName, changedColor));

        // then
        Line findLine = dao.findById(updateId);
        assertAll(
                () -> assertThat(findLine.getName()).isEqualTo(changedName),
                () -> assertThat(findLine.getColor()).isEqualTo(changedColor)
        );
    }

    @DisplayName("기존에 존재하는 노선 이름으로 이름을 수정하면 예외가 발생한다")
    @Test
    void throwExceptionWhenUpdateToExistName() {
        // given
        String name = "line";
        dao.save(new Line(name, "color"));

        Line duplicateName = new Line("test", "test");
        Long savedId = dao.save(duplicateName);

        //when, then
        assertThatThrownBy(() -> dao.update(savedId, new Line(name, "changedColor")))
                .isInstanceOf(DuplicateLineException.class);
    }

    @DisplayName("기존에 존재하는 노선 색깔으로 색깔을 수정하면 예외가 발생한다")
    @Test
    void throwExceptionWhenUpdateToExistColor() {
        // given
        String color = "red";
        dao.save(new Line("line", color));

        Line toBeUpdate = new Line("duplicateColorLine", "test");
        Long savedId = dao.save(toBeUpdate);

        //when, then
        assertThatThrownBy(() -> dao.update(savedId, new Line("duplicateColorLine", color)))
                .isInstanceOf(DuplicateLineException.class);
    }

    @DisplayName("없는 노선 정보를 변경하려 할 때, 예외를 던진다")
    @Test
    void throwExceptionWhenTryToUpdateNoLine() {
        assertThatThrownBy(() -> dao.update(1L, new Line("changedName", "changedColor")))
                .isInstanceOf(NoSuchLineException.class);
    }

    @DisplayName("노선을 제거한다")
    @Test
    void deleteById() {
        // given
        Long savedId1 = dao.save(new Line("line1", "color1"));
        Long savedId2 = dao.save(new Line("line2", "color2"));

        // when
        dao.deleteById(savedId1);

        // then
        assertThat(dao.findAll()).hasSize(1);
    }
}
