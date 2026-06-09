package br.com.javamastery.models;

import br.com.javamastery.util.ValidationUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "bus_ticket")
@Getter
@Setter
public class BusTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "traveler_id")
    private Traveler traveler;
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal ticketPrice;
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;
    @Column(name = "code", length = 10, unique = true)
    private String code;
    @Column(name = "sale_date")
    private LocalDate saleDate;
    @Enumerated(EnumType.STRING)
    private Category category;
    private LocalDateTime cancelDate;
    private boolean canceled;
    private LocalDateTime editedAt;
    private LocalDate departureDate;

    @PrePersist
    public void prePersistOperations(){
        if (this.code == null){
            this.code = ValidationUtils.generateRamdomCode(10);
        }

        if (this.saleDate == null){
            this.saleDate = LocalDate.now();
        }

        if (this.category == null && this.trip != null
                && this.trip.getOriginCity() != null && this.trip.getDestinationCity() != null){
            if (this.trip.getOriginCity().getState().getUf().equals(this.trip.getDestinationCity().getState().getUf()))
                this.category = Category.INTERCITY;
            else
                this.category = Category.INTERSTATE;
        }

        if (this.trip != null){
            this.ticketPrice = this.trip.getPrice();
        }

        this.editedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdateOperations(){
        this.editedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return String.format("Traveler: %s \nAge: %d \nCPF: %s\nBirth Date: %s" +
                "\nOrigin City: %s \nDestination City: %s \nTicket Price %.2f \nTicket Code: %s",
                this.traveler.getName(),
                this.traveler.getAge(),
                this.traveler.getCpf(),
                parser.format(this.traveler.getBirthDate()),
                this.trip.getOriginCity().getCity(),
                this.trip.getDestinationCity().getCity(),
                this.ticketPrice.doubleValue(),
                this.code);
    }
}
