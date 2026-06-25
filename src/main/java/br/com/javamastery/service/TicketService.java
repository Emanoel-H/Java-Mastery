package br.com.javamastery.service;

import br.com.javamastery.dao.BusTicketDAO;
import br.com.javamastery.exception.CancellationDeadlineExceededException;
import br.com.javamastery.exception.TicketNotFoundException;
import br.com.javamastery.models.BusTicket;
import br.com.javamastery.models.Traveler;
import br.com.javamastery.models.Trip;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TicketService {
    EntityManager em;
    BusTicketDAO  busTicketDAO;

    public TicketService(EntityManager em) {
        this.em = em;
        this.busTicketDAO = new BusTicketDAO(em);
    }

    public BusTicket buyTicket(Trip trip, LocalDate departureDate, Traveler traveler){
        BusTicket busTicket = new BusTicket();
        busTicket.setTrip(trip);

        if (!departureDate.isBefore(LocalDate.now()))
            busTicket.setDepartureDate(departureDate);
        else
            throw new IllegalArgumentException("Departure date cannot be before current date");

        busTicket.setTraveler(traveler);

        try {
            this.em.getTransaction().begin();
            busTicketDAO.save(busTicket);
            this.em.getTransaction().commit();
        } catch (Exception e) {
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }

        return busTicket;
    }

    public void cancelTicket(String ticketCode){
        LocalDateTime now = LocalDateTime.now();
        BusTicket busTicket = new BusTicket();
        busTicket.setCode(ticketCode);
        busTicket =  busTicketDAO.searchSingleTicket(busTicket);

        if (busTicket == null)
            throw new TicketNotFoundException(ticketCode);

        LocalDateTime tripDateTime = LocalDateTime.of(busTicket.getDepartureDate(),
                busTicket.getTrip().getDepartureTime());

        if (!now.isBefore(tripDateTime.minusHours(1)))
            throw new CancellationDeadlineExceededException();

        busTicket.setCancelDate(now);
        busTicket.setCanceled(true);

        busTicket.getTraveler().setCreditsBalance(busTicket.getTicketPrice());

        try{
            this.em.getTransaction().begin();
            this.busTicketDAO.update(busTicket);
            this.em.getTransaction().commit();
        } catch (Exception e) {
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateTravelerName(BusTicket busTicket, String travelerName){
        busTicket.getTraveler().setName(travelerName);

        try{
            this.em.getTransaction().begin();
            this.busTicketDAO.update(busTicket);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateTravelerCPF(BusTicket busTicket, String travelerCPF){
        busTicket.getTraveler().setCpf(travelerCPF);

        try{
            this.em.getTransaction().begin();
            this.busTicketDAO.update(busTicket);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
}
