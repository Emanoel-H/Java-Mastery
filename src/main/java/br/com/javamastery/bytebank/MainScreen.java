package br.com.javamastery.bytebank;

import br.com.javamastery.dao.AddressDAO;
import br.com.javamastery.dao.BusTicketDAO;
import br.com.javamastery.dao.EmailDAO;
import br.com.javamastery.dao.TravelerDAO;
import br.com.javamastery.models.BusTicket;
import br.com.javamastery.models.City;
import br.com.javamastery.models.Email;
import br.com.javamastery.models.Traveler;
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
//        updateTicket();
        JPAUtils jpaUtils = new JPAUtils();
        EntityManager em = jpaUtils.getEntityManager();
        EmailDAO emailDAO = new EmailDAO(em);
        TravelerDAO travelerDAO = new TravelerDAO(em);
        Email emailA = new Email();
        Scanner sc = new Scanner(System.in);
        boolean exitSystem = false;
        boolean emailFilled = false;
        boolean accessGranted = false;
        String emailAddress, password;
        Traveler travelerA;
        Traveler travelerDB;
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        em.getTransaction().begin();
        while (!exitSystem) {
            System.out.println("Please, fill up your data to log in:");
            System.out.print("Email: ");
            emailAddress = sc.nextLine();
            emailA.setEmail(emailAddress);

            if (!emailDAO.emailExists(emailA)) {
                System.out.print("""
                        \nIncorrect email!
                        1 - Insert a new email address.
                        2 - Sign up if you don't have an email yet.
                        3 - Exit
                        """);
                int emailChoice = sc.nextInt();
                sc.nextLine();

                switch (emailChoice) {
                    case 1:
                        emailFilled = false;
                        break;
                    case 2:
                        signUp(sc, parser, travelerDAO);
                        em.getTransaction().commit();
                        emailFilled = false;
                        break;
                    case 3:
                        exitSystem = true;
                        break;
                    default:
                        System.out.println("Type in a valid answer!");
                        exitSystem = true;
                }
            }else
                emailFilled = true;

            if (emailFilled){
                System.out.print("\nPassword: ");
                password = sc.nextLine();
                emailA.setPassword(password);

                if (!emailDAO.emailExists(emailA)){
                    System.out.println("Invalid password!");
                    emailFilled = false;
                }else
                    accessGranted = true;
            }

            if (accessGranted){
                while (!exitSystem) {
                    System.out.println("""
                            ----------Welcome to your best BusApp----------
                            1 - Buy a ticket
                            2 - View your tickets
                            3 - Update Profile
                            4 - Exit
                            -----------------------------------------------
                            """);
                    int busAppChoice = sc.nextInt();
                    sc.nextLine();
                    switch (busAppChoice) {
                        case 1:
                            travelerA = new Traveler();
                            travelerA.setEmail(emailA);
                            travelerDB = travelerDAO.searchPerson(travelerA);
                            buyBusTickets(travelerDB);
                            break;
                        case 2:
                            viewTickets(emailA);
                            break;
                        case 3:
                            updateProfile(emailA, travelerDAO, sc, parser);
                            em.getTransaction().commit();
                            break;
                        case 4:
                            exitSystem = true;
                            break;
                        default:
                            System.out.println("Type in a valid answer!");
                            exitSystem = true;
                    }

                }
            }
        }
        em.close();
    }

    private static void updateProfile(Email emailA, TravelerDAO travelerDAO, Scanner sc, DateTimeFormatter parser) {
        String password;
        Traveler travelerA;
        Traveler travelerDB;
        travelerA = new Traveler();
        travelerA.setEmail(emailA);
        travelerDB = travelerDAO.searchPerson(travelerA);
        System.out.print("""
        What do you want to alter on your profile?
        1 - Traveler's name
        2 - Traveler's CPF
        3 - Traveler's birth date
        4 - Password
        5 - Delete Profile
        """);
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice){
            case 1:
                System.out.println("Type in your name: ");
                String name = sc.nextLine();
                travelerDB.setName(name);
                travelerDAO.update(travelerDB);
                break;
            case 2:
                System.out.println("Type in your CPF: ");
                String cpf = sc.nextLine();
                travelerDB.setCpf(cpf);
                travelerDB.setEditedAt(LocalDate.now());
                travelerDAO.update(travelerDB);
                break;
            case 3:
                System.out.println("Type in your birth date: ");
                String dateFormatted = sc.nextLine();
                LocalDate birthDate = LocalDate.parse(dateFormatted, parser);
                travelerDB.setBirthDate(birthDate);
                travelerDB.setEditedAt(LocalDate.now());
                travelerDAO.update(travelerDB);
                break;
            case 4:
                System.out.println("Type in your new password: ");
                password = sc.nextLine();
                travelerDB.getEmail().setPassword(password);
                travelerDB.setEditedAt(LocalDate.now());
                travelerDAO.update(travelerDB);
                break;
            case 5:
                travelerDAO.delete(travelerDB);
                break;
            default:
                System.out.println("Type in a valid answer!");
        }
    }

    private static void signUp(Scanner sc, DateTimeFormatter parser, TravelerDAO travelerDAO) {
        String password;
        String emailAddress;
        System.out.println("Type ur name:");
        String name = sc.nextLine();

        System.out.println("Type ur birth date:");
        String dateFormatted = sc.nextLine();
        LocalDate birthDate = LocalDate.parse(dateFormatted, parser);

        System.out.println("Type ur CPF:");
        String cpf = sc.nextLine();

        System.out.print("Email: ");
        emailAddress = sc.nextLine();

        System.out.print("\nPassword: ");
        password = sc.nextLine();

        System.out.println("Type ur telephone:");
        String telephone = sc.nextLine();

        Traveler travelerA = new Traveler();
        travelerA.setName(name);
        travelerA.setBirthDate(birthDate);
        travelerA.setCpf(cpf);
        travelerA.getEmail().setEmail(emailAddress);
        travelerA.getEmail().setPassword(password);
        travelerA.setCreatedAt(LocalDate.now());
        travelerA.setEditedAt(LocalDate.now());
        travelerA.setTelephone(telephone);

        travelerDAO.save(travelerA);
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
        String cpf;
        em.getTransaction().begin();

        System.out.print("""
                Do you want to alter an info from your bus ticket?
                1 - Yes
                2 - No
                """);
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
//            System.out.println("Type in your CPF");
//            String cpf = sc.nextLine();
//            busTicketA.getTraveler().setCpf(cpf);
//            List<BusTicket> allTickets = busTicketDao.searchTickets(busTicketA);
//            String messageToDisplay = "Here is your ticket: ";
//
//            if (allTickets.size() > 1)
//                messageToDisplay = "Here are your tickets: ";
//
//            System.out.println(messageToDisplay);
//            allTickets.forEach(bt2 -> System.out.println(bt2.toString()));

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

    private static void viewTickets(Email email) {
        JPAUtils jpaUtils = new JPAUtils();
        EntityManager em = jpaUtils.getEntityManager();
        BusTicketDAO busTicketDao = new BusTicketDAO(em);
        BusTicket busTicketA = new BusTicket();
        Traveler travelerA = new Traveler();
        travelerA.setEmail(email);
        TravelerDAO travelerDAO = new TravelerDAO(em);

        em.getTransaction().begin();

        busTicketA.getTraveler().setCpf(travelerDAO.searchPerson(travelerA).getCpf());
        List<BusTicket> allTickets = busTicketDao.searchTickets(busTicketA);
        String messageToDisplay = "Here is your ticket: ";

        if (allTickets.size() > 1)
            messageToDisplay = "Here are your tickets: ";

        System.out.println(messageToDisplay);
        allTickets.forEach(bt2 -> System.out.println(bt2.toString()));

        if (!allTickets.isEmpty())
            updateTicket();

        System.out.println("Do you wish to cancel a trip?");

        em.getTransaction().commit();
        em.close();
    }

    private static void buyBusTickets(Traveler traveler) {
        Scanner sc = new Scanner(System.in);
        List<BusTicket> busTicketList = new ArrayList<>();
        int endTickets = 0;
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean bTraveller = false;
        String name, cpf;
        LocalDate birthDate;

        JPAUtils jpaUtils = new JPAUtils();
        EntityManager em = jpaUtils.getEntityManager();
        BusTicketDAO busTicketDao = new BusTicketDAO(em);
        AddressDAO addressDAO = new AddressDAO(em);
        City cityA = new City();

        System.out.println("---------------------------");
        System.out.println("      Bus Tickets App      ");

        em.getTransaction().begin();
        while (endTickets != 1){
            System.out.println("""
                    Are you the one who is travelling?
                    1 - Yes
                    2 - No
                    """);
            String answerTraveller = sc.nextLine();

            if (answerTraveller == "1")
                bTraveller = true;

            BusTicket busTicket = new BusTicket();

            if (!bTraveller) {
                System.out.println("Type ur name:");
                name = sc.nextLine();

                System.out.println("Type ur birth date:");
                String dateFormatted = sc.nextLine();
                birthDate = LocalDate.parse(dateFormatted, parser);

                System.out.println("Type ur CPF:");
                cpf = sc.nextLine();
            } else {
                name = traveler.getName();
                birthDate = traveler.getBirthDate();
                cpf = traveler.getCpf();
            }

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
