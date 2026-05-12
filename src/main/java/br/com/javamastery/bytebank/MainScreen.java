package br.com.javamastery.bytebank;

import br.com.javamastery.dao.AddressDAO;
import br.com.javamastery.dao.BusTicketDao;
import br.com.javamastery.models.BusTicket;
import br.com.javamastery.models.City;
import br.com.javamastery.util.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainScreen {
    static void main() {
        buyBusTickets();
//        viewTickets();
    }

    private static void viewTickets() {
        JPAUtils jpaUtils = new JPAUtils();
        EntityManager em = jpaUtils.getEntityManager();
        BusTicketDao busTicketDao = new BusTicketDao(em);


        List<BusTicket> allTickets = busTicketDao.searchAll();
        allTickets.forEach(bt2 -> System.out.println(bt2.toString()));
    }

    private static void buyBusTickets() {
        Scanner sc = new Scanner(System.in);
        List<BusTicket> busTicketList = new ArrayList<>();
        int endTickets = 0;
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        JPAUtils jpaUtils = new JPAUtils();
        EntityManager em = jpaUtils.getEntityManager();
        BusTicketDao busTicketDao = new BusTicketDao(em);
        AddressDAO addressDAO = new AddressDAO(em);
        City cityA = new City();

        System.out.println("---------------------------");
        System.out.println("      Bus Tickets App      ");

        em.getTransaction().begin();
        while (endTickets != 1){
            BusTicket busTicket = new BusTicket();
            System.out.println("Type ur name:");
            String name = sc.nextLine();

            System.out.println("Type ur birth date:");
            String dateFormatted = sc.nextLine();
            LocalDate birthDate = LocalDate.parse(dateFormatted, parser);

            System.out.println("Type ur CPF:");
            String cpf = sc.nextLine();

            System.out.println("Where is your origin?");
            List<City> allCities = addressDAO.searchCitiesByState("RJ");
            allCities.forEach(c2 -> System.out.println(c2.getCity()));
            String originCity = sc.nextLine();
            cityA.setCity(originCity);
            cityA.getState().setUf("RJ");
            City cityDB = addressDAO.searchCity(cityA);
            busTicket.setOriginCity(cityDB.getCity());

            System.out.println("Where is your destination?");
            allCities.forEach(c2 -> System.out.println(c2.toString()));
            String destinationCity = sc.nextLine();
            cityA.setCity(destinationCity);
            cityDB = addressDAO.searchCity(cityA);
            busTicket.setDestinationCity(cityDB.getCity());

            busTicket.setTicketPrice(new BigDecimal("49.90"));
            busTicket.getTraveler().setName(name);
            busTicket.getTraveler().setCpf(cpf);
            busTicket.getTraveler().setBirthDate(birthDate);

            busTicketList.add(busTicket);

            busTicketDao.save(busTicket);

            System.out.println("You want to add another traveler to this buying?");
            endTickets = sc.nextInt();
            sc.nextLine();
        }

        em.getTransaction().commit();
        em.close();

        for (BusTicket busTicket : busTicketList) {
            System.out.println(busTicket.toString());
        }
    }
}
