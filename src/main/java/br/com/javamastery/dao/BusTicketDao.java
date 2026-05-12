package br.com.javamastery.dao;

import br.com.javamastery.models.BusTicket;

import javax.persistence.EntityManager;
import java.util.List;

public class BusTicketDao {
    private EntityManager em;

    public BusTicketDao(EntityManager em) {
        this.em = em;
    }

    public void save(BusTicket busTicket){
        this.em.persist(busTicket);
    }

    public void update(BusTicket busTicket){
        this.em.merge(busTicket);
    }

    public void delete(BusTicket busTicket){
        busTicket = em.merge(busTicket);
        this.em.remove(busTicket);
    }

    public BusTicket searchById(Long id){
        return this.em.find(BusTicket.class, id);
    }
    public List<BusTicket> searchAll(){
        String jpql = "SELECT bt FROM BusTicket bt";

        return em.createQuery(jpql, BusTicket.class).getResultList();
    }
}
