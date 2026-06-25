package br.com.javamastery.service;

import br.com.javamastery.dao.AddressDAO;
import br.com.javamastery.models.City;
import br.com.javamastery.models.State;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AddressService {
    AddressDAO addressDAO;

    public AddressService(EntityManager em) {
        this.addressDAO = new AddressDAO(em);
    }

    public City searchCity(City city){
        return addressDAO.searchCity(city);
    }

    public List<State> searchAllState(){
        return addressDAO.searchAllState();
    }

    public List<City> searchCitiesByState(String stateName){
        return addressDAO.searchCitiesByState(stateName);
    }
}
