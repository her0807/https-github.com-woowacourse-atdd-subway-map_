package wooteco.subway.service.dto;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    public LineResponse(OnlyLineResponse lineResponse, List<StationResponse> stations) {
        this.id = lineResponse.getId();
        this.name = lineResponse.getName();
        this.color = lineResponse.getColor();
        this.stations = stations;
    }

    public LineResponse(Line line, List<Station> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
