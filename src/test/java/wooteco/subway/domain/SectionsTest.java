package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("추가하려는 구간의 두 역이 이미 존재하면 예외 발생")
    @Test
    void 구간_추가_두_역_이미_존재_예외발생() {
        Section section1 = new Section(new Station("당산역"), new Station("합정역"), 1);
        Section section2 = new Section(new Station("합정역"), new Station("홍대입구역"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(section1, section2)));

        Section target = new Section(new Station("당산역"), new Station("홍대입구역"), 1);

        assertThatThrownBy(() -> sections.add(target))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("추가하려는 구간과 겹치는 역이 없다면 예외 발생")
    @Test
    void 구간_추가_겹치는_역_없음_예외발생() {
        Section section1 = new Section(new Station("당산역"), new Station("합정역"), 1);
        Section section2 = new Section(new Station("합정역"), new Station("홍대입구역"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(section1, section2)));

        Section target = new Section(new Station("강남역"), new Station("선릉역"), 1);

        assertThatThrownBy(() -> sections.add(target))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("상행으로 구간 연장")
    @Test
    void 구간_추가_상행_연장() {
        Section section = new Section(new Station("합정역"), new Station("홍대입구역"), 1);
        Sections sections = new Sections(section);

        Section target = new Section(new Station("당산역"), new Station("합정역"), 1);
        sections.add(target);
        List<Section> result = sections.getSections();

        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(target),
                () -> assertThat(result.get(1)).isEqualTo(section)
        );
    }

    @DisplayName("하행으로 구간 연장")
    @Test
    void 구간_추가_하행_연장() {
        Section section = new Section(new Station("당산역"), new Station("합정역"), 1);
        Sections sections = new Sections(section);

        Section target = new Section(new Station("합정역"), new Station("신촌역"), 1);
        sections.add(target);
        List<Section> result = sections.getSections();

        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(section),
                () -> assertThat(result.get(1)).isEqualTo(target)
        );
    }

    @DisplayName("상행역을 공유하는 구간 추가")
    @Test
    void 구간_추가_상행역_공유1() {
        Section AtoC = new Section(new Station("A"), new Station("C"), 5);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoC, CtoD)));

        Section AtoB = new Section(new Station("A"), new Station("B"), 3);
        sections.add(AtoB);
        List<Section> result = sections.getSections();

        Section BtoC = new Section(new Station("B"), new Station("C"), 2);
        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoC),
                () -> assertThat(result.get(2)).isEqualTo(CtoD)
        );
    }

    @DisplayName("상행역을 공유하는 구간 추가")
    @Test
    void 구간_추가_상행역_공유2() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 2);
        Section BtoD = new Section(new Station("B"), new Station("D"), 4);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoB, BtoD)));

        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        sections.add(BtoC);
        List<Section> result = sections.getSections();

        Section CtoD = new Section(new Station("C"), new Station("D"), 3);
        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoC),
                () -> assertThat(result.get(2)).isEqualTo(CtoD)
        );
    }

    @DisplayName("하행역을 공유하는 구간 추가")
    @Test
    void 구간_추가_하행역_공유1() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        Section BtoD = new Section(new Station("B"), new Station("D"), 5);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoB, BtoD)));

        Section CtoD = new Section(new Station("C"), new Station("D"), 2);
        sections.add(CtoD);
        List<Section> result = sections.getSections();

        Section BtoC = new Section(new Station("B"), new Station("C"), 3);
        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoC),
                () -> assertThat(result.get(2)).isEqualTo(CtoD)
        );
    }

    @DisplayName("하행역을 공유하는 구간 추가")
    @Test
    void 구간_추가_하행역_공유2() {
        Section AtoC = new Section(new Station("A"), new Station("C"), 5);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoC, CtoD)));

        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        sections.add(BtoC);
        List<Section> result = sections.getSections();

        Section AtoB = new Section(new Station("A"), new Station("B"), 4);
        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoC),
                () -> assertThat(result.get(2)).isEqualTo(CtoD)
        );
    }

    @DisplayName("상행 종점역 제거")
    @Test
    void 상행_종점역_제거() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoB, BtoC, CtoD)));

        sections.delete(new Station("A"));
        List<Section> result = sections.getSections();

        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(BtoC),
                () -> assertThat(result.get(1)).isEqualTo(CtoD),
                () -> assertThatThrownBy(() -> result.get(2))
                        .isInstanceOf(IndexOutOfBoundsException.class)
        );
    }

    @DisplayName("상행 종점역 제거")
    @Test
    void 하행_종점역_제거() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoB, BtoC, CtoD)));

        sections.delete(new Station("D"));
        List<Section> result = sections.getSections();

        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoC),
                () -> assertThatThrownBy(() -> result.get(2))
                        .isInstanceOf(IndexOutOfBoundsException.class)
        );
    }

    @DisplayName("중간역 제거")
    @Test
    void 중간역_제거1() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoB, BtoC, CtoD)));

        Section AtoC = new Section(new Station("A"), new Station("C"), 2);
        sections.delete(new Station("B"));
        List<Section> result = sections.getSections();

        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoC),
                () -> assertThat(result.get(1)).isEqualTo(CtoD),
                () -> assertThatThrownBy(() -> result.get(2))
                        .isInstanceOf(IndexOutOfBoundsException.class)
        );
    }

    @DisplayName("중간역 제거")
    @Test
    void 중간역_제거2() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        Section CtoD = new Section(new Station("C"), new Station("D"), 2);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoB, BtoC, CtoD)));

        Section BtoD = new Section(new Station("B"), new Station("D"), 3);
        sections.delete(new Station("C"));
        List<Section> result = sections.getSections();

        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoD),
                () -> assertThatThrownBy(() -> result.get(2))
                        .isInstanceOf(IndexOutOfBoundsException.class)
        );
    }

    @DisplayName("섞인 구간을 순서대로 정렬해 생성")
    @Test
    void 섞인_구간_정렬_생성() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Section DtoE = new Section(new Station("D"), new Station("E"), 1);

        ArrayList<Section> mixed = new ArrayList<>(List.of(AtoB, DtoE, BtoC, CtoD));
        Sections sections = Sections.from(mixed);
        List<Section> result = sections.getSections();

        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoC),
                () -> assertThat(result.get(2)).isEqualTo(CtoD),
                () -> assertThat(result.get(3)).isEqualTo(DtoE)
        );
    }
}