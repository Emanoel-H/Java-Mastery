package br.com.javamastery.service;

import br.com.javamastery.dao.EmailDAO;
import br.com.javamastery.exception.EmailAlreadyExistsException;
import br.com.javamastery.exception.InvalidCredentialsException;
import br.com.javamastery.models.Email;
import jakarta.persistence.EntityManager;

import java.util.Objects;

public class AuthService {
    private EmailDAO emailDAO;

    public AuthService(EntityManager em) {
        this.emailDAO = new EmailDAO(em);
    }

    public Email login(String email, String password) {
        Email emailA = new Email();
        emailA.setEmail(email);
        emailA.setPassword(password);

        if (!emailDAO.emailExists(emailA))
            throw new InvalidCredentialsException();

        return emailA;
    }

    public void checkEmailAvailable(String email) {
        Email emailA = new Email();
        emailA.setEmail(email);

        if (emailDAO.emailExists(emailA))
            throw new EmailAlreadyExistsException(email);
    }

    public boolean emailExists(String email) {
        Email emailA = new Email();
        emailA.setEmail(Objects.requireNonNull(email, "Email address cannot be null."));
        return emailDAO.emailExists(emailA);
    }

}
