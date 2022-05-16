package wooteco.subway.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MissingServletRequestParameterException;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(SectionController.class)
class SectionControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SectionService sectionService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("구간을 추가한다.")
    void addSection() throws Exception {
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        given(sectionService.addSection(sectionRequest, 1L)).willReturn(new Section(1L, 2L, 3L, 10));

        MockHttpServletResponse result = mockMvc.perform(
                post("/lines/1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @ParameterizedTest
    @DisplayName("구간을 추가할때 요소가 비어있으면 예외를 반환한다.")
    @CsvSource(value = {"2:3:", "2::10", ":3:10"}, delimiter = ':')
    void addSection_Exception(Long upStationId, Long downStationId, Integer distance) throws Exception {
        String sectionRequest = "{\n" +
                "  \"upStationId\" :" + upStationId + ",\n" +
                "  \"downStationId\" :" + downStationId + ",\n" +
                "  \"distance\" :" + distance +
                "}";

        mockMvc.perform(
                post("/lines/1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sectionRequest))
                .andExpect(this::checkValidException)
                .andReturn();
    }


    @Test
    @DisplayName("구간을 삭제한다")
    void deleteSection() throws Exception {
        MockHttpServletResponse result = mockMvc.perform(
                delete("/lines/1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("stationId", "2"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파라미터가 비어있으면 예외를 발생시킨다.")
    void deleteSection_Exception() throws Exception {
        ResultActions result = mockMvc.perform(
                delete("/lines/1/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(exception -> assertThat(exception.getResolvedException().getClass()).isAssignableFrom(MissingServletRequestParameterException.class));
    }
}
