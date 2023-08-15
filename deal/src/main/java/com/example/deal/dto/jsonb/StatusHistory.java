package com.example.deal.dto.jsonb;

import com.example.deal.dto.enums.ApplicationStatus;
import com.example.deal.dto.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {
    public ApplicationStatus status;
    public String timestamp;
    public ChangeType change_type;
}
