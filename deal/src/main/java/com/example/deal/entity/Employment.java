package com.example.deal.entity;

import com.example.deal.entity.jsonb.EmploymentData;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "employment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employment_id")
    public Long employmentId;

    @JdbcTypeCode(SqlTypes.JSON)
    public EmploymentData employmentData;

    public Employment(EmploymentData employmentData) {
        this.employmentData = employmentData;
    }
}
