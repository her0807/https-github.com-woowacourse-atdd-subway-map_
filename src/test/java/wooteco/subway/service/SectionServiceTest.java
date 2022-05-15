package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.dao.FakeSectionDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.section.NonExistenceSectionDeletion;

class SectionServiceTest {

    private final SectionDao sectionDao = new FakeSectionDao();
    private final SectionService sectionService = new SectionService(sectionDao);

    private final Section first = new Section(1L, 2L, 3L, 8);
    private final Section second = new Section(1L, 3L, 4L, 8);

    private final Section savedFirstSection = new Section(1L, 1L, 2L, 3L, 8);
    private final Section savedSecondSection = new Section(2L, 1L, 3L, 4L, 8);

    @BeforeEach
    void setUp() {
        sectionDao.save(first);
        sectionDao.save(second);
    }

    @DisplayName("새로운 구간을 기존 노선의 앞 혹은 뒤에 등록할 수 있다.")
    @ParameterizedTest
    @CsvSource({"4, 5", "1, 2"})
    void saveBackOrForth(Long upStationId, Long downStationId) {
        int distance = 2;
        sectionService.connectNewSection(1L, upStationId, downStationId, distance);
        Section newSection = new Section(3L, 1L, upStationId, downStationId, distance);

        List<Section> sections = sectionDao.findByLineId(1L);
        assertThat(sections).contains(savedFirstSection, savedSecondSection, newSection);
    }

    @DisplayName("새로운 구간을 같은 상행선을 가진 구간의 사이에 등록할 수 있다.")
    @Test
    void addBetweenBasedOnUpStation() {
        Long upStationId = 2L;
        Long downStationId = 5L;
        int distance = 2;
        sectionService.connectNewSection(1L, upStationId, downStationId, distance);
        Section newSection = new Section(3L, 1L, upStationId, downStationId, distance);
        Section changedSection = new Section(1L, 1L, 5L, 3L, 6);

        List<Section> sections = sectionDao.findByLineId(1L);
        assertThat(sections).contains(changedSection, savedSecondSection, newSection);
    }

    @DisplayName("새로운 구간을 같은 하행선을 가진 구간의 사이에 등록할 수 있다.")
    @Test
    void addBetweenBasedOnDownStation() {
        Long upStationId = 5L;
        Long downStationId = 4L;
        int distance = 2;
        sectionService.connectNewSection(1L, upStationId, downStationId, distance);
        Section newSection = new Section(3L, 1L, upStationId, downStationId, distance);
        Section changedSection = new Section(2L, 1L, 3L, 5L, 6);

        List<Section> sections = sectionDao.findByLineId(1L);
        assertThat(sections).contains(savedFirstSection, changedSection, newSection);
    }

    @DisplayName("상행역을 삭제할 수 있다.")
    @Test
    void deleteForth() {
        sectionService.deleteStation(1L, 2L);

        List<Section> sections = sectionDao.findByLineId(1L);

        assertThat(sections).contains(savedSecondSection)
                .hasSize(1);
    }

    @DisplayName("하행역을 삭제할 수 있다.")
    @Test
    void deleteBank() {
        sectionService.deleteStation(1L, 4L);

        List<Section> sections = sectionDao.findByLineId(1L);

        assertThat(sections).contains(savedFirstSection)
                .hasSize(1);
    }

    @DisplayName("구간의 사이에 있는 역을 삭제할 수 있다.")
    @Test
    void deleteBetween() {
        sectionService.deleteStation(1L, 3L);

        List<Section> sections = sectionDao.findByLineId(1L);

        assertThat(sections).contains(new Section(3L, 1L, 2L, 4L, 16))
                .hasSize(1);
    }

    @DisplayName("존재하지 않는 구간을 삭제하려고 하면 예외를 발생시킨다.")
    @Test
    void delete_exceptionByNonExistenceSection() {
        sectionService.deleteStation(1L, 3L);

        assertThatThrownBy(() -> sectionService.deleteStation(1L, 3L))
                .isInstanceOf(NonExistenceSectionDeletion.class);
    }
}
