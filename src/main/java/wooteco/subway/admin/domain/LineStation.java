package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.Objects;

public class LineStation {
    @Column("pre_station")
    private Long preStationId;
    @Column("station")
    private Long stationId;
    private int distance;
    private int duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LineStation() {
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStation that = (LineStation) o;
        return distance == that.distance &&
                duration == that.duration &&
                preStationId.equals(that.preStationId) &&
                stationId.equals(that.stationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(preStationId, stationId, distance, duration);
    }
}