package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDaoImpl implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDaoImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Section> actorRowMapper = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("lineId"),
            resultSet.getLong("upStationId"),
            resultSet.getLong("downStationId"),
            resultSet.getInt("distance")
    );

    @Override
    public void save(final Section section) {
        String sql = "insert into section (lineId, upStationId, downStationId, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                section.getLindId(), section.getUpStationId(),
                section.getDownStationId(), section.getDistance()
        );
    }

    @Override
    public List<Section> findByLineId(final Long lindId) {
        String sql = "select * from section where lineId = ?";
        return jdbcTemplate.query(sql, actorRowMapper, lindId);
    }

    @Override
    public void update(final Section section) {
        String sql = "update section set (upStationId, downStationId, distance) = (?, ?, ?) where id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(),
                section.getDistance(), section.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean exists(Long lineId, Long stationId) {
        String sql = "select exists (select lineId, upStationId, downStationId from section "
                + "where lineId = ? and (upStationId = ? or downStationId = ?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId, stationId);
    }
}
