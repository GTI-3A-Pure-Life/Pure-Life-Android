package com.example.rparcas.btleandroid2021.modelo;

/**
 * Ruben Pardo Casanova
 * 21/11/2021
 * InformeCalidad.java
 */
public class InformeCalidad {

    private float valorAQI;
    private Medicion.TipoMedicion tipoGas;

    public InformeCalidad(float valorAQI, Medicion.TipoMedicion tipoGas) {
        this.valorAQI = valorAQI;
        this.tipoGas = tipoGas;
    }

    public float getValorAQI() {
        return valorAQI;
    }

    public void setValorAQI(float valorAQI) {
        this.valorAQI = valorAQI;
    }

    public Medicion.TipoMedicion getTipoGas() {
        return tipoGas;
    }

    public void setTipoGas(Medicion.TipoMedicion tipoGas) {
        this.tipoGas = tipoGas;
    }
}
