package cr.ac.una.tournamentmanager.model;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class EquipoDto {
    
    private StringProperty name;
    private StringProperty photo;
    private StringProperty sport;
    private int points; //de la clasificacion general
    private int anotaciones; //"goles" pero no son goles porque son varios deportes //maybe borrar
    private int desempenno; //va de 1 a 100 entre mayor sea mayor posibilidad de ganar el desempate
    
    public EquipoDto() {
        this.name = new SimpleStringProperty("");
        this.photo = new SimpleStringProperty("");
        this.sport = new SimpleStringProperty("");
        this.points = 0;
        this.anotaciones = 0;
        this.desempenno = (int) (Math.random() * 100) + 1; 
    }

    public String getNombre() {
        return name.get();
    }

    public void setNombre(String nombre) {
        this.name.set(nombre);
    }

    public String getFoto() {
        return photo.get();
    }

    public void setFoto(String foto) {
        this.photo.set(foto);
    }

    public String getSport() {
        return sport.get();
    }
    
    public void setSport(String sport){
        this.sport.set(sport);
    }
    
    public int getPuntos() {
        return points;
    }

    public void setPuntos(int puntos) {
        this.points = puntos;
    }

    public int getAnotaciones() {
       return anotaciones;
    }

    public void setAnotaciones(int anotaciones) {
        this.anotaciones = anotaciones;
    }
    
    public int getDesempenno() {
        return this.desempenno;
    }
    
    public void setDesempenno(int desempenno) {
        this.desempenno = desempenno;
    }

    public StringProperty getNombreProperty() {
        return name;
    }

    public StringProperty getFotoProperty() {
        return photo;
    }
    
    public StringProperty getSportProperty() {
        return sport;
    }
    
    public void sumPuntos(int valor) {
        valor += getPuntos();
        setPuntos(valor);
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final EquipoDto other = (EquipoDto) obj;
        return Objects.equals(this.getNombre(), other.getNombre());
    }
    
        
}
