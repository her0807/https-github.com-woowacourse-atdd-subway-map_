package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Sections {

    private static final int SECTIONS_MINIMUM_SIZE = 1;

    private static final String DUPLICATE_BOTH_STATION_EXCEPTION = "[ERROR] 상,하행 역이 구간에 모두 포함된 경우 추가할 수 없습니다.";
    private static final String NO_EXIST_BOTH_STATION_EXCEPTION = "[ERROR] 상,하행 역이 모두 구간에 존재하지 않는다면 추가할 수 없습니다.";
    private static final String WAY_POINT_STATION_DISTANCE_EXCEPTION = "[ERROR] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.";
    private static final String MINIMUM_STATIONS_SIZE_EXCEPTION = "[ERROR] 최소 하나 이상의 구간이 존재하여야합니다.";
    private static final String NOT_FOUND_STATION_ID_EXCEPTION = "[ERROR] 구간으로 등록되지 않은 지하철역 정보입니다.";

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void add(Section section) {
        boolean existUpStation = hasSameUpByUp(section) || hasSameDownByUp(section);
        boolean existDownStation = hasSameDownByDown(section) || hasSameUpByDown(section);
        validateAddSectionCondition(existUpStation, existDownStation);
        Optional<Section> sameUpStationSection = sections.stream().filter(it -> it.isSameUpStation(section)).findAny();
        Optional<Section> sameDownStationSection = sections.stream().filter(it -> it.isSameDownStation(section))
            .findAny();
        if (hasSameUpByUp(section) && sameUpStationSection.isPresent()) {
            splitByUpStation(section, sameUpStationSection.get());
        }
        if (hasSameDownByDown(section) && sameDownStationSection.isPresent()) {
            splitByDownStation(section, sameDownStationSection.get());
        }
        sections.add(section);
    }

    public void remove(long stationId) {
        Optional<Section> upSection = sections.stream().filter(it -> it.isSameUpStationId(stationId)).findFirst();
        Optional<Section> downSection = sections.stream().filter(it -> it.isSameDownStationId(stationId)).findFirst();
        validateExistStationId(upSection, downSection);
        validateMinimumListSize();
        if (upSection.isPresent() && downSection.isPresent()) {
            mergeUpAndDownSection(upSection.get(), downSection.get());
            return;
        }
        if (upSection.isPresent()) {
            sections.remove(upSection.get());
            return;
        }
        downSection.ifPresent(sections::remove);
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }

    public List<Long> getStationIds() {
        Set<Long> distinctStationIds = new HashSet<>();
        for (Section section : sections) {
            distinctStationIds.add(section.getUpStationId());
            distinctStationIds.add(section.getDownStationId());
        }
        return new ArrayList<>(distinctStationIds);
    }

    private void validateAddSectionCondition(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException(DUPLICATE_BOTH_STATION_EXCEPTION);
        }
        if (!existUpStation && !existDownStation) {
            throw new IllegalArgumentException(NO_EXIST_BOTH_STATION_EXCEPTION);
        }
    }

    private void splitByUpStation(Section section, Section findSection) {
        validateDistance(section, findSection);
        int distance = findSection.getDistance() - section.getDistance();
        sections.add(new Section(findSection.getId(), section.getDownStationId(), findSection.getDownStationId(), distance));
        sections.remove(findSection);
    }

    private void splitByDownStation(Section section, Section findSection) {
        validateDistance(section, findSection);
        int distance = findSection.getDistance() - section.getDistance();
        sections.add(new Section(findSection.getId(), findSection.getUpStationId(), section.getUpStationId(), distance));
        sections.remove(findSection);
    }

    private boolean hasSameUpByDown(Section section) {
        return sections.stream().anyMatch(it -> it.isSameUpByDown(section));
    }

    private boolean hasSameDownByDown(Section section) {
        return sections.stream().anyMatch(it -> it.isSameDownStation(section));
    }

    private boolean hasSameDownByUp(Section section) {
        return sections.stream().anyMatch(it -> it.isSameDownByUp(section));
    }

    private boolean hasSameUpByUp(Section section) {
        return sections.stream().anyMatch(it -> it.isSameUpStation(section));
    }

    private void mergeUpAndDownSection(Section upSection, Section downSection) {
        Section newSection = new Section(downSection.getUpStationId(), upSection.getDownStationId(),
            upSection.getDistance() + downSection.getDistance());
        sections.remove(upSection);
        sections.remove(downSection);
        sections.add(newSection);
    }

    private void validateDistance(Section section, Section findSection) {
        if (!findSection.isLongerThan(section)) {
            throw new IllegalArgumentException(WAY_POINT_STATION_DISTANCE_EXCEPTION);
        }
    }

    private void validateMinimumListSize() {
        if (sections.size() <= SECTIONS_MINIMUM_SIZE) {
            throw new IllegalArgumentException(MINIMUM_STATIONS_SIZE_EXCEPTION);
        }
    }

    private void validateExistStationId(Optional<Section> findSectionByUpStationId,
        Optional<Section> findSectionByDownStationId) {
        if (findSectionByUpStationId.isEmpty() && findSectionByDownStationId.isEmpty()) {
            throw new IllegalArgumentException(NOT_FOUND_STATION_ID_EXCEPTION);
        }
    }
}
