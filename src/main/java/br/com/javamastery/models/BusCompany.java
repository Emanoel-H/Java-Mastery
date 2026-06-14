package br.com.javamastery.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bus_company")
@Getter
@Setter
public class BusCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String legalName;
    private String tradingName;
    private String cnpj;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "email_id")
    private Email email;
    private String telephone;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;

    public BusCompany() {
        this.email = new Email();
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

    public String getTelephone() {
        if (telephone.length() == 11)
            return telephone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        else
            return telephone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
    }

    public void setTelephone(String telephone) {
        if (telephone != null &&  !telephone.isEmpty()) {
            int length = telephone.replaceAll("\\D", "").trim().length();
            if (length == 11 || length == 10)
                this.telephone = telephone.replaceAll("\\D", "").trim();
            else
                throw new IllegalArgumentException("Telephone length must be 11 or 10 digits");
        }else
            throw new NullPointerException("Telephone cannot be empty.");
    }

    public void setCnpj(String cnpj) {
        if (cnpj != null &&  !cnpj.isEmpty()) {
            if (cnpj.replaceAll("\\D", "").trim().length() == 14)
                this.cnpj = cnpj.replaceAll("\\D", "").trim();
            else
                throw new IllegalArgumentException("CNPJ format invalid");
        }else
            throw new NullPointerException("Cnpj cannot be empty.");
    }

    public String getCnpj() {
        return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    @Override
    public String toString() {
        return """
                Legal Name: %s
                Trading Name: %s
                CNPJ: %s
                Telephone: %s
                """.formatted(this.legalName, this.tradingName, getCnpj(), getTelephone());
    }
}
