package br.com.javamastery.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.transaction.Transactional;
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
    private Email email;
    private String telephone;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;

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
        if(telephone != null &&  !telephone.isEmpty()) {
            int length = telephone.replaceAll("\\D", "").trim().length();
            if (length == 11 || length == 10)
                this.telephone = telephone.replaceAll("\\D", "").trim();
            else
                throw new IllegalArgumentException("Telephone length must be 11 or 10 digits");
        }else
            throw new IllegalArgumentException("At least one filter must be informed.");
    }

    public void setCnpj(String cnpj) {
        if (cnpj.replaceAll("\\D",  "").trim().length() == 14)
            this.cnpj = cnpj.replaceAll("\\D",  "").trim();
        else
            throw new IllegalArgumentException("CNPJ format invalid");
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
