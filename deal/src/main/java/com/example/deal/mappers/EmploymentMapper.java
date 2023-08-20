package com.example.deal.mappers;

import com.example.deal.dto.EmploymentDTO;
import com.example.deal.entity.jsonb.EmploymentData;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class EmploymentMapper {
    public EmploymentData mapToEmploymentData(EmploymentDTO request) {
        return EmploymentData.builder()
                .status(request.getEmploymentStatus())
                .employer_inn(request.getEmployerINN())
                .salary(request.getSalary())
                .work_experience_total(request.getWorkExperienceTotal())
                .work_experience_current(request.getWorkExperienceCurrent())
                .position(request.getPosition())
                .build();
    }
}
