package com.example.deal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "client")
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "email")
    private String email;
    @Column(name = "gender")
    private String gender;
    @Column(name = "marital_status")
    private String maritalStatus;
    @Column(name = "dependent_amount")
    private Integer dependentAmount;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @OneToOne
    @JoinColumn(name = "passport_id", referencedColumnName = "passport_id")
    private Passport passport;
    @Column(name = "employment_id")
    private Long employmentId;
    @Column(name = "account")
    private String account;
}
