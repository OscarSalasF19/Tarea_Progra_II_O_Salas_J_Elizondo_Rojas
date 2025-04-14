package cr.ac.una.tournamentmanager.model;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class DeporteDto {
    
    private StringProperty Name;
    private StringProperty ballImageURL;
    
    
    public DeporteDto () {
        this.Name = new SimpleStringProperty("");
        this.ballImageURL = new SimpleStringProperty("");
    }

    public String getName() {
        return Name.get();
    }

    public void setName(String nombre) {
        this.Name.set(nombre);
    }

    public String getBallImageURL() {
        return ballImageURL.get();
    }

    public void setBallImageURL(String bola) {
        this.ballImageURL.set(bola);
    }

    public StringProperty getNameProperty() {
        return Name;
    }

    public StringProperty getBallImageURLProperty() {
        return ballImageURL;
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
        return Objects.equals(this.Name.get(), other.Name.get());
    }

    @Override
    public String toString() {
        return "DeporteDto{" + "nombre=" + Name.get() + ", bola=" + ballImageURL.get() + '}';
    }
    
}
