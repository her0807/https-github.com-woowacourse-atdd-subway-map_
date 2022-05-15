package wooteco.subway.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.Entity.SectionEntity;
import wooteco.subway.domain.Section;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;

    public SectionRepository(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long save(Section section) {
        return sectionDao.save(SectionEntity.from(section));
    }

    public List<SectionEntity> findByLineId(Long lineId) {
        return sectionDao.findByLineId(lineId);

    }

    public void update(Section section) {
        sectionDao.update(SectionEntity.from(section));
    }

    public void deleteById(Long id) {
        sectionDao.deleteById(id);
    }
}
