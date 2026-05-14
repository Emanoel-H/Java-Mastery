package br.com.javamastery.dao;

import br.com.javamastery.models.Person;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class TravelerDAO {
    private EntityManager em;



    public TravelerDAO(EntityManager em) {
        this.em = em;
    }

//    public List<Person> searchTraveler(){
//
//    }

    public Person searchTraveler(Person travelerA){
        String jpql = "SELECT t FROM Person t WHERE 1=1";

        if (travelerA.getCpf() != null)
            jpql = jpql + "AND t.cpf LIKE :cpf ";

        TypedQuery<Long> query = this.em.createQuery(jpql, Long.class);


        return em.find(Person.class, 1L);
    }
}
