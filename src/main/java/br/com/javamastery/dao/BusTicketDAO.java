package br.com.javamastery.dao;

import br.com.javamastery.models.BusTicket;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusTicketDAO {
    private EntityManager em;

    public BusTicketDAO(EntityManager em) {
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
        String jpql = "SELECT bt FROM BusTicket bt WHERE bt.canceled IS NOT TRUE";

        return em.createQuery(jpql, BusTicket.class).getResultList();
    }

    public BusTicket searchSingleTicket(BusTicket busTicketA){
        StringBuilder jpql = new StringBuilder("SELECT bt.id FROM BusTicket bt WHERE 1=1 ");

        Map<String, Object> params = new HashMap<>();

        if (busTicketA.getCode() != null && busTicketA.getCode().isBlank()) {
            jpql.append("AND bt.code LIKE :code ");
            params.put("code", busTicketA.getCode());
        }

        TypedQuery<BusTicket> query = this.em.createQuery(jpql.toString(), BusTicket.class);

        if (params.isEmpty())
            throw new IllegalArgumentException("At least one filter must be informed.");

        params.forEach(query::setParameter);

        List<BusTicket> results = query.getResultList();

        return !results.isEmpty() ? results.getFirst() : null;
    }

    public List<BusTicket> searchTickets(BusTicket busTicketA){
        StringBuilder jpql = new StringBuilder("SELECT bt FROM BusTicket bt WHERE 1=1 ");

        Map<String, Object> params = new HashMap<>();

        if (busTicketA.getTraveler().getId() != null){
            jpql.append("AND bt.buyerid LIKE :buyerId ");
            params.put("buyerId", busTicketA.getTraveler().getId());
        }

        if (!busTicketA.isCanceled()){
            jpql.append("AND bt.canceled = 0 ");
            params.put("canceled", busTicketA.isCanceled());
        }

        TypedQuery<BusTicket> query = this.em.createQuery(jpql.toString(), BusTicket.class);

        if (params.isEmpty())
            throw new IllegalArgumentException("At least one filter must be informed.");

        params.forEach(query::setParameter);

        return query.getResultList();
    }
}
