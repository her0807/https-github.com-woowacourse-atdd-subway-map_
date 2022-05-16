package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NameDuplicationException;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line create(final String name, final String color) {
        checkDuplication(name);
        final Line line = new Line(name, color);
        return lineDao.save(line);
    }

    private void checkDuplication(final String name) {
        if (lineDao.counts(name) > 0) {
            throw new NameDuplicationException();
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(final Long id) {
        return lineDao.findById(id);
    }

    public void edit(final Long id, final String name, final String color) {
        lineDao.edit(id, name, color);
    }

    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
