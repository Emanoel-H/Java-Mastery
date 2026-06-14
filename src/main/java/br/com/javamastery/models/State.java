package br.com.javamastery.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "states")
@Getter
@Setter
public class State {
    @Id
    @Column(name = "state_code")
    private Long code;
    private String uf;
    private String name;
    private String region;

    @Override
    public String toString() {
        return "%s || %s".formatted(this.uf, this.name);
    }
}
