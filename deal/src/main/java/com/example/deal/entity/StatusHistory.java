package com.example.deal.entity;

import com.example.deal.dto.enums.ApplicationStatus;
import com.example.deal.dto.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {
    public ApplicationStatus status;
    public Timestamp timestamp;
    public ChangeType change_type;
}
