package pl.wsb.fitnesstracker.training.dto;

import pl.wsb.fitnesstracker.training.internal.ActivityType;

import java.util.Date;

public class TrainingUpdateDto {

    private Date startTime;
    private Date endTime;
    private ActivityType activityType;
    private double distance;
    private double averageSpeed;

    public TrainingUpdateDto() {}

    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public ActivityType getActivityType() {
        return activityType;
    }
    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public double getAverageSpeed() {
        return averageSpeed;
    }
    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
}
