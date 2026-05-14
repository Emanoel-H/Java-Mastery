package br.com.javamastery.models;

import br.com.javamastery.dao.AddressDAO;
import br.com.javamastery.util.JPAUtils;
import br.com.javamastery.util.ValidationUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "bus_ticket")
public class BusTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "traveler_id")
    private Person traveler;
    @Column(name = "price")
    private BigDecimal ticketPrice;
    @Column(name = "origin_city")
    private String originCity;
    @Column(name = "destination_city")
    private String destinationCity;
    @Column(name = "code", length = 10, unique = true)
    private String code;
    @Column(name = "sale_date")
    private LocalDate saleDate;
    @Enumerated(EnumType.STRING)
    private Category category;

    public BusTicket() {
        this.traveler = new Person();
    }

    public Person getTraveler() {
        return traveler;
    }

    public void setTraveler(Person traveler) {
        this.traveler = traveler;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @PrePersist
    public void prePersistOperations(){
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (this.code == null){
            this.code = ValidationUtils.generateRamdomCode(10);
        }

        if (this.saleDate == null){
            this.saleDate = LocalDate.now();
        }

        if (this.category == null){
            this.category = Category.INTERCITY;
        }
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
                this.originCity,
                this.destinationCity,
                this.ticketPrice.doubleValue(),
                this.code);
    }
}
