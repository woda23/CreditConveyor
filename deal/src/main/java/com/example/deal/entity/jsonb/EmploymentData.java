package com.example.deal.entity.jsonb;

import com.example.deal.dto.enums.EmploymentStatus;
import com.example.deal.dto.enums.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentData {
    public EmploymentStatus status;
    public String employer_inn;
    public BigDecimal salary;
    public Position position;
    public Integer work_experience_total;
    public Integer work_experience_current;
}
