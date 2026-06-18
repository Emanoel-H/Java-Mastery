package br.com.javamastery.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class OsrmResponse {
    private String code;
    private List<Route> routes;

    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        private double distance;
        private double duration;
    }

    public double getDistanceInKM(){
        if(routes==null || routes.isEmpty())
            throw new RuntimeException("No routes found in OSRM Response");

        return routes.getFirst().getDistance() / 1000;
    }

}
