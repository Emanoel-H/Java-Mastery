package br.com.javamastery.bytebank;

import br.com.javamastery.dao.AddressDAO;
import br.com.javamastery.dao.BusTicketDAO;
import br.com.javamastery.models.BusTicket;
import br.com.javamastery.models.City;
import br.com.javamastery.util.JPAUtils;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainScreen {
    static void main() {
//        buyBusTickets();
//        viewTickets();
        updateTicket();
    }

    private static void updateTicket() {
        JPAUtils jpaUtils = new JPAUtils();
        EntityManager em = jpaUtils.getEntityManager();
        BusTicketDAO busTicketDao = new BusTicketDAO(em);
        AddressDAO addressDAO = new AddressDAO(em);
        City cityA = new City();
        BusTicket busTicketSought;
        City cityDB;
        List<City> allCities = addressDAO.searchCitiesByState("RJ");
        cityA.getState().setUf("RJ");
        BusTicket busTicketA = new BusTicket();
        Scanner sc = new Scanner(System.in);
        em.getTransaction().begin();

        System.out.print("""
                Do you want to alter an info from your bus ticket?
                1 - Yes
                2 - No
                """);
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            System.out.println("Type in your CPF");
            String cpf = sc.nextLine();
            busTicketA.getTraveler().setCpf(cpf);
            List<BusTicket> allTickets = busTicketDao.searchTickets(busTicketA);
            String messageToDisplay = "Here is your ticket: ";

            if (allTickets.size() > 1)
                messageToDisplay = "Here are your tickets: ";

            System.out.println(messageToDisplay);
            allTickets.forEach(bt2 -> System.out.println(bt2.toString()));

            System.out.println("Type in the code of the ticket you want to alter: ");
            String ticketCode = sc.nextLine();
            busTicketA.setCode(ticketCode);
            busTicketSought = busTicketDao.searchSingleTicket(busTicketA);

            System.out.print("""
                    What do you want to alter on your ticket?
                    1- Traveler's name
                    2- Traveler's CPF
                    3- Traveler's birth date
                    4- Origin City
                    5- Destination City
                    """);
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice){
                case 1:
                    System.out.println("Type in your name: ");
                    String name = sc.nextLine();
                    busTicketSought.getTraveler().setName(name);
                    busTicketDao.update(busTicketSought);
                    break;
                case 2:
                    System.out.println("Type in your CPF: ");
                    cpf = sc.nextLine();
                    busTicketSought.getTraveler().setCpf(cpf);
                    busTicketDao.update(busTicketSought);
                    break;
                case 3:
                    DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    System.out.println("Type in your birth date: ");
                    String dateFormatted = sc.nextLine();
                    LocalDate birthDate = LocalDate.parse(dateFormatted, parser);
                    busTicketSought.getTraveler().setBirthDate(birthDate);
                    busTicketDao.update(busTicketSought);
                    break;
                case 4:
                    allCities.forEach(c2 -> System.out.println(c2.getCity()));
                    System.out.println("Type in your Origin City: ");
                    String originCity = sc.nextLine();
                    cityA.setCity(originCity);
                    cityDB = addressDAO.searchCity(cityA);
                    busTicketSought.setOriginCity(cityDB.getCity());
                    busTicketDao.update(busTicketSought);
                    break;
                case 5:
                    allCities.forEach(c2 -> System.out.println(c2.getCity()));
                    System.out.println("Type in your Destination City: ");
                    String destinationCity = sc.nextLine();
                    cityA.setCity(destinationCity);
                    cityDB = addressDAO.searchCity(cityA);
                    busTicketSought.setOriginCity(cityDB.getCity());
                    busTicketDao.update(busTicketSought);
                    break;
                default:
                    System.out.println("Type in a valid number!");
            }
        }

        em.getTransaction().commit();

        busTicketSought = busTicketDao.searchSingleTicket(busTicketA);
        System.out.println(busTicketSought.toString());

        em.close();
    }

    private static void viewTickets() {
        JPAUtils jpaUtils = new JPAUtils();
        EntityManager em = jpaUtils.getEntityManager();
        BusTicketDAO busTicketDao = new BusTicketDAO(em);


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
        BusTicketDAO busTicketDao = new BusTicketDAO(em);
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
