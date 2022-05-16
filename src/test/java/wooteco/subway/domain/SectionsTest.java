package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private static final Long UP_END_STATION = 1L;
    private static final Long DOWN_END_STATION = 4L;

    private Sections sections;

    @BeforeEach
    void setUp() {
        Section firstSection = new Section(UP_END_STATION, 2L, 10);
        Section secondSection = new Section(2L, 3L, 10);
        Section thirdSection = new Section(3L, DOWN_END_STATION, 10);
        sections = new Sections(List.of(firstSection, secondSection, thirdSection));
    }

    @DisplayName("상행 종점에 새로운 구간을 등록한다.")
    @Test
    void addSectionToUpStation() {
        Section upEndSection = new Section(5L, UP_END_STATION, 10);
        sections.add(upEndSection);

        assertAll(
            () -> assertThat(sections.getSections().size()).isEqualTo(4),
            () -> assertThat(hasSection(sections, upEndSection)).isTrue()
        );
    }

    @DisplayName("하행 종점에 새로운 구간을 등록한다.")
    @Test
    void addSectionToDownStation() {
        Section downEndSection = new Section(DOWN_END_STATION, 5L, 10);
        sections.add(downEndSection);

        assertAll(
            () -> assertThat(sections.getSections().size()).isEqualTo(4),
            () -> assertThat(hasSection(sections, downEndSection)).isTrue()
        );
    }

    @DisplayName("상,하행 지하철역이 구간 목록에 모두 존재한다면 예외가 발생한다.")
    @Test
    void addSection_existSameUpDownStation_exception() {
        Section addSection = new Section(3L, 1L, 6);

        assertThatThrownBy(() -> sections.add(addSection))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 상,하행 역이 구간에 모두 포함된 경우 추가할 수 없습니다.");
    }

    @DisplayName("상,하행 지하철역이 구간 목록에 모두 없다면 예외가 발생한다.")
    @Test
    void addSection_noExistSameUpDownStation_exception() {
        Section section = new Section(7L, 13L, 6);

        assertThatThrownBy(() -> sections.add(section))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 상,하행 역이 모두 구간에 존재하지 않는다면 추가할 수 없습니다.");
    }

    @DisplayName("동일한 상행 지하철역이 존재한다면 기존 구간을 분리하여 저장한다.")
    @Test
    void addSection_InsideSection_byUpStation() {
        Section section = new Section(1L, 6L, 4);
        Section splitSection = new Section(6L, 2L, 6);
        sections.add(section);

        assertAll(
            () -> assertThat(hasSection(sections, section)).isTrue(),
            () -> assertThat(hasSection(sections, splitSection)).isTrue()
        );
    }

    @DisplayName("상행이 동일한 구간이 존재한지만 새로 입력하는 구간의 길이가 크거나 같다면 예외가 발생한다.")
    @Test
    void addSection_InsideSection_byUpStation_distance_exception() {
        Section section = new Section(1L, 6L, 10);

        assertThatThrownBy(() -> sections.add(section))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    @DisplayName("동일한 하행 지하철역이 존재한다면 기존 구간을 분리하여 저장한다.")
    @Test
    void addSection_InsideSection_byDownStation() {
        Section section = new Section(6L, 2L, 4);
        Section splitSection = new Section(1L, 6L, 6);
        sections.add(section);

        assertAll(
            () -> assertThat(hasSection(sections, section)).isTrue(),
            () -> assertThat(hasSection(sections, splitSection)).isTrue()
        );
    }

    @DisplayName("하행이 동일한 구간이 존재한지만 새로 입력하는 구간의 길이가 크거나 같다면 예외가 발생한다.")
    @Test
    void addSection_InsideSection_byDownStation_distance_exception() {
        Section section = new Section(6L, 2L, 10);

        assertThatThrownBy(() -> sections.add(section))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");

    }

    @DisplayName("상행 종점을 제거할 경우 다음에 오는 역이 종점이 된다.")
    @Test
    void removeUpEndStation() {
        Section section = new Section(1L, 2L, 10);

        sections.remove(UP_END_STATION);
        assertAll(
            () -> assertThat(sections.getSections().size()).isEqualTo(2),
            () -> assertThat(hasSection(sections, section)).isFalse()
        );
    }

    @DisplayName("하행 종점을 제거할 경우 이전에 있는 역이 종점이 된다.")
    @Test
    void removeDownEndStation() {
        Section section = new Section(3L, 4L, 10);

        sections.remove(DOWN_END_STATION);
        assertAll(
            () -> assertThat(sections.getSections().size()).isEqualTo(2),
            () -> assertThat(hasSection(sections, section)).isFalse()
        );
    }

    @DisplayName("구간 중간에 있는 역 제거할 경우 각 양 옆의 구간의 합으로 재배치된다.")
    @Test
    void WhenRemoveMiddleStation_ThenMergeSideStations() {
        Long middleStationId = 2L;
        Section formerSection = new Section(1L, middleStationId, 10);
        Section laterSection = new Section(middleStationId, 3L, 10);
        Section mergedSection = new Section(1L, 3L, 20);

        sections.remove(middleStationId);
        assertAll(
            () -> assertThat(sections.getSections().size()).isEqualTo(2),
            () -> assertThat(hasSection(sections, formerSection)).isFalse(),
            () -> assertThat(hasSection(sections, formerSection)).isFalse(),
            () -> assertThat(hasSection(sections, mergedSection)).isTrue()
        );
    }

    @DisplayName("구간 목록에 하나의 구간이 존재하는 경우 삭제 시 예외가 발생한다.")
    @Test
    void removeSection_whenRemainOnlyOneSection_exception() {
        Section section = new Section(1L, 6L, 10);
        sections.remove(1L);
        sections.remove(2L);

        assertThatThrownBy(() -> sections.remove(3L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 최소 하나 이상의 구간이 존재하여야합니다.");
    }

    private boolean hasSection(Sections sections, Section section) {
        return sections.getSections().contains(section);
    }
}
