package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;
    private final Long lineId;

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(null, upStation, downStation, distance, null);
    }

    public Section(final Station upStation, final Station downStation, final int distance, final Long lineId) {
        this(null, upStation, downStation, distance, lineId);
    }

    public Section(final Long id, final Station upStation, final Station downStation, final int distance, final Long lineId) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.lineId = lineId;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isGreaterOrEqualTo(final Section other) {
        return this.distance >= other.distance;
    }

    public Section merge(final Section section) {
        return new Section(this.upStation, section.downStation, sumDistance(section), this.lineId);
    }

    private int sumDistance(final Section section) {
        return this.distance + section.distance;
    }

    public Section createSectionInBetween(final Section section) {
        if (this.upStation.equals(section.upStation)) {
            return new Section(this.id, section.downStation, this.downStation, subtractDistance(section), this.lineId);
        }
        return new Section(this.id, this.upStation, section.upStation, subtractDistance(section), this.lineId);
    }

    private int subtractDistance(final Section section) {
        return this.distance - section.distance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        final Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(lineId, section.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, lineId);
    }
}
