package com.example.healthcare.controller;

import com.example.healthcare.entity.Patient;
import com.example.healthcare.service.PatientService;
import com.example.healthcare.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    public Patient addPatient(@RequestBody Patient patient) {
        return patientService.addPatient(patient);
    }

    @DeleteMapping("/{id}")
    public void removePatient(@PathVariable Long id) {
        patientService.removePatient(id);
    }

    @GetMapping("/suggest-doctor/{id}")
    public Object suggestDoctor(@PathVariable Long id) {
        Patient patient = patientService.findPatientById(id);
        if (patient == null) {
            return "Patient not found";
        }

        String speciality = getSpecialityBySymptom(patient.getSymptom());
        List<Doctor> doctors = doctorService.findDoctorsByCityAndSpeciality(patient.getCity(), speciality);

        if (doctors.isEmpty()) {
            if (!patient.getCity().equalsIgnoreCase("Delhi") &&
                !patient.getCity().equalsIgnoreCase("Noida") &&
                !patient.getCity().equalsIgnoreCase("Faridabad")) {
                return "We are still waiting to expand to your location";
            }
            return "There isn’t any doctor present at your location for your symptom";
        }

        return doctors;
    }

    private String getSpecialityBySymptom(String symptom) {
        switch (symptom.toLowerCase()) {
            case "arthritis":
            case "back pain":
            case "tissue injuries":
                return "Orthopaedic";
            case "dysmenorrhea":
                return "Gynecology";
            case "skin infection":
            case "skin burn":
                return "Dermatology";
            case "ear pain":
                return "ENT";
            default:
                return "";
        }
    }
}
