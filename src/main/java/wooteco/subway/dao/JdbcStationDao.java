package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao {

    public static final int STATION_EXIST_VALUE = 1;
    private static final int DELETE_SUCCESS = 1;
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> rowMapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(String name) {
        String sql = "insert into station (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Station> findAll() {
        String sql = "select * from station";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean deleteById(Long id) {
        String sql = "delete from station where id = ?";
        return jdbcTemplate.update(sql, id) == DELETE_SUCCESS;
    }

    public boolean isExistStation(String name) {
        String sql = "select EXISTS (select name from station where name = ?) as success";
        return jdbcTemplate.queryForObject(sql, Integer.class, name) == STATION_EXIST_VALUE;
    }

    public Station findById(Long id) {
        String sql = "select * from station where id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }
}
