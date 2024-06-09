package com.design_patterns.strategy_pattern;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

public interface AlertStrategy {

    public boolean checkAlert(Patient patient, List<PatientRecord> records);
}
