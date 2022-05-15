package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.service.LineService.DUPLICATE_EXCEPTION_MESSAGE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.mock.MemoryLineDao;
import wooteco.subway.mock.MemorySectionDao;
import wooteco.subway.mock.MemoryStationDao;

class LineServiceTest {
    private final MemoryLineDao lineDao = new MemoryLineDao();
    private final MemoryStationDao stationDao = new MemoryStationDao();
    private final MemorySectionDao sectionDao = new MemorySectionDao();
    private final LineService lineService = new LineService(lineDao, stationDao, sectionDao);

    @BeforeEach
    void beforeEach() {
        lineDao.clear();
        stationDao.clear();
        sectionDao.clear();

        stationDao.save(new Station(1L, "첫번째역"));
        stationDao.save(new Station(2L, "두번째역"));
    }

    @Test
    void saveDuplicateName() {
        String name = "신분당선";
        String color = "bg-red-600";
        lineService.create(new Line(name, color), new Section(1L, 2L, 10));

        assertThatThrownBy(() -> lineService.create(new Line(name, "blue"), new Section(1L, 2L, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DUPLICATE_EXCEPTION_MESSAGE);
    }

    @Test
    void saveDuplicateColor() {
        String name = "신분당선";
        String color = "bg-red-600";
        lineService.create(new Line(name, color), new Section(1L, 2L, 10));

        assertThatThrownBy(() -> lineService.create(new Line("2호선", color), new Section(1L, 2L, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DUPLICATE_EXCEPTION_MESSAGE);
    }

    @Test
    void updateDuplicate() {
        String name = "신분당선";
        String color = "bg-red-600";
        lineService.create(new Line(name, color), new Section(1L, 2L, 10));

        Line line = lineService.create(new Line("2호선", "bg-green-600"), new Section(1L, 2L, 10));

        Line duplicateUpdate = new Line(line.getId(), name, color);
        assertThatThrownBy(() -> lineService.update(duplicateUpdate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DUPLICATE_EXCEPTION_MESSAGE);
    }
}
