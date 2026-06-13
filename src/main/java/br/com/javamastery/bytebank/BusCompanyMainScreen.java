package br.com.javamastery.bytebank;

import br.com.javamastery.dao.*;
import br.com.javamastery.models.*;
import br.com.javamastery.util.JPAUtils;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class BusCompanyMainScreen {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        JPAUtils jpaUtils = new JPAUtils();
        EntityManager em = jpaUtils.getEntityManager();
        EmailDAO emailDAO = new EmailDAO(em);
        BusCompanyDAO busCompanyDAO = new BusCompanyDAO(em);
        boolean exitSystem = false;
        boolean emailFilled = false;
        boolean accessGranted = false;
        String emailAddress, password;
        Email emailA = new Email();
        BusCompany busCompanyA;
        BusCompany busCompanyDB;

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
                        signUp(sc, busCompanyDAO);
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
                            busCompanyDB = busCompanyDAO.searchCompany(busCompanyA);
                            createTrip(em, sc, busCompanyDB);
                            em.getTransaction().commit();
                            break;
                        case 2:
                            busCompanyA = new BusCompany();
                            busCompanyA.setEmail(emailA);
                            busCompanyDB = busCompanyDAO.searchCompany(busCompanyA);
                            viewTrips(em, busCompanyDB);
                            break;
                        case 3:
                            busCompanyA = new BusCompany();
                            busCompanyA.setEmail(emailA);
                            busCompanyDB = busCompanyDAO.searchCompany(busCompanyA);
                            boolean exitWhile = false;
                            while (!exitWhile){
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
                                        String legalName;
                                        do {
                                            System.out.print("Type in your Legal Name: ");
                                            legalName = sc.nextLine().trim();

                                            if (legalName.isEmpty()) {
                                                System.out.println("Legal name cannot be empty. Please try again.");
                                            }
                                        } while (legalName.isEmpty());

                                        busCompanyDB.setLegalName(legalName);
                                        busCompanyDAO.updateCompany(busCompanyDB);
                                        em.getTransaction().commit();
                                        break;
                                    case 2:
                                        String tradingName;
                                        do {
                                            System.out.print("Type in your Trading Name: ");
                                            tradingName = sc.nextLine().trim();

                                            if (tradingName.isEmpty()) {
                                                System.out.println("Trading name cannot be empty. Please try again.");
                                            }
                                        } while (tradingName.isEmpty());

                                        busCompanyDB.setTradingName(tradingName);
                                        busCompanyDAO.updateCompany(busCompanyDB);
                                        em.getTransaction().commit();
                                        break;
                                    case 3:
                                        String newPassword;
                                        do {
                                            System.out.print("Type in your new Password: ");
                                            newPassword = sc.nextLine().trim();

                                            if (newPassword.isEmpty()) {
                                                System.out.println("A password cannot be empty. Please try again.");
                                            }
                                        } while (newPassword.isEmpty());

                                        busCompanyDB.getEmail().setPassword(newPassword);
                                        busCompanyDAO.updateCompany(busCompanyDB);
                                        em.getTransaction().commit();
                                        break;
                                    case 4:
                                        String newCNPJ;
                                        do {
                                            System.out.print("Type in your new CNPJ: ");
                                            newCNPJ = sc.nextLine().trim();

                                            if (newCNPJ.isEmpty()) {
                                                System.out.println("CNPJ cannot be empty. Please try again.");
                                            }
                                        } while (newCNPJ.isEmpty());

                                        busCompanyDB.setCnpj(newCNPJ);
                                        busCompanyDAO.updateCompany(busCompanyDB);
                                        em.getTransaction().commit();
                                        break;
                                    case 5:
                                        String newTelephone;
                                        do {
                                            System.out.print("Type in your new Telephone: ");
                                            newTelephone = sc.nextLine().trim();

                                            if (newTelephone.isEmpty()) {
                                                System.out.println("Telephone cannot be empty. Please try again.");
                                            }
                                        } while (newTelephone.isEmpty());

                                        busCompanyDB.setTelephone(newTelephone);
                                        busCompanyDAO.updateCompany(busCompanyDB);
                                        em.getTransaction().commit();
                                        break;
                                    case 6:
                                        System.out.println("Exiting...");
                                        exitWhile = true;
                                        break;
                                    default:
                                        System.out.println("Type in a valid choice!");
                                }
                            }
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

    private static void viewTrips(EntityManager em, BusCompany busCompanyDB) {
        TripDAO tripDAO = new TripDAO(em);
        Trip tripA = new Trip();
        tripA.setBusCompany(busCompanyDB);

        List<Trip> allTrips = tripDAO.searchTrips(tripA);
        String messageToDisplay = "Here is your trip: ";

        if (allTrips.size() > 1)
            messageToDisplay = "Here are your trips: ";

        System.out.println(messageToDisplay);
        allTrips.forEach(t2 -> System.out.println(t2.toString()));

        if (!allTrips.isEmpty()) {
            editTrip(em, tripA, tripDAO);
            deleteTrip(em, tripA, tripDAO);
        }
    }

    private static void deleteTrip(EntityManager em, Trip tripA, TripDAO tripDAO) {
        Scanner sc = new Scanner(System.in);
        System.out.println("""
                Do you wish to delete a trip?
                0 - No
                1 - Yes
                """);
        int choice = sc.nextInt();

        if (choice == 1){
            boolean getBack = true;
            while (getBack) {
                System.out.println("Type in the code of the trip you want to alter: \n(Type 'C' to cancel) ");
                String tripCode = sc.nextLine();
                if (!tripCode.equalsIgnoreCase("C")) {
                    tripA.setCode(tripCode);
                    Trip tripDB = tripDAO.searchSingleTrip(tripA);

                    if (tripDB == null)
                        throw new IllegalArgumentException("Type in a valid code!");
                    else {
                        tripDAO.delete(tripDB);
                        em.getTransaction().commit();
                        getBack = false;
                    }
                }else
                    getBack = false;

            }
        }
    }

    private static void editTrip(EntityManager em, Trip tripA, TripDAO tripDAO) {
        boolean getBack;
        String cityName, stateName = "";
        AddressDAO addressDAO = new AddressDAO(em);
        City cityA = new City();
        City cityDB;
        Scanner sc = new Scanner(System.in);
        System.out.print("""
            Do you want to alter an info from your bus ticket?
            1 - Yes
            2 - No
            """);
        int choice = sc.nextInt();
        sc.nextLine();
        if (choice == 1) {
            getBack = true;
            Trip tripDB = null;
            while (getBack) {
                System.out.println("Type in the code of the ticket you want to alter: ");
                String tripCode = sc.nextLine();
                tripA.setCode(tripCode);
                tripDB = tripDAO.searchSingleTrip(tripA);

                if (tripDB == null)
                    throw new IllegalArgumentException("Type in a valid code!");
                else
                    getBack = false;
            }

            boolean exitWhile = false;
            while (!exitWhile) {
                System.out.print("""
                    What do you want to alter on your ticket?
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
                        cityName = "";
                        while (cityName.isEmpty()) {
                            System.out.println("Type in the name of the origin city: \nIf you wish to view the cities before type 1");
                            cityName = sc.nextLine();

                            if (cityName.trim().charAt(0) == '1')
                                tripDB.setOriginCity(viewCities(stateName, addressDAO, sc, cityName));
                            else {
                                cityA.setCity(cityName.toLowerCase());
                                cityDB = addressDAO.searchCity(cityA);

                                if (cityDB != null)
                                    tripDB.setOriginCity(cityDB);
                                else {
                                    cityName = "";
                                    throw new NullPointerException("Type in a valid value!");
                                }
                            }
                        }
                        if (tripDB.getOriginCity() != null) {
                            tripDAO.updateTrip(tripDB);
                            em.getTransaction().commit();
                        }
                        break;
                    case 2:
                        cityName = "";
                        while (cityName.isEmpty()) {
                            System.out.println("Type in the name of the destination city: \nIf you wish to view the cities before type 1");
                            cityName = sc.nextLine();

                            if (cityName.trim().charAt(0) == '1')
                                tripDB.setDestinationCity(viewCities(stateName, addressDAO, sc, cityName));
                            else {
                                cityA.setCity(cityName.toLowerCase());
                                cityDB = addressDAO.searchCity(cityA);

                                if (cityDB != null)
                                    tripDB.setDestinationCity(cityDB);
                                else {
                                    cityName = "";
                                    throw new NullPointerException("Type in a valid value!");
                                }
                            }
                        }

                        if (tripDB.getDestinationCity() != null) {
                            tripDAO.updateTrip(tripDB);
                            em.getTransaction().commit();
                        }
                        break;
                    case 3:
                        boolean getBackTime = false;
                        while (!getBackTime) {
                            System.out.println("When will be the departure time? (Use 24-hour format, example: 14:30)");
                            String departureTimeString = sc.nextLine();

                            try {
                                LocalTime departureTime = LocalTime.parse(departureTimeString);
                                tripDB.setDepartureTime(departureTime);
                                tripDAO.updateTrip(tripDB);
                                em.getTransaction().commit();
                                getBackTime = true;
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid format! Please enter the time precisely as HH:mm.");
                            }
                        }
                        break;
                    case 4:
                        boolean getBackPrice = false;
                        while (!getBackPrice) {
                            System.out.println("Type in the price of the trip: ");
                            BigDecimal tripPrice = sc.nextBigDecimal();

                            if (tripPrice.intValue() <= 0 || tripPrice.equals(BigDecimal.ZERO))
                                throw new RuntimeException("The Trip price must be a value higher than 0");
                            else {
                                tripDB.setPrice(tripPrice);
                                tripDAO.updateTrip(tripDB);
                                em.getTransaction().commit();
                                getBackPrice = true;
                            }
                        }
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

    private static void createTrip(EntityManager em, Scanner sc, BusCompany busCompanyDB) {
        Trip trip = new Trip();
        AddressDAO addressDAO = new AddressDAO(em);
        TripDAO tripDAO = new TripDAO(em);
        String cityName, stateName = "";
        BigDecimal tripPrice;
        City cityA = new City();
        City cityDB;
        boolean getBack;

        cityName = "";
        while (cityName.isEmpty()) {
            System.out.println("Type in the name of the origin city: \nIf you wish to view the cities before type 1");
            cityName = sc.nextLine();

            if (cityName.trim().charAt(0) == '1')
                trip.setOriginCity(viewCities(stateName, addressDAO, sc, cityName));
            else {
                cityA.setCity(cityName.toLowerCase());
                cityDB = addressDAO.searchCity(cityA);

                if (cityDB != null)
                    trip.setOriginCity(cityDB);
                else{
                    cityName = "";
                    throw new NullPointerException("Type in a valid value!");
                }
            }
        }

        cityName = "";
        while (cityName.isEmpty()) {
            System.out.println("Type in the name of the destination city: \nIf you wish to view the cities before type 1");
            cityName = sc.nextLine();

            if (cityName.trim().charAt(0) == '1')
                trip.setDestinationCity(viewCities(stateName, addressDAO, sc, cityName));
            else {
                cityA.setCity(cityName.toLowerCase());
                cityDB = addressDAO.searchCity(cityA);

                if (cityDB != null)
                    trip.setDestinationCity(cityDB);
                else {
                    cityName = "";
                    throw new NullPointerException("Type in a valid value!");
                }
            }
        }

        double suggestedPrice = Trip.calculateHaversine(trip.getOriginCity(), trip.getDestinationCity()) * 0.2;

        System.out.printf("""
                Suggested price based on distance in KM: R$ %.2f
                Do you wish to keep this price?
                0 - No
                1 - Yes
                """, suggestedPrice);
        int priceChoice = sc.nextInt();

        if (priceChoice == 0){
            getBack = false;
            while (!getBack) {
                System.out.println("Type in the price of the trip: ");
                tripPrice = sc.nextBigDecimal();

                if (tripPrice.intValue() <= 0 || tripPrice.equals(BigDecimal.ZERO))
                    throw new RuntimeException("The Trip price must be a value higher than 0");
                else {
                    trip.setPrice(tripPrice);
                    getBack = true;
                }
            }
        }else
            trip.setPrice(BigDecimal.valueOf(suggestedPrice));

        getBack = false;
        while (!getBack) {
            System.out.println("When will be the departure time? (Use 24-hour format, example: 14:30)");
            String departureTimeString = sc.nextLine();

            try {
                LocalTime departureTime = LocalTime.parse(departureTimeString);
                trip.setDepartureTime(departureTime);
                getBack = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format! Please enter the time precisely as HH:mm.");
            }
        }

        trip.setBusCompany(busCompanyDB);

        tripDAO.save(trip);
    }

    private static City viewCities(String stateName, AddressDAO addressDAO, Scanner sc, String cityName) {
        City cityDB = null;
        while (stateName.isEmpty()) {
            System.out.println("From which state do you want to search the cities?");
            List<State> allStates = addressDAO.searchAllState();
            allStates.forEach(s -> System.out.println(s.toString()));
            stateName = sc.nextLine();

            if (stateName.trim().length() > 2) {
                String finalStateName = stateName;
                if (allStates.stream().noneMatch(s -> s.getName().trim().equalsIgnoreCase(finalStateName.trim()))) {
                    stateName = "";
                    throw new IllegalArgumentException("Type in a valid value!");
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
                    throw new IllegalArgumentException("Type in a valid value!");
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
                System.out.println("Type in the city your selected city:");
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

    private static void signUp(Scanner sc, BusCompanyDAO busCompanyDAO) {
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

        System.out.print("\nPassword: ");
        password = sc.nextLine();

        BusCompany busCompany = new BusCompany();
        busCompany.setLegalName(legalName);
        busCompany.setTradingName(tradingName);
        busCompany.setCnpj(cnpj);
        busCompany.setTelephone(telephone);
        busCompany.getEmail().setEmail(emailAddress);
        busCompany.getEmail().setPassword(password);

        busCompanyDAO.save(busCompany);
    }
}
