package wooteco.subway.domain;

public class Section {
    private Long id;
    private Station upStation;
    private Station downStation;
    private Long distance;

    public Section(Long id, Station upStation, Station downStation, Long distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public Long getDistance() {
        return distance;
    }
}
