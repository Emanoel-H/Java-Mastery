package br.com.javamastery.dao;

import br.com.javamastery.models.BusTicket;
import br.com.javamastery.models.Trip;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripDAO {
    private EntityManager em;

    public TripDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Trip trip){
        this.em.persist(trip);
    }

    public List<Trip> searchTrips(Trip tripA){
        StringBuilder jpql = new StringBuilder("SELECT t FROM Trip t WHERE 1=1 ");

        Map<String, Object> params = new HashMap<>();

        if (tripA.getBusCompany().getEmail() != null && !tripA.getBusCompany().getEmail().getEmail().isBlank()) {
            jpql.append("AND t.busCompany.email.email LIKE :companyEmail ");
            params.put("companyEmail", tripA.getBusCompany().getEmail().getEmail());
        }

        if (tripA.getOriginCity() != null && tripA.getOriginCity().getIBGE_code() != null) {
            jpql.append("AND t.originCity.IBGE_code = :IBGECode ");
            params.put("IBGECode", tripA.getOriginCity().getIBGE_code());
        }

        if (tripA.getDestinationCity() != null && tripA.getDestinationCity().getIBGE_code() != null) {
            jpql.append("AND t.destinationCity.IBGE_code = :IBGECodeDestination ");
            params.put("IBGECodeDestination", tripA.getDestinationCity().getIBGE_code());
        }

        TypedQuery<Trip> query = this.em.createQuery(jpql.toString(), Trip.class);

        params.forEach(query::setParameter);

        return query.getResultList();
    }

    public Trip searchSingleTrip(Trip tripA){
        StringBuilder jpql = new StringBuilder("SELECT t FROM Trip t WHERE 1=1 ");

        Map<String, Object> params = new HashMap<>();

        if (tripA.getBusCompany().getEmail() != null && !tripA.getBusCompany().getEmail().getEmail().isBlank()) {
            jpql.append("AND t.busCompany.email.email LIKE :companyEmail ");
            params.put("companyEmail", tripA.getBusCompany().getEmail().getEmail());
        }

        if (tripA.getOriginCity() != null && tripA.getOriginCity().getIBGE_code() != null) {
            jpql.append("AND t.originCity.IBGE_code = :IBGECode ");
            params.put("IBGECode", tripA.getOriginCity().getIBGE_code());
        }

        if (tripA.getDestinationCity() != null && tripA.getDestinationCity().getIBGE_code() != null) {
            jpql.append("AND t.destinationCity.IBGE_code = :IBGECodeDestination ");
            params.put("IBGECodeDestination", tripA.getDestinationCity().getIBGE_code());
        }

        if (tripA.getCode() != null && !tripA.getCode().isBlank()) {
            jpql.append("AND t.code = :code ");
            params.put("code", tripA.getCode());
        }
        
        TypedQuery<Trip> query = this.em.createQuery(jpql.toString(), Trip.class);

        params.forEach(query::setParameter);

        List<Trip> trip = query.getResultList();

        return trip.isEmpty() ? null : query.getSingleResult();
    }

    public void updateTrip(Trip tripA){
        this.em.merge(tripA);
    }

    public void delete(Trip tripA){
        if (isTripActive(tripA))
            throw new RuntimeException("You can't delete a trip that is already related to a sale");

        tripA = em.merge(tripA);
        this.em.remove(tripA);
    }

    public boolean isTripActive(Trip tripA){
        StringBuilder jpql = new StringBuilder("SELECT bt.id FROM BusTicket bt WHERE 1=1 ");

        Map<String, Object> params = new HashMap<>();

        if (tripA.getCode() != null && !tripA.getCode().isBlank()) {
            jpql.append("AND bt.trip.code = :code ");
            params.put("code", tripA.getCode());
        }

        jpql.append("AND bt.canceled = 0 ");

        TypedQuery<BusTicket> query = this.em.createQuery(jpql.toString(), BusTicket.class);

        params.forEach(query::setParameter);

        List<BusTicket> tickets = query.getResultList();

        return !tickets.isEmpty();
    }
}
