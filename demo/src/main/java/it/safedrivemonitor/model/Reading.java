package it.safedrivemonitor.model;

/**
 * Entità che rappresenta una riga nella tabella readings (o un record
 * generico).
 */
public class Reading {
    private int id;
    private String driverId;
    private String driverName;
    private double alcoholLevel;
    private double thcLevel;
    private double cocaineLevel;
    private double mdmaLevel;
    private String result;
    private String timestamp;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public double getAlcoholLevel() {
        return alcoholLevel;
    }

    public void setAlcoholLevel(double alcoholLevel) {
        this.alcoholLevel = alcoholLevel;
    }

    public double getThcLevel() {
        return thcLevel;
    }

    public void setThcLevel(double thcLevel) {
        this.thcLevel = thcLevel;
    }

    public double getCocaineLevel() {
        return cocaineLevel;
    }

    public void setCocaineLevel(double cocaineLevel) {
        this.cocaineLevel = cocaineLevel;
    }

    public double getMdmaLevel() {
        return mdmaLevel;
    }

    public void setMdmaLevel(double mdmaLevel) {
        this.mdmaLevel = mdmaLevel;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
