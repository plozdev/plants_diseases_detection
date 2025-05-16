package com.example.plantdiseasedetection.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;

public class ScanResult implements Serializable {
    private String id;
    private String userId;
    private Plant plant;
    private String diseaseDetected;
    private String treatmentRecommendations;
    private String preventativeMeasures;
    private Timestamp timestamp;

    public ScanResult() {
        // Required empty constructor for Firestore
    }

    public ScanResult(String id, String userId, Plant plant, String diseaseDetected,
                      String treatmentRecommendations, String preventativeMeasures, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.plant = plant;
        this.diseaseDetected = diseaseDetected;
        this.treatmentRecommendations = treatmentRecommendations;
        this.preventativeMeasures = preventativeMeasures;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public String getDiseaseDetected() {
        return diseaseDetected;
    }

    public void setDiseaseDetected(String diseaseDetected) {
        this.diseaseDetected = diseaseDetected;
    }

    public String getTreatmentRecommendations() {
        return treatmentRecommendations;
    }

    public void setTreatmentRecommendations(String treatmentRecommendations) {
        this.treatmentRecommendations = treatmentRecommendations;
    }

    public String getPreventativeMeasures() {
        return preventativeMeasures;
    }

    public void setPreventativeMeasures(String preventativeMeasures) {
        this.preventativeMeasures = preventativeMeasures;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
