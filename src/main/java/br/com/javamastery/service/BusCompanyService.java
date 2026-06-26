package br.com.javamastery.service;

import br.com.javamastery.dao.BusCompanyDAO;
import br.com.javamastery.models.BusCompany;
import jakarta.persistence.EntityManager;

import java.util.Objects;

public class BusCompanyService {
    EntityManager em;
    BusCompanyDAO  busCompanyDAO;

    public BusCompanyService(EntityManager em) {
        this.em = em;
        busCompanyDAO = new BusCompanyDAO(em);
    }

    public BusCompany signUp(String legalName, String tradingName, String cnpj, String telephone, String emailAddress, String password){
        BusCompany busCompany = new BusCompany();
        busCompany.setLegalName(Objects.requireNonNull(legalName, "Cannot be empty"));
        busCompany.setTradingName(Objects.requireNonNull(tradingName, "Cannot be empty"));
        busCompany.setCnpj(cnpj);
        busCompany.setTelephone(telephone);
        busCompany.getEmail().setEmail(Objects.requireNonNull(emailAddress, "Cannot be empty"));
        busCompany.getEmail().setPassword(Objects.requireNonNull(password, "Cannot be empty"));

        try{
            this.em.getTransaction().begin();
            this.busCompanyDAO.save(busCompany);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw e;
        }

        return busCompany;
    }

    public BusCompany searchCompany(BusCompany busCompany){
        return busCompanyDAO.searchCompany(busCompany);
    }

    public void updateLegalName(BusCompany busCompany, String legalName){
        busCompany.setLegalName(Objects.requireNonNull(legalName, "Cannot be empty"));

        try{
            this.em.getTransaction().begin();
            this.busCompanyDAO.updateCompany(busCompany);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateTradingName(BusCompany busCompany, String tradingName){
        busCompany.setTradingName(Objects.requireNonNull(tradingName, "Cannot be empty"));

        try{
            this.em.getTransaction().begin();
            this.busCompanyDAO.updateCompany(busCompany);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateCnpj(BusCompany busCompany, String cnpj){
        busCompany.setCnpj(cnpj);

        try{
            this.em.getTransaction().begin();
            this.busCompanyDAO.updateCompany(busCompany);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updatePassword(BusCompany busCompany, String password){
        busCompany.getEmail().setPassword(Objects.requireNonNull(password, "Cannot be empty"));

        try{
            this.em.getTransaction().begin();
            this.busCompanyDAO.updateCompany(busCompany);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public void updateTelephone(BusCompany busCompany, String telephone){
        busCompany.setTelephone(telephone);

        try{
            this.em.getTransaction().begin();
            this.busCompanyDAO.updateCompany(busCompany);
            this.em.getTransaction().commit();
        }catch(Exception e){
            this.em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
}
