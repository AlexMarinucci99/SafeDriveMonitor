package it.safedrivemonitor.model;

/**
 * Entità che rappresenta una riga nella tabella readings (o un record generico).
 */
public class Reading {
    // Identificatore univoco della lettura
    private int id;
    // Identificatore del driver (autista)
    private String driverId;
    // Nome del driver
    private String driverName;
    // Livello di alcol rilevato
    private double alcoholLevel;
    // Livello di THC rilevato
    private double thcLevel;
    // Livello di cocaina rilevato
    private double cocaineLevel;
    // Livello di MDMA rilevato
    private double mdmaLevel;
    // Risultato dell'analisi (es. "positivo" o "negativo")
    private String result;
    // Timestamp della lettura (rappresentato come String)
    private String timestamp;

    // Restituisce il nome del driver
    public String getDriverName() {
        return driverName;
    }

    // Imposta il nome del driver
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    // Restituisce l'identificativo univoco della lettura
    public int getId() {
        return id;
    }

    // Imposta l'identificativo univoco della lettura
    public void setId(int id) {
        this.id = id;
    }

    // Restituisce l'identificatore del driver
    public String getDriverId() {
        return driverId;
    }

    // Imposta l'identificatore del driver
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    // Restituisce il livello di alcol rilevato
    public double getAlcoholLevel() {
        return alcoholLevel;
    }

    // Imposta il livello di alcol rilevato
    public void setAlcoholLevel(double alcoholLevel) {
        this.alcoholLevel = alcoholLevel;
    }

    // Restituisce il livello di THC rilevato
    public double getThcLevel() {
        return thcLevel;
    }

    // Imposta il livello di THC rilevato
    public void setThcLevel(double thcLevel) {
        this.thcLevel = thcLevel;
    }

    // Restituisce il livello di cocaina rilevato
    public double getCocaineLevel() {
        return cocaineLevel;
    }

    // Imposta il livello di cocaina rilevato
    public void setCocaineLevel(double cocaineLevel) {
        this.cocaineLevel = cocaineLevel;
    }

    // Restituisce il livello di MDMA rilevato
    public double getMdmaLevel() {
        return mdmaLevel;
    }

    // Imposta il livello di MDMA rilevato
    public void setMdmaLevel(double mdmaLevel) {
        this.mdmaLevel = mdmaLevel;
    }

    // Restituisce il risultato dell'analisi
    public String getResult() {
        return result;
    }

    // Imposta il risultato dell'analisi
    public void setResult(String result) {
        this.result = result;
    }

    // Restituisce il timestamp della lettura
    public String getTimestamp() {
        return timestamp;
    }

    // Imposta il timestamp della lettura
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
