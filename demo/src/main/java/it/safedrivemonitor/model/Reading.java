package it.safedrivemonitor.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entità immutabile che rappresenta una riga nella tabella readings (o un record generico).
 */
public final class Reading {

    private final int id;
    private final String driverId;
    private final String driverName;
    private final double alcoholLevel;
    private final double thcLevel;
    private final double cocaineLevel;
    private final double mdmaLevel;
    private final String result;
    private final LocalDateTime timestamp;

    public Reading(int id, String driverId, String driverName, double alcoholLevel,
                   double thcLevel, double cocaineLevel, double mdmaLevel,
                   String result, LocalDateTime timestamp) {
        this.id = id;
        this.driverId = driverId;
        this.driverName = driverName;
        this.alcoholLevel = alcoholLevel;
        this.thcLevel = thcLevel;
        this.cocaineLevel = cocaineLevel;
        this.mdmaLevel = mdmaLevel;
        this.result = result;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public double getAlcoholLevel() {
        return alcoholLevel;
    }

    public double getThcLevel() {
        return thcLevel;
    }

    public double getCocaineLevel() {
        return cocaineLevel;
    }

    public double getMdmaLevel() {
        return mdmaLevel;
    }

    public String getResult() {
        return result;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reading)) return false;
        Reading reading = (Reading) o;
        return id == reading.id &&
                Double.compare(reading.alcoholLevel, alcoholLevel) == 0 &&
                Double.compare(reading.thcLevel, thcLevel) == 0 &&
                Double.compare(reading.cocaineLevel, cocaineLevel) == 0 &&
                Double.compare(reading.mdmaLevel, mdmaLevel) == 0 &&
                Objects.equals(driverId, reading.driverId) &&
                Objects.equals(driverName, reading.driverName) &&
                Objects.equals(result, reading.result) &&
                Objects.equals(timestamp, reading.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, driverName, alcoholLevel, thcLevel, cocaineLevel, mdmaLevel, result, timestamp);
    }

    @Override
    public String toString() {
        return "Reading{" +
                "id=" + id +
                ", driverId='" + driverId + '\'' +
                ", driverName='" + driverName + '\'' +
                ", alcoholLevel=" + alcoholLevel +
                ", thcLevel=" + thcLevel +
                ", cocaineLevel=" + cocaineLevel +
                ", mdmaLevel=" + mdmaLevel +
                ", result='" + result + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
