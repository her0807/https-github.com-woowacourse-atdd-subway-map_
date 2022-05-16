package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

@JdbcTest
public class SectionDaoTest {
    private final SectionDao sectionDao;

    @Autowired
    private SectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("구간이 성공적으로 저장되는지 테스트")
    @Test
    void save_success() {
        sectionDao.save(new Section(1L, 2L, 10L), 1L);
        assertThat(sectionDao.findAllByLineId(1L).size()).isEqualTo(1);
    }

    @DisplayName("특정 호선의 모든 구간 갯수가 일치하는지 테스트")
    @Test
    void findAll_same_line_id() {
        sectionDao.save(new Section(1L, 2L, 10L), 1L);
        sectionDao.save(new Section(2L, 3L, 10L), 1L);
        sectionDao.save(new Section(1L, 2L, 10L), 3L);
        assertThat(sectionDao.findAllByLineId(3L).size()).isEqualTo(1);
    }

    @DisplayName("존재하는 구간 id가 있을 때 null이 아닌 구간을 가져오는지 테스트")
    @Test
    void findById_exist() {
        Section section = sectionDao.save(new Section(1L, 2L, 10L), 1L);
        assertThat(sectionDao.findById(section.getId())).isNotNull();
    }

    @DisplayName("존재하는 구간 id가 없으면 예외가 발생하는지 테스트")
    @Test
    void findById_not_exist() {
        sectionDao.save(new Section(1L, 2L, 10L), 1L);
        assertThatThrownBy(() -> sectionDao.findById(-1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("특정 역의 id로 이루어진 구간이 있는지 테스트")
    @Test
    void exist_by_station_id() {
        sectionDao.save(new Section(2L,1L, 2L, 10L), 2L);
        sectionDao.save(new Section(1L, 2L, 3L, 1L), 1L);
        sectionDao.save(new Section(1L, 7L, 8L, 10L), 1L);
        assertThat(sectionDao.isAnySectionWithStationId(2L)).isTrue();
    }
}
