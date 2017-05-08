package com.example.twins.testkeepsolid.data.model;

import java.util.List;

public class TaskModel {

    private String notes, alias, title, version, remindOnDate, invitee;
    private int type, creationDate;
    private boolean completed;
    private RemindOnLocation remindOnLocation;
    private List<String> tasks;

    public TaskModel() {
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRemindOnDate() {
        return remindOnDate;
    }

    public void setRemindOnDate(String remindOnDate) {
        this.remindOnDate = remindOnDate;
    }

    public String getInvitee() {
        return invitee;
    }

    public void setInvitee(String invitee) {
        this.invitee = invitee;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(int creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public RemindOnLocation getRemindOnLocation() {
        return remindOnLocation;
    }

    public void setRemindOnLocation(RemindOnLocation remindOnLocation) {
        this.remindOnLocation = remindOnLocation;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    public class RemindOnLocation {
        private String description ;
        private int radius ;
        private boolean onEnter;
        private Coordinates coordinates;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public boolean isOnEnter() {
            return onEnter;
        }

        public void setOnEnter(boolean onEnter) {
            this.onEnter = onEnter;
        }

        public Coordinates getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
        }
    }

    public class Coordinates {
        private String longitude, latitude;

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
    }
}
