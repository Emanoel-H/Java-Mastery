package br.com.javamastery.service;

import br.com.javamastery.client.OsrmClient;
import br.com.javamastery.dao.TripDAO;
import br.com.javamastery.models.City;
import br.com.javamastery.models.Trip;
import jakarta.persistence.EntityManager;

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
        Trip trip = new Trip();
        trip.setOriginCity(origin);
        trip.setDestinationCity(destination);

        trip.calculateRealDistance(osrmClient);

        return trip.getDistanceKM() * PRICE_PER_KM;
    }
}
