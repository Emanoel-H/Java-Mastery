package br.com.javamastery.bytebank;

import br.com.javamastery.client.OsrmClient;
import br.com.javamastery.dao.*;
import br.com.javamastery.exception.InvalidCredentialsException;
import br.com.javamastery.exception.InvalidPriceException;
import br.com.javamastery.exception.TripNotFoundException;
import br.com.javamastery.models.*;
import br.com.javamastery.service.AddressService;
import br.com.javamastery.service.AuthService;
import br.com.javamastery.service.BusCompanyService;
import br.com.javamastery.service.TripService;
import br.com.javamastery.util.JPAUtils;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class BusCompanyMainScreen {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        JPAUtils jpaUtils = new JPAUtils();
        EntityManager em = jpaUtils.getEntityManager();
        BusCompanyDAO busCompanyDAO = new BusCompanyDAO(em);
        boolean exitSystem = false;
        boolean accessGranted = false;
        String emailAddress, password;
        Email emailA = new Email();
        BusCompany busCompanyA;
        BusCompany busCompanyDB;
        AuthService authService = new AuthService(em);
        TripService tripService = new TripService(em);
        BusCompanyService busCompanyService = new BusCompanyService(em);
        AddressService addressService = new AddressService(em);

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
                        signUp(sc, authService, busCompanyService);
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
                            1 - Create a trip
                            2 - View your trips
                            3 - Update Profile
                            4 - Exit
                            -----------------------------------------------
                            """);
                    int busAppChoice = sc.nextInt();
                    sc.nextLine();
                    switch (busAppChoice) {
                        case 1:
                            busCompanyA = new BusCompany();
                            busCompanyA.setEmail(emailA);
                            busCompanyDB = busCompanyService.searchCompany(busCompanyA);
                            createTrip(sc, busCompanyDB, tripService, addressService);
                            break;
                        case 2:
                            busCompanyA = new BusCompany();
                            busCompanyA.setEmail(emailA);
                            busCompanyDB = busCompanyService.searchCompany(busCompanyA);
                            viewTrips(em, busCompanyDB, tripService, addressService);
                            break;
                        case 3:
                            busCompanyA = new BusCompany();
                            busCompanyA.setEmail(emailA);
                            busCompanyDB = busCompanyService.searchCompany(busCompanyA);
                            updateProfile(sc, busCompanyDB, busCompanyDAO, em, busCompanyService);
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

    private static void updateProfile(Scanner sc, BusCompany busCompanyDB, BusCompanyDAO busCompanyDAO, EntityManager em, BusCompanyService busCompanyService) {
        boolean exitWhile = false;
        while (!exitWhile){
            System.out.println(busCompanyDB);
            System.out.print("""
            What do you want to alter on your ticket?
            1 - Legal Name
            2 - Trading Name
            3 - Password
            4 - CNPJ
            5 - Telephone
            6 - Exit
            """);
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    String legalName = collectLegalName(sc);
                    busCompanyService.updateLegalName(busCompanyDB, legalName);
                    break;
                case 2:
                    String tradingName = collectTradingName(sc);
                    busCompanyService.updateTradingName(busCompanyDB, tradingName);
                    break;
                case 3:
                    String newPassword = collectPassword(sc);
                    busCompanyService.updatePassword(busCompanyDB, newPassword);
                    break;
                case 4:
                    String newCNPJ = collectCnpj(sc);
                    busCompanyService.updateCnpj(busCompanyDB, newCNPJ);
                    break;
                case 5:
                    String newTelephone = collectTelephone(sc);
                    busCompanyService.updateTelephone(busCompanyDB, newTelephone);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    exitWhile = true;
                    break;
                default:
                    System.out.println("Type in a valid choice!");
            }
        }
    }

    private static void viewTrips(EntityManager em, BusCompany busCompanyDB, TripService tripService, AddressService addressService) {
        TripDAO tripDAO = new TripDAO(em);
        Trip tripA = new Trip();
        tripA.setBusCompany(busCompanyDB);

        List<Trip> allTrips = tripDAO.searchTrips(tripA);

        if (!allTrips.isEmpty()) {
            String messageToDisplay = "Here is your trip: ";

            if (allTrips.size() > 1)
                messageToDisplay = "Here are your trips: ";

            System.out.println(messageToDisplay);
            allTrips.forEach(t2 -> System.out.println(t2.toString()));

            if (!allTrips.isEmpty()) {
                editTrip(em, tripA, tripDAO, tripService, addressService);
                deleteTrip(em, tripA, tripDAO);
            }
        }else
            System.out.println("There is no trips in the database yet!");
    }

    private static void deleteTrip(EntityManager em, Trip tripA, TripDAO tripDAO) {
        Scanner sc = new Scanner(System.in);
        System.out.println("""
                Do you wish to delete a trip?
                0 - No
                1 - Yes
                """);
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1){
            boolean getBack = true;
            while (getBack) {
                System.out.println("Type in the code of the trip you want to alter: \n(Type 'C' to cancel) ");
                String tripCode = sc.nextLine();
                if (!tripCode.equalsIgnoreCase("C")) {
                    tripA.setCode(tripCode);
                    Trip tripDB = tripDAO.searchSingleTrip(tripA);

                    if (tripDB == null)
                        throw new TripNotFoundException(tripCode);
                    else {
                        em.getTransaction().begin();
                        tripDAO.delete(tripDB);
                        em.getTransaction().commit();
                        getBack = false;
                    }
                }else
                    getBack = false;

            }
        }
    }

    private static void editTrip(EntityManager em, Trip tripA, TripDAO tripDAO, TripService tripService, AddressService addressService) {
        boolean getBack;
        String cityName, stateName = "";
        City cityA = new City();
        City cityDB;
        OsrmClient osrmClient = new OsrmClient();
        Scanner sc = new Scanner(System.in);
        System.out.print("""
            Do you want to alter an info from a trip?
            1 - Yes
            2 - No
            """);
        int choice = sc.nextInt();
        sc.nextLine();
        if (choice == 1) {
            getBack = true;
            Trip tripDB = null;
            while (getBack) {
                System.out.println("Type in the code of the trip you want to alter: ");
                String tripCode = sc.nextLine();
                tripA.setCode(tripCode);
                tripDB = tripService.searchSingleTrip(tripA);

                if (tripDB == null)
                    throw new TripNotFoundException(tripCode);
                else
                    getBack = false;
            }

            boolean exitWhile = false;
            while (!exitWhile) {
                System.out.print("""
                    What do you want to alter on your trip?
                    1- Origin City
                    2- Destination City
                    3- Departure Time
                    4- Price
                    5- Exit
                    """);
                choice = sc.nextInt();
                sc.nextLine();
                switch (choice) {
                    case 1:
                        City origin = collectOriginCity(sc, addressService);
                        tripService.updateOriginCity(tripDB, origin);
                        break;
                    case 2:
                        City destination = collectDestinationCity(sc, addressService);
                        tripService.updateDestinationCity(tripDB, destination);
                        break;
                    case 3:
                        LocalTime departureTime = askDepartureTime(sc);
                        tripService.updateDepartureTime(tripDB, departureTime);
                        break;
                    case 4:
                        double suggested = tripService.suggestPrice(tripDB.getOriginCity(), tripDB.getDestinationCity());
                        BigDecimal price = askPriceOrAcceptSuggestion(sc, suggested);
                        tripService.updateTripPrice(tripDB, price);
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        exitWhile = true;
                        break;
                    default:
                        System.out.println("Type in a valid choice!");
                }
            }
        }
    }

    private static void createTrip(Scanner sc, BusCompany busCompanyDB, TripService tripService, AddressService addressService) {
        City origin = collectOriginCity(sc, addressService);
        City destination = collectDestinationCity(sc, addressService);
        double suggestedPrice = tripService.suggestPrice(origin, destination);
        BigDecimal price = askPriceOrAcceptSuggestion(sc, suggestedPrice);
        LocalTime departureTime = askDepartureTime(sc);

        Trip trip = tripService.createTrip(origin, destination, price, departureTime, busCompanyDB);

        System.out.println("Trip successfully created!");
        System.out.println(trip);
    }

    private static City viewCities(String stateName, Scanner sc, String cityName, AddressService addressService) {
        City cityDB = null;
        cityName = "";
        while (stateName.isEmpty()) {
            System.out.println("From which state do you want to search the cities?");
            List<State> allStates = addressService.searchAllState();
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
                List<City> citiesByState = addressService.searchCitiesByState(stateName);
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
                cityDB = addressService.searchCity(cityA);
            }
        }

        return cityDB;
    }

    private static void signUp(Scanner sc, AuthService authService, BusCompanyService busCompanyService) {
        String password;
        String emailAddress;
        System.out.println("Type your Legal Name:");
        String legalName = sc.nextLine();

        System.out.println("Type your Trading Name:");
        String tradingName = sc.nextLine();

        System.out.println("Type your CNPJ:");
        String cnpj = sc.nextLine();

        System.out.println("Type your Telephone:");
        String telephone = sc.nextLine();

        System.out.print("Email: ");
        emailAddress = sc.nextLine();
        authService.checkEmailAvailable(emailAddress);

        System.out.print("\nPassword: ");
        password = sc.nextLine();

        busCompanyService.signUp(legalName, tradingName, cnpj, telephone, emailAddress, password);
    }

    private static City collectOriginCity(Scanner sc, AddressService addressService) {
        String cityName = "";
        String stateName = "";
        City originCity = null;
        City cityA = new City();

        while (cityName.isEmpty()) {
            System.out.println("Type in the name of the origin city: \nIf you wish to view the cities before type 1");
            cityName = sc.nextLine();

            if (cityName.trim().charAt(0) == '1')
                originCity = viewCities(stateName, sc, cityName, addressService);
            else {
                cityA.setCity(cityName.toLowerCase());
                originCity = addressService.searchCity(cityA);

                if (originCity == null){
                    cityName = "";
                    System.out.println("Type in a valid value!");
                }
            }
        }

        return originCity;
    }

    private static City collectDestinationCity(Scanner sc, AddressService addressService){
        String cityName = "";
        String stateName = "";
        City destinationCity = null;
        City cityA = new City();

        while (cityName.isEmpty()) {
            System.out.println("Type in the name of the destination city: \nIf you wish to view the cities before type 1");
            cityName = sc.nextLine();

            if (cityName.trim().charAt(0) == '1')
                destinationCity = viewCities(stateName, sc, cityName, addressService);
            else {
                cityA.setCity(cityName.toLowerCase());
                destinationCity = addressService.searchCity(cityA);

                if (destinationCity == null){
                    cityName = "";
                    System.out.println("Type in a valid value!");
                }
            }
        }

        return destinationCity;
    }

    public static BigDecimal askPriceOrAcceptSuggestion(Scanner sc, double suggested){
        BigDecimal tripPrice = BigDecimal.ZERO;

        System.out.printf("""
                Suggested price based on distance in KM: R$ %.2f
                Do you wish to keep this price?
                0 - No
                1 - Yes
                """, suggested);
        int priceChoice = sc.nextInt();

        if (priceChoice == 0){
            boolean getBack = false;
            while (!getBack) {
                System.out.println("Type in the price of the trip: ");
                tripPrice = sc.nextBigDecimal();
                sc.nextLine();

                if (tripPrice.intValue() <= 0 || tripPrice.equals(BigDecimal.ZERO))
                    throw new InvalidPriceException(tripPrice);
                else {
                    getBack = true;
                }
            }
        }else
            tripPrice =BigDecimal.valueOf(suggested);

        return tripPrice;
    }

    public static LocalTime askDepartureTime(Scanner sc){
        LocalTime departureTime = LocalTime.now();

        boolean getBack = false;
        while (!getBack) {
            System.out.println("When will be the departure time? (Use 24-hour format, example: 14:30)");
            String departureTimeString = sc.nextLine();

            try {
                departureTime = LocalTime.parse(departureTimeString);
                getBack = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format! Please enter the time precisely as HH:mm.");
            }
        }
        return departureTime;

    }

    public static String collectLegalName(Scanner sc){
        String legalName;
        do {
            System.out.print("Type in your Legal Name: ");
            legalName = sc.nextLine().trim();

            if (legalName.isEmpty()) {
                System.out.println("Legal name cannot be empty. Please try again.");
            }
        } while (legalName.isEmpty());

        return legalName;
    }

    public static String collectTradingName(Scanner sc){
        String tradingName;
        do {
            System.out.print("Type in your Trading Name: ");
            tradingName = sc.nextLine().trim();

            if (tradingName.isEmpty()) {
                System.out.println("Trading name cannot be empty. Please try again.");
            }
        } while (tradingName.isEmpty());

        return tradingName;
    }

    public static String collectPassword(Scanner sc){
        String newPassword;
        do {
            System.out.print("Type in your new Password: ");
            newPassword = sc.nextLine().trim();

            if (newPassword.isEmpty()) {
                System.out.println("A password cannot be empty. Please try again.");
            }
        } while (newPassword.isEmpty());

        return newPassword;
    }

    public static String collectCnpj(Scanner sc){
        String newCNPJ;
        do {
            System.out.print("Type in your new CNPJ: ");
            newCNPJ = sc.nextLine().trim();

            if (newCNPJ.isEmpty()) {
                System.out.println("CNPJ cannot be empty. Please try again.");
            }
        } while (newCNPJ.isEmpty());

        return newCNPJ;
    }

    public static String collectTelephone(Scanner sc){
        String newTelephone;
        do {
            System.out.print("Type in your new Telephone: ");
            newTelephone = sc.nextLine().trim();

            if (newTelephone.isEmpty()) {
                System.out.println("Telephone cannot be empty. Please try again.");
            }
        } while (newTelephone.isEmpty());

        return newTelephone;
    }
}
