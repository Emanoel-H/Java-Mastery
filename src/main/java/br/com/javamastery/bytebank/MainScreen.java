package br.com.javamastery.bytebank;

import br.com.javamastery.dao.*;
import br.com.javamastery.models.*;
import br.com.javamastery.util.JPAUtils;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class MainScreen {
    public static void main(String[] args) {
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

        while (!exitSystem) {
            System.out.println("Please, fill up your data to log in:");
            System.out.print("Email: ");
            emailAddress = sc.nextLine();
            emailA.setEmail(Objects.requireNonNull(emailAddress, "Email address cannot be null."));

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
                        signUp(sc, parser, travelerDAO, em);
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
                emailA.setPassword(Objects.requireNonNull(password, "Password cannot be null."));

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
                            buyBusTickets(travelerDB, em);
                            break;
                        case 2:
                            viewTickets(emailA, em);
                            break;
                        case 3:
                            updateProfile(emailA, travelerDAO, sc, parser, em);
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

    private static void updateProfile(Email emailA, TravelerDAO travelerDAO, Scanner sc, DateTimeFormatter parser, EntityManager em) {
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
                String name = sc.nextLine().replaceAll("\\d", "");
                travelerDB.setName(Objects.requireNonNull(name, "Cannot be empty"));

                em.getTransaction().begin();
                travelerDAO.update(travelerDB);
                em.getTransaction().commit();
                break;
            case 2:
                System.out.println("Type in your CPF: ");
                String cpf = sc.nextLine();
                travelerDB.setCpf(cpf);
                travelerDAO.update(travelerDB);
                break;
            case 3:
                System.out.println("Type in your birth date: ");
                String dateFormatted = sc.nextLine();
                LocalDate birthDate = LocalDate.parse(dateFormatted, parser);
                travelerDB.setBirthDate(birthDate);

                em.getTransaction().begin();
                travelerDAO.update(travelerDB);
                em.getTransaction().commit();
                break;
            case 4:
                System.out.println("Type in your new password: ");
                password = sc.nextLine();
                travelerDB.getEmail().setPassword(Objects.requireNonNull(password, "Cannot be empty"));

                em.getTransaction().begin();
                travelerDAO.update(travelerDB);
                em.getTransaction().commit();
                break;
            case 5:
                em.getTransaction().begin();
                travelerDAO.delete(travelerDB);
                em.getTransaction().commit();
                break;
            default:
                System.out.println("Type in a valid answer!");
        }
    }

    private static void signUp(Scanner sc, DateTimeFormatter parser, TravelerDAO travelerDAO, EntityManager em) {
        String password;
        String emailAddress;
        System.out.println("Type ur name:");
        String name = sc.nextLine().replaceAll("\\d", "");

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
        travelerA.setName(Objects.requireNonNull(name, "Cannot be empty"));
        travelerA.setBirthDate(birthDate);
        travelerA.setCpf(cpf);
        travelerA.getEmail().setEmail(Objects.requireNonNull(emailAddress, "Cannot be empty"));
        travelerA.getEmail().setPassword(Objects.requireNonNull(password, "Cannot be empty"));
        travelerA.setTelephone(telephone);

        em.getTransaction().begin();
        travelerDAO.save(travelerA);
        em.getTransaction().commit();
    }

    private static void updateTicket(EntityManager em) {
        BusTicketDAO busTicketDao = new BusTicketDAO(em);
        City cityA = new City();
        BusTicket busTicketSought;
        cityA.getState().setUf("RJ");
        BusTicket busTicketA = new BusTicket();
        Scanner sc = new Scanner(System.in);
        String cpf;

        System.out.print("""
                Do you want to alter an info from your bus ticket?
                1 - Yes
                2 - No
                """);
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            System.out.println("Type in the code of the ticket you want to alter: ");
            String ticketCode = sc.nextLine();
            busTicketA.setCode(ticketCode);

            busTicketSought = busTicketDao.searchSingleTicket(busTicketA);
            System.out.println(busTicketSought);

            System.out.print("""
                    What do you want to alter on your ticket?
                    1- Traveler's name
                    2- Traveler's CPF
                    3- Traveler's birth date
                    """);
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice){
                case 1:
                    System.out.println("Type in your name: ");
                    String name = sc.nextLine().replaceAll("\\d", "");
                    busTicketSought.getTraveler().setName(Objects.requireNonNull(name, "Cannot be empty"));

                    em.getTransaction().begin();
                    busTicketDao.update(busTicketSought);
                    em.getTransaction().commit();
                    break;
                case 2:
                    System.out.println("Type in your CPF: ");
                    cpf = sc.nextLine();
                    busTicketSought.getTraveler().setCpf(cpf);

                    em.getTransaction().begin();
                    busTicketDao.update(busTicketSought);
                    em.getTransaction().commit();
                    break;
                case 3:
                    DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    System.out.println("Type in your birth date: ");
                    String dateFormatted = sc.nextLine();
                    LocalDate birthDate = LocalDate.parse(dateFormatted, parser);
                    busTicketSought.getTraveler().setBirthDate(birthDate);

                    em.getTransaction().begin();
                    busTicketDao.update(busTicketSought);
                    em.getTransaction().commit();
                    break;
                default:
                    System.out.println("Type in a valid number!");
            }
        }
    }

    private static void viewTickets(Email email, EntityManager em) {
        BusTicketDAO busTicketDao = new BusTicketDAO(em);
        BusTicket busTicketA = new BusTicket();
        Traveler travelerA = new Traveler();
        travelerA.setEmail(email);
        TravelerDAO travelerDAO = new TravelerDAO(em);

        busTicketA.getTraveler().setCpf(travelerDAO.searchPerson(travelerA).getCpf());
        List<BusTicket> allTickets = busTicketDao.searchTickets(busTicketA);
        String messageToDisplay = "Here is your ticket: ";

        if (allTickets.size() > 1)
            messageToDisplay = "Here are your tickets: ";

        System.out.println(messageToDisplay);
        allTickets.forEach(bt2 -> System.out.println(bt2.toString()));

        if (!allTickets.isEmpty()) {
            updateTicket(em);
            cancelTicket(busTicketA, busTicketDao, em);
        }
    }

    private static void cancelTicket(BusTicket busTicketA, BusTicketDAO busTicketDao, EntityManager em) {
        Scanner sc = new Scanner(System.in);
        System.out.println("""
                Do you wish to cancel a trip?
                0 - No
                1 - Yes
                """);
        String cancelChoice = sc.nextLine();

        if (cancelChoice.equals("1") || cancelChoice.equalsIgnoreCase("Yes")){
            boolean getBack = true;
            while (getBack) {
                System.out.println("Type in the code of the ticket you want to alter: \n(Type 'C' to cancel) ");
                String tripCode = sc.nextLine();
                if (!tripCode.equalsIgnoreCase("C")) {
                    busTicketA.setCode(tripCode);
                    BusTicket busTicketDB = busTicketDao.searchSingleTicket(busTicketA);

                    if (busTicketDB == null)
                        throw new IllegalArgumentException("Type in a valid code!");
                    else {
                        LocalDateTime tripDateTime = LocalDateTime.of(busTicketDB.getDepartureDate(),
                                busTicketDB.getTrip().getDepartureTime());

                        LocalDateTime now = LocalDateTime.now();

                        if (now.isBefore(tripDateTime.minusHours(1))){
                            busTicketDB.setCancelDate(now);
                            busTicketDB.setCanceled(true);
                            
                            busTicketDB.getTraveler().setCreditsBalance(busTicketDB.getTicketPrice());

                            em.getTransaction().begin();
                            busTicketDao.update(busTicketDB);
                            em.getTransaction().commit();

                            System.out.println("Ticket canceled, check your balance!");
                        }else
                            System.out.println("The canceling time is already over!");

                        getBack = false;
                    }
                }else
                    getBack = false;

            }
        }
    }

    private static void buyBusTickets(Traveler traveler, EntityManager em) {
        Scanner sc = new Scanner(System.in);
        List<BusTicket> busTicketList = new ArrayList<>();
        int endTickets = 0;
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("ddMMyyyy");
        boolean bTraveller = false;
        String name, cpf;
        LocalDate birthDate;

        BusTicketDAO busTicketDao = new BusTicketDAO(em);
        AddressDAO addressDAO = new AddressDAO(em);
        TripDAO tripDAO = new TripDAO(em);
        Trip tripA = new Trip();
        City cityA = new City();

        System.out.println("---------------------------");
        System.out.println("      Bus Tickets App      ");

        while (endTickets != 1){
            boolean getBackCities = true;
            BusTicket busTicket = new BusTicket();

            while (getBackCities) {
                System.out.println("Where is your origin?");
                List<City> allCities = addressDAO.searchCitiesByState("RJ");
                allCities.forEach(c2 -> System.out.println(c2.getCity()));
                String originCity = sc.nextLine();
                cityA.setCity(originCity);
                cityA.getState().setUf("RJ");
                City cityDB = addressDAO.searchCity(cityA);
                tripA.setOriginCity(cityDB);

                System.out.println("Where is your destination?");
                allCities.forEach(c2 -> System.out.println(c2.toString()));
                String destinationCity = sc.nextLine();
                cityA.setCity(destinationCity);
                cityDB = addressDAO.searchCity(cityA);
                tripA.setDestinationCity(cityDB);

                List<Trip> availableTrips = tripDAO.searchTrips(tripA);

                if (!availableTrips.isEmpty()) {
                    boolean getBackTrips = true;
                    while (getBackTrips) {
                        availableTrips.forEach(trip -> System.out.println(trip.toString()));

                        System.out.println("Type in the code of the trip you selected: ");
                        String tripCode = sc.nextLine().trim();
                        tripA.setCode(tripCode);
                        Trip tripDB = tripDAO.searchSingleTrip(tripA);
                        if (tripDB != null) {
                            busTicket.setTrip(tripDB);
                            getBackTrips = false;
                        }else
                            System.out.println("There is no trip with that code. Try again!");
                    }
                    getBackCities = false;
                } else
                    throw new RuntimeException("There are no trips matching these cities you selected! Try again!");
            }
            boolean getBackDeparture = true;
            while (getBackDeparture) {
                try {
                    System.out.println("Type in the date of when you are willing to travel: (pattern: dd/MM/yyyy)");
                    String departureDay = sc.nextLine();
                    LocalDate departureDate = LocalDate.parse(departureDay, parser);

                    if (!departureDate.isBefore(LocalDate.now())) {
                        getBackDeparture = false;
                        busTicket.setDepartureDate(departureDate);
                    } else
                        System.out.println("Invalid departure date! You can't travel to past! Try again!");
                }catch (DateTimeParseException e) {
                    System.out.println("Invalid departure date! Try again!");
                }
            }

            System.out.println("""
                    Are you the one who is travelling?
                    1 - Yes
                    2 - No
                    """);
            String answerTraveller = sc.nextLine();

            if (answerTraveller.equals("1") || answerTraveller.equalsIgnoreCase("Yes"))
                bTraveller = true;

            if (!bTraveller) {
                System.out.println("Type ur name:");
                name = sc.nextLine().replaceAll("\\d", "");

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

            busTicket.getTraveler().setName(Objects.requireNonNull(name, "Cannot be empty"));
            busTicket.getTraveler().setCpf(cpf);
            busTicket.getTraveler().setBirthDate(birthDate);

            busTicketList.add(busTicket);

            em.getTransaction().begin();
            busTicketDao.save(busTicket);
            em.getTransaction().commit();

            System.out.println("Do you want to buy another ticket?");
            endTickets = sc.nextInt();
            sc.nextLine();
        }

        busTicketList.forEach(System.out::println);
    }
}
