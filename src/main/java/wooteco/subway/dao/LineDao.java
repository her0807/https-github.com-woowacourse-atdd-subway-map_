package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
            );

    public Line insert(Line line) {
        long id = simpleJdbcInsert.executeAndReturnKey(Map.of("name", line.getName(), "color", line.getColor()))
                .longValue();

        return new Line(id, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Optional<Line> findById(Long id) {
        final String sql = "SELECT * FROM LINE WHERE id = ?";
        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, lineRowMapper, id));
        } catch(IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public int delete(Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int update(Line line) {
        final String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        return jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    public boolean isExistName(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE name = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, name));
    }

    public boolean isExistName(Long id, String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE id != ? AND name = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id, name));
    }

}
