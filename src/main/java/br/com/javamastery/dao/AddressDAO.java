package br.com.javamastery.dao;

import br.com.javamastery.models.City;
import br.com.javamastery.models.State;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressDAO {
    private EntityManager em;

    public AddressDAO(EntityManager em) {
        this.em = em;
    }

    public List<State> searchAllState(){
        String jpql = "SELECT s FROM State s";

        return this.em.createQuery(jpql, State.class).getResultList();
    }

    public List<City> searchCitiesByState(String uf){
        String jpql = "SELECT c FROM City c WHERE 1=1 ";

        if (uf.length() > 2)
            jpql = jpql + "AND c.state.name = :uf ";
        else
            jpql = jpql + "AND c.state.uf = :uf ";

        return this.em.createQuery(jpql, City.class).setParameter("uf", uf).getResultList();
    }

    public State searchState(State state){
        String jpql = "SELECT s.code FROM State s WHERE 1=1 ";

        if (state.getName() != null)
            jpql = jpql + "AND s.name LIKE :name ";

        if (state.getUf() != null)
            jpql = jpql + "AND s.uf LIKE :uf ";

        TypedQuery<Long> query = this.em.createQuery(jpql, Long.class);

        if (state.getName() != null)
            query.setParameter("name", state.getName() + "%");

        if (state.getUf() != null)
            query.setParameter("uf", state.getUf() + "+");

        Long stateId = query.getSingleResult();

        return this.em.find(State.class, stateId);
    }

    public City searchCity(City cityA){
        StringBuilder jpql = new StringBuilder("SELECT c FROM City c WHERE 1=1 ");

        Map<String, Object> params = new HashMap<>();

        if (cityA.getCity() != null && !cityA.getCity().isBlank()) {
            jpql.append("AND c.city LIKE :city ");
            params.put("city", cityA.getCity() + "%");
        }

        if (cityA.getState().getUf() != null && !cityA.getState().getUf().isBlank()) {
            jpql.append("AND c.state.uf LIKE :uf ");
            params.put("uf", cityA.getState().getUf());
        }

        if (cityA.getState().getName() != null && !cityA.getState().getName().isBlank()) {
            jpql.append("AND c.state.name LIKE :state ");
            params.put("state", cityA.getState().getName());
        }

        TypedQuery<City> query = this.em.createQuery(jpql.toString(), City.class);

        params.forEach(query::setParameter);

        if (params.isEmpty())
            throw new IllegalArgumentException("At least one filter must be informed.");

        List<City> result = query.getResultList();

        return result.isEmpty() ? null : result.getFirst();
    }
}
