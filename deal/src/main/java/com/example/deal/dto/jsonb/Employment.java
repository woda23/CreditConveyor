package com.example.deal.dto.jsonb;

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
public class Employment {
    public String status;
    public String employer_inn;
    public BigDecimal salary;
    public String position;
    public Integer work_experience_total;
    public Integer work_experience_current;
}
