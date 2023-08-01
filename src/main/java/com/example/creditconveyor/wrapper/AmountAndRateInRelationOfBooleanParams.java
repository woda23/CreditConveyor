package com.example.creditconveyor.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmountAndRateInRelationOfBooleanParams {
    private BigDecimal amount;
    private BigDecimal rate;
}
