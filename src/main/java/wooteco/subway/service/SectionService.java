package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.utils.ExceptionMessage;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void saveInitialSection(LineRequest lineRequest, Line line) {
        sectionDao.saveInitialSection(lineRequest, line.getId());
    }

    @Transactional
    public Section addSection(SectionRequest sectionRequest, long lineId) {
        Section newSection = new Section(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        if (isStartOrEndOfSection(newSection, lineId)) {
            return sectionDao.save(newSection);
        }
        return insertToBetweenSections(newSection);
    }

    private boolean isStartOrEndOfSection(Section section, long id) {
        boolean isFirst = sectionDao.findByUpStationId(section.getDownStationId(), id).isPresent();
        boolean isLast = sectionDao.findByDownStationId(section.getUpStationId(), id).isPresent();
        if (isFirst || isLast) {
            if (isBetweenStation(section, id)) {
                return false;
            }
            return true;
        }
        return false;
    }

    private Section insertToBetweenSections(Section newSection) {
        Section findSection = sectionDao.findBySameUpOrDownStation(newSection)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_SECTION));
        validateDistance(findSection, newSection);
        updateExistSection(newSection, findSection);
        return sectionDao.save(newSection);
    }

    private void validateDistance(Section findSection, Section newSection) {
        if (findSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalArgumentException("추가하려는 구간의 길이는 기존 구간길이보다 길 수 없습니다.");
        }
    }

    private void updateExistSection(Section newSection, Section findSection) {
        sectionDao.updateDistance(findSection, newSection);
        if (newSection.hasSameUpstation(findSection)) {
            sectionDao.updateUpStation(findSection, newSection);
            return;
        }
        sectionDao.updateDownStation(findSection, newSection);
    }

    private boolean isBetweenStation(Section section, long id) {
        return sectionDao.findByUpStationId(section.getUpStationId(), id).isPresent() || sectionDao.findByDownStationId(section.getDownStationId(), id).isPresent();
    }

    @Transactional
    public void deleteSection(long stationId, long lineId) {
        Station station = stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_STATION));

        Sections sections = makeSections(lineId);
        validateSectionSize(sections);

        if (sections.isFirstStation(station.getId())) {
            deleteFirstStation(stationId, lineId);
            return;
        }

        if (sections.isLastStation(station.getId())) {
            deleteLastStation(stationId, lineId);
            return;
        }

        deleteBetweenStation(stationId, lineId);
    }

    private void validateSectionSize(Sections sections) {
        if (sections.isOnlyOneSection()) {
            throw new IllegalArgumentException("노선 내의 구간이 하나 이하라면 삭제할 수 없습니댜.");
        }
    }

    private void deleteFirstStation(long stationId, long lineId) {
        Section section = sectionDao.findByUpStationId(stationId, lineId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_SECTION));
        sectionDao.deleteById(section.getId(), section.getLineId());
    }

    private void deleteLastStation(long stationId, long lineId) {
        Section section = sectionDao.findByDownStationId(stationId, lineId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_SECTION));
        sectionDao.deleteById(section.getId(), section.getLineId());
    }

    private void deleteBetweenStation(long stationId, long lineId) {
        Section previousSection = sectionDao.findByDownStationId(stationId, lineId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_SECTION));
        Section nextSection = sectionDao.findByUpStationId(stationId, lineId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_SECTION));

        sectionDao.deleteById(previousSection.getId(), lineId);
        sectionDao.deleteById(nextSection.getId(), lineId);
        int distance = previousSection.getDistance() + nextSection.getDistance();
        Section mergeSection = new Section(lineId, previousSection.getUpStationId(), nextSection.getDownStationId(), distance);
        sectionDao.save(mergeSection);
    }

    public Sections makeSections(long lineId) {
        List<Section> getSections = sectionDao.findByLine(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선에 구간이 존재하지 않습니다."));
        return new Sections(getSections);
    }

    public List<Station> makeSectionsToStations(long lineId) {
        List<Station> sectionStations = new ArrayList<>();
        List<Long> sortedStations = makeSections(lineId).sortStations();

        for (Long stationId : sortedStations) {
            Station station = stationDao.findById(stationId)
                    .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_STATION));
            sectionStations.add(station);
        }

        return sectionStations;
    }
}
