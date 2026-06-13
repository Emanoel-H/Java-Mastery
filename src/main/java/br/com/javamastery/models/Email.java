package br.com.javamastery.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Entity
@Table(name = "email")
@Getter
@Setter
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(length = 16, nullable = false)
    private String password;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public void setEmail(String email) {
        if (Pattern.compile(EMAIL_REGEX).matcher(email).matches())
            this.email = email;
        else
            throw new IllegalArgumentException("Invalid email format.");
    }

    public void setPassword(String password) {
        if (password.length() <= 16)
            this.password = password;
        else
            throw new IllegalArgumentException("Invalid password format. \nYour password must contain only 16 characters");
    }
}
