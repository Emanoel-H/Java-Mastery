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

    private static String telephoneFormatter(String telephone){
        if (telephone.length() == 11)
            return telephone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        else if (telephone.length() == 10)
            return telephone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        else
            return telephone;
    }

    private static String cnpjFormatter(String cnpj){
        if (cnpj.length() == 14)
            return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        else
            return cnpj;
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

    @Override
    public String toString() {
        return """
                Legal Name: %s
                Trading Name: %s
                CNPJ: %s
                Telephone: %s
                """.formatted(this.legalName, this.tradingName, cnpjFormatter(this.cnpj), telephoneFormatter(this.telephone));
    }
}
