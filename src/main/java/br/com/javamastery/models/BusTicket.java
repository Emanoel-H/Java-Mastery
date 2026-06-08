package br.com.javamastery.models;

import br.com.javamastery.util.ValidationUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.transaction.Transactional;
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
    private boolean isCancelable;
    private LocalDateTime cancelDate;
    private LocalDateTime editedAt;

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
                this.originCity,
                this.destinationCity,
                this.ticketPrice.doubleValue(),
                this.code);
    }
}
