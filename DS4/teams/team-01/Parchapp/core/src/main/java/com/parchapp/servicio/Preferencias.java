package com.parchapp.servicio;

import com.parchapp.dominio.Compania;
import com.parchapp.dominio.Etiqueta;
import com.parchapp.dominio.Lugar;

// Lo que respondió el usuario en "¿Qué hacemos hoy?"
public class Preferencias {

    private Compania compania = Compania.SOLO;
    private int personas = 1;
    private int buscadas;
    private int evitadas;
    private int presupuestoMaximo = Lugar.PRECIO_MAXIMO;

    public Preferencias con(Compania compania, int personas) {
        this.compania = compania;
        this.personas = Math.max(1, personas);
        return this;
    }

    public Preferencias buscar(Etiqueta... etiquetas) {
        buscadas |= Etiqueta.mascara(etiquetas);
        return this;
    }

    public Preferencias evitar(Etiqueta... etiquetas) {
        evitadas |= Etiqueta.mascara(etiquetas);
        return this;
    }

    public Preferencias presupuesto(int maximo) {
        presupuestoMaximo = Math.max(0, Math.min(Lugar.PRECIO_MAXIMO, maximo));
        return this;
    }

    public Compania getCompania() {
        return compania;
    }

    public int getPersonas() {
        return personas;
    }

    public int getBuscadas() {
        return buscadas;
    }

    public int getEvitadas() {
        return evitadas;
    }

    public int getPresupuestoMaximo() {
        return presupuestoMaximo;
    }
}
