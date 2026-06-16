package br.com.javamastery.models;

import br.com.javamastery.util.ValidationUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", length = 10, unique = true)
    private String code;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "originCity_id")
    private City originCity;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "destinationCity_id")
    private City destinationCity;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "company_id")
    private BusCompany busCompany;
    private LocalTime departureTime;
    private boolean active = true;
    private double distanceKM;
    @Enumerated(EnumType.STRING)
    private Category category;

    public Trip() {
        this.originCity = new City();
        this.destinationCity = new City();
        this.busCompany = new BusCompany();
        this.price = BigDecimal.ZERO;
    }

    @PrePersist
    public void prePersistOperations(){
        if (this.distanceKM == 0 && this.originCity != null && this.destinationCity != null)
            this.distanceKM = calculateHaversine(this.originCity, this.destinationCity);

        if (this.code == null)
            this.code = ValidationUtils.generateRamdomCode(10);
    }

    public static double calculateHaversine(City originCity, City destinationCity) {
        final int earthRadius = 6371;

        double latDistance = Math.toRadians(destinationCity.getLatitude() - originCity.getLatitude());
        double lonDistance = Math.toRadians(destinationCity.getLongitude() - originCity.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(originCity.getLatitude())) * Math.cos(Math.toRadians(destinationCity.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    public String distanceToHours(){
        final double INTERCITY_BUS_SPEED = 57.5;

        double calculusTime = this.distanceKM / INTERCITY_BUS_SPEED;

        int hours = (int) calculusTime;

        double remainingTime = calculusTime - hours;

        int minutes = (int) Math.round(remainingTime * 60);

        return String.format("%d hours and %d minutes", hours, minutes);
    }

    public void setOriginCity(City originCity) {
        this.originCity = originCity;

        if (destinationCity != null)
            this.distanceKM = calculateHaversine(this.originCity, this.destinationCity);
    }

    public void setDestinationCity(City destinationCity) {
        this.destinationCity = destinationCity;

        if (originCity != null)
            this.distanceKM = calculateHaversine(this.originCity, this.destinationCity);
    }

    @Override
    public String toString() {
        return """
                Trip Code: %s
                Origin City: %s
                Destination City: %s
                Distance: %.2f
                Travel time: %s
                Price: %.2f
                """.formatted(this.code,
                this.originCity.getCity(),
                this.originCity.getCity(),
                this.distanceKM,
                distanceToHours(),
                this.price);
    }
}
