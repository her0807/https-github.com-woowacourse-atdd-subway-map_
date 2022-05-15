package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public interface SectionDao {

    Section save(Section section);

    Sections findByLineId(Long lineId);

    int updateSection(Section updateSection);

    void deleteSections(List<Section> sections);
}
