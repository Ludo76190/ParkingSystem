package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    /**
     * calculate parking fare for one given ticket.
     *
     * @param ticket one initialized ticket
     */

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime());
        }

        // getTime renvoie des millisecondes
        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        // diviser duration par 60.0*60*1000 pour convertir en heure
        // il faut coder 60.0 dans la division pour avoir le type double
        double duration = (outHour - inHour)/(60.0*60*1000);

        // Free 30 Minutes
        if (duration <= 0.5) {
            duration=0;
        }

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                // calcul du prix arrondi à 2 chiffres
                ticket.setPrice((double) ((int)((duration * Fare.CAR_RATE_PER_HOUR)*100))/100);;
                break;
            }
            case BIKE: {
                // calcul du prix arrondi à 2 chiffres
                ticket.setPrice((double) ((int)((duration * Fare.BIKE_RATE_PER_HOUR)*100))/100);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}