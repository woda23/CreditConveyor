package com.example.conveyor.service.abstraction;

import com.example.conveyor.dto.ScoringDataDTO;
import com.example.conveyor.wrapper.ScoringResult;

public interface LoanScoringService {
    ScoringResult calculateScore(ScoringDataDTO scoringData);
}
