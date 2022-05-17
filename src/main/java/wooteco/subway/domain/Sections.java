package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {

    private static final String SAME_SECTION_EXCEPTION = "동일한 Section은 추가할 수 없습니다.";
    private static final String NOT_EXIST_STATION_IN_LINE = "상행역, 하행역 둘 다 포함되지 않으면 추가할 수 없습니다.";
    private static final String EXCEED_DISTANCE = "새로 들어오는 구간의 거리가 추가될 구간보다 작아야 합니다.";
    private static final String NON_REMOVABLE_EXCEPTION = "노선에 역이 2개 존재하는 경우는 삭제할 수 없습니다.";

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public Section connectSection(Section inputSection) {
        validateSameSection(inputSection);
        Section connectedSection = selectAddPoint(inputSection);
        Direction direction = Direction.findDirection(connectedSection, inputSection);
        syncSection(connectedSection, inputSection, direction);
        sections.add(inputSection);
        return connectedSection;
    }

    private void validateSameSection(Section inputSection) {
        boolean isSameSection = sections.stream()
                .anyMatch(section -> isSameAllStations(section, inputSection));

        if (isSameSection) {
            throw new IllegalArgumentException(SAME_SECTION_EXCEPTION);
        }
    }

    private boolean isSameAllStations(Section section, Section inputSection) {
        List<Long> sectionStations = List.of(section.getUpStationId(), section.getDownStationId());
        List<Long> inputSectionStations = List.of(inputSection.getUpStationId(), inputSection.getDownStationId());

        return sectionStations.containsAll(inputSectionStations);
    }

    private Section selectAddPoint(Section inputSection) {
        if (isEdgeSection(inputSection)) {
            return findAddPoint(inputSection);
        }
        List<Section> connectableSection = findConnectableSection(inputSection);
        return findAddPoint(connectableSection, inputSection);
    }

    private boolean isEdgeSection(Section inputSection) {
        int countOfCoincidence = (int) sections.stream()
                .filter(section -> section.isExistSameStation(inputSection))
                .count();

        return countOfCoincidence == 1;
    }

    private Section findAddPoint(Section inputSection) {
        return sections.stream()
                .filter(section -> section.isExistSameStation(inputSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_STATION_IN_LINE));
    }

    private Section findAddPoint(List<Section> sections, Section inputSection) {
        return sections.stream()
                .filter(section -> {
                    Direction direction = Direction.findDirection(section, inputSection);
                    return direction == Direction.BETWEEN_UP || direction == Direction.BETWEEN_DOWN;
                })
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_STATION_IN_LINE));
    }

    private List<Section> findConnectableSection(Section inputSection) {
        return sections.stream()
                .filter(section -> section.isExistSameStation(inputSection))
                .collect(Collectors.toList());
    }

    private void syncSection(Section section, Section inputSection, Direction direction) {
        if (direction == Direction.BETWEEN_UP) {
            validateDistance(section, inputSection);
            section.update(inputSection.getDownStationId(), section.getDownStationId(),
                    section.getDistance() - inputSection.getDistance());
        }
        if (direction == Direction.BETWEEN_DOWN) {
            validateDistance(section, inputSection);
            section.update(section.getUpStationId(), inputSection.getUpStationId(),
                    section.getDistance() - inputSection.getDistance());
        }
    }

    private void validateDistance(Section section, Section inputSection) {
        if (section.getDistance() <= inputSection.getDistance()) {
            throw new IllegalArgumentException(EXCEED_DISTANCE);
        }
    }

    public Sections deleteSection(Long stationId) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException(NON_REMOVABLE_EXCEPTION);
        }
        List<Section> overlapSections = sections.stream()
                .filter(section -> (stationId.equals(section.getUpStationId())) || stationId.equals(
                        section.getDownStationId()))
                .collect(Collectors.toList());

        if (overlapSections.size() == 1) {
            sections.removeAll(overlapSections);
            return new Sections(Collections.emptyList());
        }
        deleteCenterSection(overlapSections, stationId);
        return new Sections(sections);
    }

    public boolean isExistSection() {
        return !sections.isEmpty();
    }

    public int size() {
        return sections.size();
    }

    private void deleteCenterSection(List<Section> overlapSections, Long stationId) {
        Section section = overlapSections.get(0);
        sections.removeAll(overlapSections);
        Section newSection = generateNewSection(overlapSections, section, stationId);
        sections.add(newSection);
    }

    private Section generateNewSection(List<Section> overlapSections, Section section, Long stationId) {
        if (section.getUpStationId().equals(stationId)) {
            return new Section(section.getLineId(), overlapSections.get(1).getUpStationId(),
                    section.getDownStationId(),
                    section.getDistance() + overlapSections.get(1).getDistance());
        }
        return new Section(section.getLineId(), section.getUpStationId(),
                overlapSections.get(1).getDownStationId(),
                section.getDistance() + overlapSections.get(1).getDistance());
    }

    public List<Long> sortSection() {
        List<Long> sortedStations = new ArrayList<>();
        Map<Long, Long> sectionMap = new HashMap<>();
        for (Section section : sections) {
            sectionMap.put(section.getUpStationId(), section.getDownStationId());
        }

        Long mostUpStation = 0L;
        for (Long stationId : sectionMap.keySet()) {
            if (sectionMap.containsKey(stationId) && !sectionMap.containsValue(stationId)) {
                mostUpStation = stationId;
            }
        }
        sortedStations.add(mostUpStation);

        for (int i = 0; i < sectionMap.size(); i++) {
            Long nextStation = sectionMap.get(mostUpStation);
            sortedStations.add(nextStation);
            mostUpStation = nextStation;
        }
        return sortedStations;
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}
