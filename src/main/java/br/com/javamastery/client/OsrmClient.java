package br.com.javamastery.client;

import br.com.javamastery.client.dto.OsrmResponse;
import br.com.javamastery.models.City;
import br.com.javamastery.models.Trip;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OsrmClient {
    public static final String BASE_URL = "http://router.project-osrm.org/route/v1/driving/";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OsrmClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public double getRealDistanceKM(City origin, City destination) {
        String url = BASE_URL + origin.getLongitude() + "," +
                origin.getLatitude() + ";" +
                destination.getLongitude() + "," +
                destination.getLatitude() +
                "?overview=false";

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new RuntimeException("OSRM API Error : HTTP : " + response.statusCode());

            OsrmResponse osrmResponse = objectMapper.readValue(response.body(), OsrmResponse.class);

            return osrmResponse.getDistanceInKM();
        }catch(Exception e) {
            System.out.println("Could not fetch real distance, falling back to Haversine.");
            return Trip.calculateHaversine(origin, destination);
        }
    }
}
