package wooteco.subway.domain;

import java.util.Objects;

public class Section {
    private final Long id;
    private final Long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section merge(Long lineId, Section leftSection, Section rightSection) {
        return new Section(
                lineId,
                leftSection.getUpStation(),
                rightSection.getDownStation(),
                leftSection.plusDistance(rightSection.getDistance()));
    }

    public boolean isEqualsAndSmallerThan(int distance) {
        return this.distance <= distance;
    }

    public boolean isSameUpDownStation(Long upStationId, Long downStationId) {
        return this.upStation.getId().equals(upStationId) &&
                this.downStation.getId().equals(downStationId);
    }

    public boolean haveStationId(Long upStationId, Long downStationId) {
        return this.upStation.getId().equals(downStationId) || this.downStation.getId().equals(upStationId) ||
                this.upStation.getId().equals(upStationId) || this.downStation.getId().equals(downStationId);
    }

    public int minusDistance(int value) {
        return this.distance - value;
    }

    public int plusDistance(int value) {
        return this.distance + value;
    }

    public boolean isSameDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isSameUpStation(Station station) {
        return upStation.equals(station);
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(lineId, section.lineId) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStation, downStation, distance);
    }
}
