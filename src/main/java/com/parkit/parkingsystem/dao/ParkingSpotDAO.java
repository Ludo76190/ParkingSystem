package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParkingSpotDAO {
    private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     * get the next available spot for a given parking type.
     *
     * @param parkingType parking type
     * @return the id of the next available parking spot
     */

    public int getNextAvailableSlot(ParkingType parkingType){
        Connection con = null;
        int result=-1;
        try {
            con = dataBaseConfig.getConnection();
            ResultSet rs = null;
            PreparedStatement ps = null;
            try {
                ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
                ps.setString(1, parkingType.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            } finally {
                dataBaseConfig.closeResultSet(rs);
                dataBaseConfig.closePreparedStatement(ps);
            }
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }

    /**
     * update in the DB a given parking spot.
     *
     * @param parkingSpot parking spot we want to updated
     * @return true if the parking spot has been updated in the DB
     */

    public boolean updateParking(ParkingSpot parkingSpot){
        //update the availability fo that parking slot
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = null;
            int updateRowCount;
            try {
                ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
                ps.setBoolean(1, parkingSpot.isAvailable());
                ps.setInt(2, parkingSpot.getId());
                updateRowCount = ps.executeUpdate();
            } finally {
                dataBaseConfig.closePreparedStatement(ps);
            }
            return (updateRowCount == 1);
        } catch (Exception ex){
            logger.error("Error updating parking info",ex);
            return false;
        } finally {
            dataBaseConfig.closeConnection(con);
        }
    }

}
