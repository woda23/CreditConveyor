package com.example.deal.entity;

import com.example.deal.entity.jsonb.StatusHistory;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.ArrayList;

@Entity
@Table(name = "application")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private Client client;
    @OneToOne
    @JoinColumn(name = "credit_id", referencedColumnName = "credit_id")
    private Credit credit;
    @Column(name = "status")
    private String status;
    @Column(name = "creation_data")
    private Timestamp creationData;
    @Column(name = "applied_offer")
    @JdbcTypeCode(SqlTypes.JSON)
    private String appliedOffer;
    @Column(name = "sign_date")
    private Timestamp signDate;
    @Column(name = "sec_code")
    private Integer secCode;
    @Column(name = "status_history")
    @JdbcTypeCode(SqlTypes.JSON)
    private ArrayList<StatusHistory> statusHistory;
}
