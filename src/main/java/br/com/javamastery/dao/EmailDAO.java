package br.com.javamastery.dao;

import br.com.javamastery.models.Email;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class EmailDAO {
    private EntityManager em;

    public EmailDAO(EntityManager em) {
        this.em = em;
    }

    public boolean emailExists(Email emailA){
        String jpql = "SELECT e FROM Email e WHERE 1=1 ";

        if (emailA.getEmail() != null)
            jpql += "AND e.email = :email ";

        if (emailA.getPassword() != null)
            jpql += "AND e.password = :password ";

        TypedQuery<Email> query = this.em.createQuery(jpql, Email.class);

        if (emailA.getEmail() != null)
            query.setParameter("email", emailA.getEmail());

        if (emailA.getPassword() != null)
            query.setParameter("password", emailA.getPassword());

        List<Email> emails = query.getResultList();

        return  emails.stream().anyMatch(e2 -> e2.getEmail().equals(emailA.getEmail()));
    }
}
