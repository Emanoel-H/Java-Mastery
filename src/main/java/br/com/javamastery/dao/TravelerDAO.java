package br.com.javamastery.dao;

import br.com.javamastery.models.Traveler;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelerDAO {
    private EntityManager em;

    public TravelerDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Traveler traveler){
        this.em.persist(traveler);
    }

    public void update(Traveler traveler){
        this.em.merge(traveler);
    }

    public void delete(Traveler traveler){
        traveler = this.em.merge(traveler);
        this.em.remove(traveler);
    }

    public Traveler searchPerson(Traveler traveler){
        StringBuilder jpql = new StringBuilder("SELECT p FROM Person p WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (traveler.getEmail() != null && traveler.getEmail().getEmail() != null && !traveler.getEmail().getEmail().isBlank()) {
            jpql.append("AND p.email.email = :email ");
            params.put("email", traveler.getEmail().getEmail());
        }

        if (traveler.getCpf() != null && !traveler.getCpf().isBlank()) {
            jpql.append("AND p.cpf = :cpf ");
            params.put("cpf", traveler.getCpf());
        }

        TypedQuery<Traveler> query = em.createQuery(jpql.toString(), Traveler.class);

        params.forEach(query::setParameter);

        if (params.isEmpty())
            throw new IllegalArgumentException("At least one filter must be informed.");

        List<Traveler> results = query.getResultList();

        return results.isEmpty() ? null : results.getFirst();
    }
}
