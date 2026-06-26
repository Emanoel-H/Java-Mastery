package br.com.javamastery.service;

import br.com.javamastery.client.OsrmClient;
import br.com.javamastery.dao.TripDAO;
import br.com.javamastery.exception.InvalidPriceException;
import br.com.javamastery.models.BusCompany;
import br.com.javamastery.models.City;
import br.com.javamastery.models.Trip;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class TripService {
    public static final double PRICE_PER_KM = 0.35;
    private TripDAO tripDAO;
    private OsrmClient osrmClient;
    private EntityManager em;

    public TripService(EntityManager em) {
        this.em = em;
        tripDAO = new TripDAO(em);
        osrmClient = new OsrmClient();
    }

    public double suggestPrice(City origin, City  destination) {
        double distanceKM = osrmClient.getRealDistanceKM(origin, destination);
        return distanceKM * PRICE_PER_KM;
    }

    public Trip createTrip(City origin, City destination, BigDecimal price, LocalTime departureTime, BusCompany busCompany) {
        Trip trip = new Trip();
        trip.setOriginCity(origin);
        trip.setDestinationCity(destination);

        if (price.compareTo(BigDecimal.ZERO) > 0)
            trip.setPrice(price);
        else
            throw new InvalidPriceException(price);

        trip.setDepartureTime(departureTime);
        trip.setBusCompany(busCompany);
        trip.calculateRealDistance(osrmClient);

        try {
            this.em.getTransaction().begin();
            this.tripDAO.save(trip);
            this.em.getTransaction().commit();
        } catch (Exception e) {
            this.em.getTransaction().rollback();
            throw e;
        }
            return trip;
    }

    public List<Trip> searchTrips(Trip trip) {
        return tripDAO.searchTrips(trip);
    }

    public Trip searchSingleTrip(Trip trip) {
        return tripDAO.searchSingleTrip(trip);
    }

    public void updateOriginCity(Trip trip, City origin) {
        trip.setOriginCity(origin);

        trip.setPrice(BigDecimal.valueOf(suggestPrice(trip.getOriginCity(), trip.getDestinationCity())));

        try{
            this.em.getTransaction().begin();
            this.tripDAO.updateTrip(trip);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateDestinationCity(Trip trip, City destination) {
        trip.setOriginCity(destination);

        trip.setPrice(BigDecimal.valueOf(suggestPrice(trip.getOriginCity(), trip.getDestinationCity())));

        try{
            this.em.getTransaction().begin();
            this.tripDAO.updateTrip(trip);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
}
