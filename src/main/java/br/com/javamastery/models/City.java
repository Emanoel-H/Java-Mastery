package br.com.javamastery.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "cities")
@Getter
@Setter
public class City {
    @Id
    private Long IBGE_code;
    private String city;
    private double latitude;
    private double longitude;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "state_code")
    private State state;
    private int ddd;

    public City() {
        this.state = new State();
    }

    @Override
    public String toString() {
        return "%s - %s".formatted(this.city, this.state.getUf());
    }
}
