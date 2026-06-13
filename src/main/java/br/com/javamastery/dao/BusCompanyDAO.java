package br.com.javamastery.dao;

import br.com.javamastery.models.BusCompany;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusCompanyDAO {
    private EntityManager em;

    public BusCompanyDAO(EntityManager em) {
        this.em = em;
    }

    public void save(BusCompany busCompany){
        this.em.persist(busCompany);
    }

    public BusCompany searchCompany(BusCompany company){
        StringBuilder jpql = new StringBuilder("SELECT bc FROM BusCompany bc WHERE 1=1 ");

        Map<String, Object> params = new HashMap<>();

        if (company.getEmail() != null && company.getEmail().getEmail() != null && !company.getEmail().getEmail().isBlank()) {
            jpql.append("AND bc.email.email = :email ");
            params.put("email", company.getEmail().getEmail());
        }

        TypedQuery<BusCompany> query = em.createQuery(jpql.toString(), BusCompany.class);

        params.forEach(query::setParameter);

        if (params.isEmpty())
            throw new IllegalArgumentException("At least one filter must be informed.");

        List<BusCompany> results = query.getResultList();

        return results.isEmpty() ? null : results.getFirst();
    }

    public void updateCompany(BusCompany busCompany){
        this.em.merge(busCompany);
    }
}
