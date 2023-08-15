package com.example.deal.dto.entity;

import com.example.deal.dto.PaymentScheduleElement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "credit")
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credit_id")
    private Long id;

    private BigDecimal amount;

    private Integer term;

    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    private BigDecimal rate;

    private BigDecimal psk;

    @Column(name = "payment_schedule")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<PaymentScheduleElement> paymentSchedule;

    @Column(name = "insurance_enable")
    private Boolean insuranceEnable;

    @Column(name = "salary_client")
    private Boolean salaryClient;

    @Column(name = "credit_status")
    private String creditStatus;
}
