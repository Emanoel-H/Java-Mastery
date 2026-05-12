package br.com.javamastery.models;

import javax.persistence.*;

@Entity
@Table(name = "cities")
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

    public Long getIBGE_code() {
        return IBGE_code;
    }

    public void setIBGE_code(Long IBGE_code) {
        this.IBGE_code = IBGE_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getDdd() {
        return ddd;
    }

    public void setDdd(int ddd) {
        this.ddd = ddd;
    }

    @Override
    public String toString() {
        return "%s - %s".formatted(this.city, this.state.getUf());
    }
}
