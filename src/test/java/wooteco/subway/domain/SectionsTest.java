package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.IllegalSectionCreatedException;
import wooteco.subway.exception.IllegalSectionDeleteException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void init() {
        final Section section1 = new Section(1L, 2L, 10);
        final Section section2 = new Section(2L, 3L, 10);
        final Section section3 = new Section(3L, 4L, 10);
        final Section section4 = new Section(4L, 5L, 10);

        sections = new Sections(List.of(section1, section2, section3, section4));
    }

    @DisplayName("등록 할 수 있는 구간 일 때 예외를 발생시키지 않는 것을 확인한다.")
    @Test
    void validate_possible_section() {
        final Section section = new Section(2L, 6L, 5);

        sections.validatePossibleSection(section);
    }

    @DisplayName("인자로 전달 된 구간의 상행역과 하행역이 모두 이미 등록되어 있을 떄 예외를 발생시키는 지 확인한다.")
    @Test
    void validate_possible_section_all_registered_exception() {
        final Section section = new Section(2L, 3L, 5);

        assertThatThrownBy(() -> sections.validatePossibleSection(section))
                .isInstanceOf(IllegalSectionCreatedException.class);
    }

    @DisplayName("인자로 전달 된 구간의 상행역과 하행역이 모두 등록되어 있지 않을 때 예외를 발생시키는 지 확인한다.")
    @Test
    void validate_possible_section_none_registered_exception() {
        final Section section = new Section(10L, 11L, 5);

        assertThatThrownBy(() -> sections.validatePossibleSection(section))
                .isInstanceOf(IllegalSectionCreatedException.class);
    }

    @DisplayName("인자로 전달 된 구간이 기점 구간이면 True 를 반환하는 지 확인한다.")
    @Test
    void is_last_station_left_true() {
        final Section section = new Section(6L, 1L, 5);
        final boolean actual = sections.isLastStation(section);

        assertThat(actual).isTrue();
    }

    @DisplayName("인자로 전달 된 구간이 기점 구간이 아니면 False 를 반환하는 지 확인한다.")
    @Test
    void is_last_station_left_false() {
        final Section section = new Section(2L, 6L, 5);
        final boolean actual = sections.isLastStation(section);

        assertThat(actual).isFalse();
    }

    @DisplayName("인자로 전달 된 구간이 종점 구간이면 True 를 반환하는 지 확인한다.")
    @Test
    void is_last_station_right_true() {
        final Section section = new Section(5L, 6L, 5);
        final boolean actual = sections.isLastStation(section);

        assertThat(actual).isTrue();
    }

    @DisplayName("인자로 전달 된 구간이 종점 구간이 아니면 False 를 반환하는 지 확인한다.")
    @Test
    void is_last_station_right_false() {
        final Section section = new Section(2L, 6L, 5);
        final boolean actual = sections.isLastStation(section);

        assertThat(actual).isFalse();
    }

    @DisplayName("인자로 전달 된 구간의 상행 역이 기존의 구간들의 상행역 중에 일치하면 True 를 반환하는 지 확인한다.")
    @Test
    void matches_up_station_id_true() {
        final Section section = new Section(2L, 6L, 5);
        final boolean actual = sections.matchUpStationId(section);

        assertThat(actual).isTrue();
    }

    @DisplayName("인자로 전달 된 구간의 상행 역이 기존의 구간들의 상행역 중에 일치하지 않으면 False 를 반환하는 지 확인한다.")
    @Test
    void matches_up_station_id_false() {
        final Section section = new Section(5L, 6L, 5);
        final boolean actual = sections.matchUpStationId(section);

        assertThat(actual).isFalse();
    }

    @DisplayName("인자로 전달 된 구간의 하행 역이 기존의 구간들의 하행역 중에 일치하면 True 를 반환하는 지 확인한다.")
    @Test
    void matches_down_station_id_true() {
        final Section section = new Section(1L, 5L, 5);
        final boolean actual = sections.matchDownStationId(section);

        assertThat(actual).isTrue();
    }

    @DisplayName("인자로 전달 된 구간의 하행 역이 기존의 구간들의 하행역 중에 일치하지 않으면 False 를 반환하는 지 확인한다.")
    @Test
    void matches_down_station_id_false() {
        final Section section = new Section(6L, 1L, 5);
        final boolean actual = sections.matchDownStationId(section);

        assertThat(actual).isFalse();
    }

    @DisplayName("가지고 있는 두 두간이 정상적으로 합쳐지는 지 확인한다.")
    @Test
    void integrate_two_sections() {
        final Sections possibleIntegratedSections = new Sections(
                List.of(new Section(1L, 2L, 10),
                        new Section(2L, 3L, 10)));
        final Section integratedSection = possibleIntegratedSections.integrateTwoSections();

        assertThat(integratedSection.getDistance()).isEqualTo(20);
    }

    @DisplayName("가지고 있는 구간이 하나일 때 예외를 발생시키는 지 확인한다.")
    @Test
    void integrate_lack_section_exception() {
        final Sections failedToIntegrateSections = new Sections(
                List.of(new Section(1L, 2L, 10)));

        assertThatThrownBy(failedToIntegrateSections::integrateTwoSections)
                .isInstanceOf(IllegalSectionDeleteException.class);
    }

    @DisplayName("구간이 1개일 때 True 를 반환하는 지 확인한다.")
    @Test
    void hasOneSection_true() {
        final Sections sections = new Sections(
                List.of(new Section(1L, 2L, 10)));

        assertThat(sections.hasOneSection()).isTrue();
    }

    @DisplayName("구간이 1개가 아닐 때 False 를 반환하는 지 확인한다.")
    @Test
    void hasOneSection_false() {
        final Sections sections = new Sections(
                List.of(new Section(1L, 2L, 10),
                        new Section(2L, 3L, 10)));

        assertThat(sections.hasOneSection()).isFalse();
    }

    @DisplayName("구간이 2개일 때 True 를 반환하는 지 확인한다.")
    @Test
    void hasTwoSection_true() {
        final Sections sections = new Sections(
                List.of(new Section(1L, 2L, 10),
                        new Section(2L, 3L, 10)));

        assertThat(sections.hasTwoSection()).isTrue();
    }

    @DisplayName("구간이 2개가 아닐 때 False 를 반환하는 지 확인한다.")
    @Test
    void hasTwoSection_false() {
        final Sections sections = new Sections(
                List.of(new Section(1L, 2L, 10)));

        assertThat(sections.hasTwoSection()).isFalse();
    }
}
