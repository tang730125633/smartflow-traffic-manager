package com.smartflow.traffic.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 交通数据实体类
 */
@Entity
@Table(name = "traffic_data")
public class TrafficData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
    
    @Column(name = "pedestrian_count")
    private Integer pedestrianCount = 0;
    
    @Column(name = "car_count")
    private Integer carCount = 0;
    
    @Column(name = "bicycle_count")
    private Integer bicycleCount = 0;
    
    @Column(name = "bus_count")
    private Integer busCount = 0;
    
    @Column(name = "motorcycle_count")
    private Integer motorcycleCount = 0;
    
    @Column(name = "truck_count")
    private Integer truckCount = 0;
    
    @Column(name = "pedestrian_speed", precision = 5, scale = 2)
    private Double pedestrianSpeed = 0.0;
    
    @Column(name = "car_speed", precision = 5, scale = 2)
    private Double carSpeed = 0.0;
    
    @Column(name = "bicycle_speed", precision = 5, scale = 2)
    private Double bicycleSpeed = 0.0;
    
    @Column(name = "bus_speed", precision = 5, scale = 2)
    private Double busSpeed = 0.0;
    
    @Column(name = "motorcycle_speed", precision = 5, scale = 2)
    private Double motorcycleSpeed = 0.0;
    
    @Column(name = "truck_speed", precision = 5, scale = 2)
    private Double truckSpeed = 0.0;
    
    @Column(name = "volume", precision = 5, scale = 2)
    private Double volume = 0.0;
    
    @Column(name = "congestion")
    private Integer congestion = 0; // 0: Low, 1: Medium, 2: High
    
    @Column(name = "camera_id")
    private String cameraId;
    
    @Column(name = "location")
    private String location;
    
    // 构造函数
    public TrafficData() {
        this.dateTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    public Integer getPedestrianCount() {
        return pedestrianCount;
    }
    
    public void setPedestrianCount(Integer pedestrianCount) {
        this.pedestrianCount = pedestrianCount;
    }
    
    public Integer getCarCount() {
        return carCount;
    }
    
    public void setCarCount(Integer carCount) {
        this.carCount = carCount;
    }
    
    public Integer getBicycleCount() {
        return bicycleCount;
    }
    
    public void setBicycleCount(Integer bicycleCount) {
        this.bicycleCount = bicycleCount;
    }
    
    public Integer getBusCount() {
        return busCount;
    }
    
    public void setBusCount(Integer busCount) {
        this.busCount = busCount;
    }
    
    public Integer getMotorcycleCount() {
        return motorcycleCount;
    }
    
    public void setMotorcycleCount(Integer motorcycleCount) {
        this.motorcycleCount = motorcycleCount;
    }
    
    public Integer getTruckCount() {
        return truckCount;
    }
    
    public void setTruckCount(Integer truckCount) {
        this.truckCount = truckCount;
    }
    
    public Double getPedestrianSpeed() {
        return pedestrianSpeed;
    }
    
    public void setPedestrianSpeed(Double pedestrianSpeed) {
        this.pedestrianSpeed = pedestrianSpeed;
    }
    
    public Double getCarSpeed() {
        return carSpeed;
    }
    
    public void setCarSpeed(Double carSpeed) {
        this.carSpeed = carSpeed;
    }
    
    public Double getBicycleSpeed() {
        return bicycleSpeed;
    }
    
    public void setBicycleSpeed(Double bicycleSpeed) {
        this.bicycleSpeed = bicycleSpeed;
    }
    
    public Double getBusSpeed() {
        return busSpeed;
    }
    
    public void setBusSpeed(Double busSpeed) {
        this.busSpeed = busSpeed;
    }
    
    public Double getMotorcycleSpeed() {
        return motorcycleSpeed;
    }
    
    public void setMotorcycleSpeed(Double motorcycleSpeed) {
        this.motorcycleSpeed = motorcycleSpeed;
    }
    
    public Double getTruckSpeed() {
        return truckSpeed;
    }
    
    public void setTruckSpeed(Double truckSpeed) {
        this.truckSpeed = truckSpeed;
    }
    
    public Double getVolume() {
        return volume;
    }
    
    public void setVolume(Double volume) {
        this.volume = volume;
    }
    
    public Integer getCongestion() {
        return congestion;
    }
    
    public void setCongestion(Integer congestion) {
        this.congestion = congestion;
    }
    
    public String getCameraId() {
        return cameraId;
    }
    
    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
}


