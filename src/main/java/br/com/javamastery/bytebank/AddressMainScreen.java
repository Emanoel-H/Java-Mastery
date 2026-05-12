package br.com.javamastery.bytebank;

import br.com.javamastery.dao.AddressDAO;
import br.com.javamastery.models.City;
import br.com.javamastery.models.State;
import br.com.javamastery.util.JPAUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Scanner;

public class AddressMainScreen {
    static void main() {
        JPAUtils jpaUtils = new JPAUtils();
        EntityManager em = jpaUtils.getEntityManager();
        AddressDAO addressDAO = new AddressDAO(em);
        Scanner sc = new Scanner(System.in);
        List<State> allStates = addressDAO.searchAllState();
        State stateA = new State();

        System.out.println("Which state of Brazil you want to search the cities?");
        allStates.forEach(s2 -> System.out.println(s2.toString()));
        String stateName   = sc.nextLine();

        if (stateName.length() > 2)
            stateA.setName(stateName);
        else
            stateA.setUf(stateName);

        State  stateDB = addressDAO.searchState(stateA);

        List<City> citiesByState = addressDAO.searchCitiesByState(stateName);
        System.out.println("These are the cities of the state " + stateDB.getName() + ":");
        citiesByState.forEach(c2 -> System.out.println(c2.toString()));
    }
}
