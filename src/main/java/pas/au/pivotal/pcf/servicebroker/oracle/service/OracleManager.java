package pas.au.pivotal.pcf.servicebroker.oracle.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class OracleManager
{
    private Logger logger = LoggerFactory.getLogger(OracleManager.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${oracle.tablespaceName:EXAMPLE}")
    private String tablespaceName;

    @Value("${oracle.tempTablespaceName:TEMP}")
    private String tempTablespaceName;

    @Value("${spring.datasource.url:JDBC_URL}")
    private String jdbcUrl;

    public void createUserForService (String username, String password) throws SQLException
    {
        executeDDL(String.format(Constants.NEW_USER, username, password, tablespaceName, tablespaceName, tempTablespaceName));
        logger.info("Oracle User Created ....");

        executeDDL(String.format(Constants.GRANT_CREATE_SESSION, username));
        logger.info("Grant CREATE SESSION Given ....");

    }

    public void deleteUser (String username) throws SQLException
    {
        executeDDL(String.format(Constants.KILL_SESSIONS, username));
        logger.info("Ended all current USERS sessions ....");

        executeDDL(String.format(Constants.DROP_USER, username.toUpperCase()));
        logger.info("Oracle User Deleted ....");
    }

    public void executeDDL(String ddl) throws SQLException
    {
        try
        {
            jdbcTemplate.execute(ddl);
        }
        catch (Exception e)
        {
            logger.info("Error while executing SQL DDL statement '" + ddl + "'", e);
        }

    }

    public List<Map<String, Object>> executeQuery (String sql, Object[] args)
    {
        List<Map<String, Object>> resultList = null;

        if (args == null) {
            resultList = jdbcTemplate.queryForList(sql);
        }
        else {
            resultList = jdbcTemplate.queryForList(sql, args);
        }

        return resultList;
    }

    public String getJdbcUrl ()
    {
        return jdbcUrl;
    }
}
