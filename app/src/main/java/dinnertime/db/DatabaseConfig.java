package dinnertime.db;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author aaron.mitchell
 */
@Configuration
public class DatabaseConfig {
    @Bean
    @Primary
    public DataSource createDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate createJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DinnerTimeDbAccessor getDinnerTimeDbAccessor(){
        DinnerTimeDbAccessor dbAccessor = new DinnerTimeDbAccessor();
        dbAccessor.setJdbcTemplate(createJdbcTemplate(createDataSource()));
        return dbAccessor;
    }
}
