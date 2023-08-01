package com.example.creditconveyor.service.abstraction;

import com.example.creditconveyor.dto.ScoringDataDTO;
import com.example.creditconveyor.wrapper.ScoringResult;

public interface LoanScoringService {
    ScoringResult calculateScore(ScoringDataDTO scoringData);
}
