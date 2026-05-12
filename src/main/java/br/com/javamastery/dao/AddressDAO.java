package br.com.javamastery.dao;

import br.com.javamastery.models.City;
import br.com.javamastery.models.State;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

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
        String jpql = "SELECT c.IBGE_code FROM City c WHERE 1=1 ";

        if (cityA.getCity() != null)
            jpql += "AND c.city LIKE :city ";

        if (cityA.getState().getUf() != null)
            jpql += "AND c.state.uf LIKE :uf ";

        if (cityA.getState().getName() != null)
            jpql += "AND c.state.name LIKE :state ";

        TypedQuery<Long> query = this.em.createQuery(jpql, Long.class);

        if (cityA.getCity() != null)
            query.setParameter("city", cityA.getCity() + "%");

        if (cityA.getState().getUf() != null)
            query.setParameter("uf", cityA.getState().getUf());

        if (cityA.getState().getName() != null)
            query.setParameter("state", cityA.getState().getName());

        Long cityId = query.setFirstResult(0).setMaxResults(1).getSingleResult();

        return this.em.find(City.class, cityId);
    }
}
