package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {
    private StationRequest gangnamStationRequest = new StationRequest("강남역");
    private StationRequest seolleungStationRequest = new StationRequest("선릉역");


    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given

        // when
        ExtractableResponse<Response> response = requestStationCreation(gangnamStationRequest);

        // then
        StationResponse actualResponse = response.jsonPath().getObject(".", StationResponse.class);
        StationResponse expectedResponse = StationResponse.of(
                gangnamStationRequest.toStation(extractIdFromHeader(response)));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하는 경우 상태코드 400 오류가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        requestStationCreation(gangnamStationRequest);

        // when
        ExtractableResponse<Response> response = requestStationCreation(gangnamStationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> requestStationCreation(StationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철역을 모두 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = requestStationCreation(gangnamStationRequest);
        ExtractableResponse<Response> createResponse2 = requestStationCreation(seolleungStationRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        // then
        List<StationResponse> expectedStations = Stream.of(createResponse1, createResponse2)
                .map(it -> it.jsonPath().getObject(".", StationResponse.class))
                .collect(Collectors.toList());
        List<StationResponse> actualStations = response.jsonPath().getList(".", StationResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualStations).isEqualTo(expectedStations);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = requestStationCreation(gangnamStationRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(extractLocationFromHeader(createResponse))
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철역을 제거하는 경우 상태코드 404 오류가 발생한다.")
    @Test
    void deleteStationNotExists() {
        // given

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/stations/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
