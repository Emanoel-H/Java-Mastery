package br.com.javamastery.bytebank;

import br.com.javamastery.dao.*;
import br.com.javamastery.exception.CancellationDeadlineExceededException;
import br.com.javamastery.exception.InvalidCredentialsException;
import br.com.javamastery.exception.TicketNotFoundException;
import br.com.javamastery.models.*;
import br.com.javamastery.service.AuthService;
import br.com.javamastery.service.TicketService;
import br.com.javamastery.service.TravelerService;
import br.com.javamastery.service.TripService;
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
        TravelerDAO travelerDAO = new TravelerDAO(em);
        Scanner sc = new Scanner(System.in);
        boolean exitSystem = false;
        Email emailA = new Email();
        boolean accessGranted = false;
        String emailAddress, password;
        Traveler travelerA;
        Traveler travelerDB;
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        AuthService authService = new AuthService(em);
        TicketService ticketService =  new TicketService(em);
        TripService tripService = new TripService(em);
        TravelerService travelerService = new TravelerService(em);

        while (!exitSystem) {
            System.out.println("Please, fill up your data to log in:");
            System.out.print("Email: ");
            emailAddress = sc.nextLine();

            if (!authService.emailExists(emailAddress)) {
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
                        break;
                    case 2:
                        signUp(sc, parser, authService, travelerService);
                        break;
                    case 3:
                        exitSystem = true;
                        break;
                    default:
                        System.out.println("Type in a valid answer!");
                        exitSystem = true;
                }
                continue;
            }

            System.out.print("\nPassword: ");
            password = sc.nextLine();

            try{
                emailA = authService.login(emailAddress, password);
                accessGranted = true;
            }catch(InvalidCredentialsException e){
                System.out.println("Invalid password!");
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
                            buyBusTickets(travelerDB, em, ticketService, tripService);
                            break;
                        case 2:
                            viewTickets(emailA, em, ticketService);
                            break;
                        case 3:
                            updateProfile(emailA, travelerDAO, sc, parser, em, exitSystem);
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

    private static void updateProfile(Email emailA, TravelerDAO travelerDAO, Scanner sc, DateTimeFormatter parser, EntityManager em, boolean exitSystem) {
        String password;
        Traveler travelerA;
        Traveler travelerDB;
        travelerA = new Traveler();
        travelerA.setEmail(emailA);
        travelerDB = travelerDAO.searchPerson(travelerA);

        boolean exitWhile = false;
        while (!exitWhile) {
            System.out.println(travelerDB);
            System.out.print("""
                    What do you want to alter on your profile?
                    1 - Traveler's name
                    2 - Traveler's CPF
                    3 - Traveler's birth date
                    4 - Traveler's Telephone
                    5 - Password
                    6 - Delete Profile
                    7 - Exit
                    """);
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
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

                    em.getTransaction().begin();
                    travelerDAO.update(travelerDB);
                    em.getTransaction().commit();
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
                    System.out.println("Type in your telephone: ");
                    String telephone = sc.nextLine();
                    travelerDB.setTelephone(telephone);

                    em.getTransaction().begin();
                    travelerDAO.update(travelerDB);
                    em.getTransaction().commit();
                    break;
                case 5:
                    System.out.println("Type in your new password: ");
                    password = sc.nextLine();
                    travelerDB.getEmail().setPassword(Objects.requireNonNull(password, "Cannot be empty"));

                    em.getTransaction().begin();
                    travelerDAO.update(travelerDB);
                    em.getTransaction().commit();
                    break;
                case 6:
                    em.getTransaction().begin();
                    travelerDAO.delete(travelerDB);
                    em.getTransaction().commit();
                    exitWhile = true;
                    exitSystem = exitWhile;
                    break;
                case 7:
                    System.out.println("Exiting...");
                    exitWhile = true;
                    break;
                default:
                    System.out.println("Type in a valid answer!");
            }
        }
    }

    private static void signUp(Scanner sc, DateTimeFormatter parser, AuthService authService, TravelerService travelerService) {
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
        authService.checkEmailAvailable(emailAddress);

        System.out.print("\nPassword: ");
        password = sc.nextLine();

        System.out.println("Type ur telephone:");
        String telephone = sc.nextLine();

        travelerService.signUp(name, birthDate, cpf, emailAddress, password, telephone);
    }

    private static void updateTicket(EntityManager em, TicketService ticketService) {
        BusTicketDAO busTicketDao = new BusTicketDAO(em);
        BusTicket busTicketSought;
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
            boolean getBack = true;
            while (getBack) {
                System.out.println("Type in the code of the ticket you want to alter: \n(Type 'C' to cancel) ");
                String ticketCode = sc.nextLine();
                if (!ticketCode.equalsIgnoreCase("C")) {
                    busTicketA.setCode(ticketCode);

                    busTicketSought = busTicketDao.searchSingleTicket(busTicketA);
                    System.out.println(busTicketSought);

                    if (busTicketSought == null)
                        throw new TicketNotFoundException(ticketCode);
                    else {
                        boolean exitWhile = false;
                        while (!exitWhile) {
                            System.out.print("""
                                    What do you want to alter on your ticket?
                                    1- Traveler's name
                                    2- Traveler's CPF
                                    3- Traveler's birth date
                                    4- Exit
                                    """);
                            choice = sc.nextInt();
                            sc.nextLine();
                            switch (choice) {
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
                                case 4:
                                    System.out.println("Exiting...");
                                    exitWhile = true;
                                    break;
                                default:
                                    System.out.println("Type in a valid number!");
                            }
                        }
                    }
                }else
                    getBack = false;
            }
        }
    }

    private static void viewTickets(Email email, EntityManager em, TicketService  ticketService) {
        BusTicketDAO busTicketDao = new BusTicketDAO(em);
        BusTicket busTicketA = new BusTicket();
        Traveler travelerA = new Traveler();
        travelerA.setEmail(email);
        TravelerDAO travelerDAO = new TravelerDAO(em);
        Scanner sc = new  Scanner(System.in);

        System.out.println("Do you wish to also view the canceled tickets? \n0- No \n1- Yes");
        String canceledTickets = sc.nextLine();

        busTicketA.setCanceled(canceledTickets.trim().replaceAll("\\D", "").equals("1") || canceledTickets.trim().replaceAll("\\d", "").equalsIgnoreCase("Yes"));

        busTicketA.getTraveler().setId(travelerDAO.searchPerson(travelerA).getId());
        List<BusTicket> allTickets = busTicketDao.searchTickets(busTicketA);

        if (!allTickets.isEmpty()) {
            String messageToDisplay = "Here is your ticket: ";

            if (allTickets.size() > 1)
                messageToDisplay = "Here are your tickets: ";

            System.out.println(messageToDisplay);
            allTickets.forEach(bt2 -> System.out.println(bt2.toString()));

            if (!allTickets.isEmpty()) {
                updateTicket(em, ticketService);
                cancelTicket(ticketService);
            }
        }else
            System.out.println("There is no tickets in your database!");

    }

    private static void cancelTicket(TicketService  ticketService) {
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
                String ticketCode = sc.nextLine();
                if (!ticketCode.equalsIgnoreCase("C")) {
                    ticketService.cancelTicket(ticketCode);
                    System.out.println("Ticket canceled, check your balance!");
                }
                getBack = false;
            }
        }
    }

    private static void buyBusTickets(Traveler traveler, EntityManager em, TicketService ticketService, TripService tripService) {
        Scanner sc = new Scanner(System.in);
        List<BusTicket> busTicketList = new ArrayList<>();
        int endTickets = 0;
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("ddMMyyyy");
        AddressDAO addressDAO = new AddressDAO(em);
        Trip tripA = new Trip();
        Trip trip = null;

        System.out.println("---------------------------");
        System.out.println("      Bus Tickets App      ");

        while (endTickets != 1){
            boolean getBackCities = true;
            BusTicket busTicket = new BusTicket();

            while (getBackCities) {
                City origin = collectOriginCity(sc, addressDAO);
                tripA.setOriginCity(origin);

                City destination = collectDestinationCity(sc, addressDAO);
                tripA.setDestinationCity(destination);

                List<Trip> availableTrips = tripService.searchTrips(tripA);

                if (!availableTrips.isEmpty()) {
                    trip = collectTrip(sc, availableTrips, tripService);
                    getBackCities = false;
                } else
                    throw new RuntimeException("There are no trips matching these cities you selected! Try again!");
            }

            LocalDate departureDate = collectDepartureDate(sc, parser);

            Traveler travelerA = collectTraveler(sc, traveler, parser);

            busTicketList.add(ticketService.buyTicket(trip, departureDate, travelerA));

            System.out.println("Do you want to buy another ticket? \n0- No \n1- Yes");
            endTickets = sc.nextInt();
            sc.nextLine();
        }

        busTicketList.forEach(System.out::println);
    }

    public static City collectOriginCity(Scanner sc, AddressDAO addressDAO){
        String cityName = "";
        String stateName = "";
        City originCity = null;
        City cityA = new City();

        while (cityName.isEmpty()) {
            System.out.println("Type in the name of the origin city: \nIf you wish to view the cities before type 1");
            cityName = sc.nextLine();

            if (cityName.trim().charAt(0) == '1')
                originCity = viewCities(stateName, addressDAO, sc, cityName);
            else {
                cityA.setCity(cityName.toLowerCase());
                originCity = addressDAO.searchCity(cityA);

                if (originCity == null){
                    cityName = "";
                    System.out.println("Type in a valid value!");
                }
            }
        }

        return originCity;
    }

    private static City viewCities(String stateName, AddressDAO addressDAO, Scanner sc, String cityName) {
        City cityDB = null;
        cityName = "";
        while (stateName.isEmpty()) {
            System.out.println("From which state do you want to search the cities?");
            List<State> allStates = addressDAO.searchAllState();
            allStates.forEach(s -> System.out.println(s.toString()));
            stateName = sc.nextLine();

            if (stateName.trim().length() > 2) {
                String finalStateName = stateName;
                if (allStates.stream().noneMatch(s -> s.getName().trim().equalsIgnoreCase(finalStateName.trim()))) {
                    stateName = "";
                    System.out.println("Type in a valid value!");
                }else {
                    stateName = allStates
                            .stream()
                            .map(State::getName)
                            .filter(s -> s.trim().equalsIgnoreCase(finalStateName.trim()))
                            .findFirst()
                            .orElse(null);
                }
            }else {
                String finalStateUF = stateName;
                if (allStates.stream().noneMatch(s -> s.getUf().equals(finalStateUF))){
                    stateName = "";
                    System.out.println("Type in a valid value!");
                }else {
                    stateName = allStates
                            .stream()
                            .map(State::getName)
                            .filter(s -> s.trim().equalsIgnoreCase(finalStateUF.trim()))
                            .findFirst()
                            .orElse(null);
                }
            }
        }

        if (!stateName.isEmpty()) {
            while (cityName.isEmpty()) {
                List<City> citiesByState = addressDAO.searchCitiesByState(stateName);
                System.out.printf("Cities from the state %s\n", stateName);
                citiesByState.forEach(c -> System.out.println(c.toString()));
                System.out.println("Type in your selected city:");
                cityName = sc.nextLine();

                if (!cityName.isEmpty()) {
                    String finalCityName = cityName;
                    if (citiesByState.stream().noneMatch(c -> c.getCity().trim().equalsIgnoreCase(finalCityName.trim())))
                        cityName = "";
                }
            }

            if (!cityName.isEmpty()){
                City cityA = new City();
                cityA.setCity(cityName);
                cityDB = addressDAO.searchCity(cityA);
            }
        }

        return cityDB;
    }

    private static City collectDestinationCity(Scanner sc, AddressDAO addressDAO){
        String cityName = "";
        String stateName = "";
        City destinationCity = null;
        City cityA = new City();

        while (cityName.isEmpty()) {
            System.out.println("Type in the name of the destination city: \nIf you wish to view the cities before type 1");
            cityName = sc.nextLine();

            if (cityName.trim().charAt(0) == '1')
                destinationCity = viewCities(stateName, addressDAO, sc, cityName);
            else {
                cityA.setCity(cityName.toLowerCase());
                destinationCity = addressDAO.searchCity(cityA);

                if (destinationCity == null){
                    cityName = "";
                    System.out.println("Type in a valid value!");
                }
            }
        }

        return destinationCity;
    }

    public static Trip collectTrip(Scanner sc, List<Trip> availableTrips, TripService tripService){
        boolean getBackTrips = true;
        Trip tripA = new Trip();
        Trip tripDB = null;
        while (getBackTrips) {
            availableTrips.forEach(trip -> System.out.println(trip.toString()));

            System.out.println("Type in the code of the trip you selected: ");
            String tripCode = sc.nextLine().trim();
            tripA.setCode(tripCode);
            tripDB = tripService.searchSingleTrip(tripA);
            if (tripDB != null) {
                getBackTrips = false;
            }else
                System.out.println("There is no trip with that code. Try again!");
        }

        return tripDB;
    }

    public static LocalDate collectDepartureDate(Scanner sc, DateTimeFormatter parser){
        LocalDate  departureDate = null;

        boolean getBackDeparture = true;
        while (getBackDeparture) {
            try {
                System.out.println("Type in the date of when you are willing to travel: (pattern: dd/MM/yyyy)");
                String departureDay = sc.nextLine();
                departureDate = LocalDate.parse(departureDay, parser);

                if (!departureDate.isBefore(LocalDate.now())) {
                    getBackDeparture = false;
                } else
                    System.out.println("Invalid departure date! You can't travel to past! Try again!");
            }catch (DateTimeParseException e) {
                System.out.println("Invalid departure date! Try again!");
            }
        }

        return departureDate;
    }

    public static Traveler collectTraveler(Scanner sc, Traveler buyer, DateTimeFormatter parser){
        Traveler traveler = new Traveler();
        boolean bTraveller = false;
        String name, cpf;
        LocalDate birthDate;

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
            name = buyer.getName();
            birthDate = buyer.getBirthDate();
            cpf = buyer.getCpf();
        }

        traveler.setName(Objects.requireNonNull(name, "Cannot be empty"));
        traveler.setCpf(cpf);
        traveler.setBirthDate(birthDate);

        return traveler;
    }
}
