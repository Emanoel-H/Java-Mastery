package br.com.javamastery.service;

import br.com.javamastery.dao.TravelerDAO;
import br.com.javamastery.models.Traveler;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.Objects;

public class TravelerService {
    EntityManager em;
    TravelerDAO travelerDAO;

    public TravelerService(EntityManager em) {
        this.em = em;
        this.travelerDAO = new TravelerDAO(em);
    }

    public Traveler signUp(String name, LocalDate birthDate, String cpf, String email, String phone, String password) {
        Traveler traveler = new Traveler();
        traveler.setName(Objects.requireNonNull(name, "Cannot be empty"));
        traveler.setBirthDate(birthDate);
        traveler.setCpf(cpf);
        traveler.setTelephone(phone);
        traveler.getEmail().setEmail(Objects.requireNonNull(email, "Cannot be empty"));
        traveler.getEmail().setPassword(Objects.requireNonNull(password, "Cannot be empty"));

        try{
            this.em.getTransaction().begin();
            travelerDAO.save(traveler);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }

        return traveler;
    }

    public void updateName(Traveler traveler, String name) {
        traveler.setName(name);

        try{
            this.em.getTransaction().begin();
            this.travelerDAO.update(traveler);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateCPF(Traveler  traveler, String cpf) {
        traveler.setCpf(cpf);

        try{
            this.em.getTransaction().begin();
            this.travelerDAO.update(traveler);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateTelephone(Traveler traveler, String telephone) {
        traveler.setTelephone(telephone);

        try{
            this.em.getTransaction().begin();
            this.travelerDAO.update(traveler);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateBirthDate(Traveler traveler, LocalDate birthDate) {
        traveler.setBirthDate(birthDate);

        try{
            this.em.getTransaction().begin();
            this.travelerDAO.update(traveler);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updatePassword(Traveler traveler, String password) {
        traveler.getEmail().setPassword(password);

        try{
            this.em.getTransaction().begin();
            this.travelerDAO.update(traveler);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void deleteProfile(Traveler traveler){
        try{
            this.em.getTransaction().begin();
            this.travelerDAO.delete(traveler);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
}
