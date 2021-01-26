package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    @Spy
    private static ParkingSpotDAO parkingSpotDAO;
    @Spy
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        int slot1=parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actually saved in DB
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket);
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

        //TODO: Check that Parking table is updated with availability
        int slot2=parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotEquals(slot1, slot2);
    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        //TODO: check that the fare is generated correctly in the database
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNull(ticket.getOutTime());
        assertEquals(ticket.getPrice(),0.0);

        //TODO: check that out time is populated correctly in the database
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        try {
            TimeUnit.SECONDS.sleep(1);
            parkingService.processExitingVehicle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ticket =ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket.getOutTime());
    }

    @Test
    public void testRecurrentUser(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        try {
            TimeUnit.SECONDS.sleep(1);
            parkingService.processExitingVehicle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ticketDAO.recurrentUser("ABCDEF");;
    }

}
