package cr.ac.una.tournamentmanager.model;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class DeporteDto {
    
    private StringProperty nombre;
    private StringProperty bola;
    
    
    public DeporteDto () {
        this.nombre = new SimpleStringProperty("");
        this.bola = new SimpleStringProperty("");
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getBola() {
        return bola.get();
    }

    public void setBola(String bola) {
        this.bola.set(bola);
    }

    public StringProperty getNombreProperty() {
        return nombre;
    }

    public StringProperty getBolaProperty() {
        return bola;
    }

    @Override //ni idea
    public int hashCode() { 
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DeporteDto other = (DeporteDto) obj;
        return Objects.equals(this.nombre.get(), other.nombre.get());
    }

    @Override
    public String toString() {
        return "DeporteDto{" + "nombre=" + nombre.get() + ", bola=" + bola.get() + '}';
    }
    
}
