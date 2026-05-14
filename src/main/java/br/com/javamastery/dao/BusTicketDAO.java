package br.com.javamastery.dao;

import br.com.javamastery.models.BusTicket;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

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
        String jpql = "SELECT bt FROM BusTicket bt";

        return em.createQuery(jpql, BusTicket.class).getResultList();
    }

    public BusTicket searchSingleTicket(BusTicket busTicketA){
        String jpql = "SELECT bt.id FROM BusTicket bt WHERE 1=1 ";

        if (busTicketA.getCode() != null)
            jpql += "AND bt.code LIKE :code ";

        TypedQuery<Long> query = this.em.createQuery(jpql, Long.class);

        if (busTicketA.getTraveler().getCpf() != null)
            query.setParameter("code", busTicketA.getCode());

        Long ticketId = query.setFirstResult(0).setMaxResults(1).getSingleResult();

        return this.em.find(BusTicket.class, ticketId);
    }

    public List<BusTicket> searchTickets(BusTicket busTicketA){
        String jpql = "SELECT bt FROM BusTicket bt WHERE 1=1 ";

        if (busTicketA.getTraveler().getCpf() != null)
            jpql += "AND bt.traveler.cpf LIKE :cpf ";

        TypedQuery<BusTicket> query = this.em.createQuery(jpql, BusTicket.class);

        if (busTicketA.getTraveler().getCpf() != null)
            query.setParameter("cpf", busTicketA.getTraveler().getCpf());

        return query.getResultList();
    }
}
