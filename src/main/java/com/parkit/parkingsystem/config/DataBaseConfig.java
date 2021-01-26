package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;

/**
 * Database configuration.
 */

public class DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseConfig");


    /**
     * Retrieve DB credentials
     * @return credentials
     */
    private final GetCredentials getCredentials = new GetCredentials();

    String[] credentials;
    {
        try {
            credentials = getCredentials.getPropValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * open a connection to the DB, credentials stored in db.properties.
     *
     * @return a connection
     * @throws ClassNotFoundException if com.mysql.cj.jdbc.Driver is not found
     * @throws SQLException if exception while executing SQL instructions
     * @throws IOException if exception while reading the db.properties file
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/prod?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC", credentials[0], credentials[1]);
    }

    /**
     * close a given connection to the DB.
     *
     * @param con the connection to close
     */
    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    /**
     * close a given prepared statement.
     *
     * @param ps prepared statement to close
     */
    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    /**
     * close a given result set.
     *
     * @param rs result set to close
     */
    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
