package br.com.javamastery.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity
@Table(name = "traveler")
@Getter
@Setter
public class Traveler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    private String cpf;
    @Formula("extract(year from age(current_date, birth_date))")
    private int age;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "email_id")
    private Email email;
    private String telephone;
    private BigDecimal creditsBalance;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;

    public Traveler() {
        this.email = new Email();
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        setAge(Period.between(this.birthDate, LocalDate.now()).getYears());
    }

    public void setCpf(String cpf){
        if(cpf != null &&  !cpf.isEmpty()) {
            if (cpf.replaceAll("\\D", "").trim().length() == 11)
                this.cpf = cpf.replaceAll("\\D", "").trim();
            else
                throw new IllegalArgumentException("CPF format invalid");
        }else
            throw new NullPointerException("CPF cannot be empty.");
    }

    public String getCpf() {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private void setAge(int age){
        this.age = age;
    }

    public String getTelephone() {
        if(telephone.length()==11)
            return telephone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        else
            return telephone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
    }

    public void setTelephone(String telephone){
        if(telephone != null &&  !telephone.isEmpty()) {
            int length = telephone.replaceAll("\\D", "").trim().length();
            if (length == 11 || length == 10)
                this.telephone = telephone.replaceAll("\\D", "").trim();
            else
                throw new IllegalArgumentException("Telephone length must be 11 or 10 digits");
        }else
            throw new NullPointerException("Telephone cannot be empty.");
    }

    public void setCreditsBalance(BigDecimal creditsBalance) {
        this.creditsBalance = this.creditsBalance.add(creditsBalance);
    }

    @PrePersist
    public void prePersistOperations(){
        this.createdAt = LocalDateTime.now();
        this.editedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdateOperations(){
        this.editedAt = LocalDateTime.now();
    }
}
