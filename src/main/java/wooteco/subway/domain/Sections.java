package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> sections) {
        values = sort(new ArrayList<>(sections));
    }

    private List<Section> sort(List<Section> sections) {
        List<Section> sortedSections = new ArrayList<>();
        Section first = findFirst(sections);
        while (!sections.isEmpty()) {
            sortedSections.add(first);
            sections.remove(first);
            first = nextSection(sections, first);
        }
        return sortedSections;
    }

    private Section nextSection(List<Section> sections, Section target) {
        return sections.stream()
            .filter(section -> section.hasUpSection(target))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("no"));
    }

    private Section findFirst(List<Section> sections) {
//        for (Section target : sections) {
//            for (Section value : sections) {
//                if (!target.equals(value) && value.hasDownSection(target)) {
//                    return value;
//                }
//            }
//        }
//        throw new IllegalArgumentException("시작역을 찾을 수 없습니다.");
        return sections.stream()
            .flatMap(target -> sections.stream().filter(value -> !value.equals(target) &&
                !value.hasDownSection(target)))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("시작역을 찾을 수 없습니다."));
    }

    public List<Section> getValues() {
        return List.copyOf(values);
    }
}
