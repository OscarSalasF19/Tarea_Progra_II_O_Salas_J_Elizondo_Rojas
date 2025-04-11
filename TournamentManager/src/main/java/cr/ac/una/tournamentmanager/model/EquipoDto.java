package cr.ac.una.tournamentmanager.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class EquipoDto {
    
    private StringProperty name;
    private StringProperty photo;
    private ObjectProperty<DeporteDto> sport;
    private int points; //de la clasificacion general
    private int anotaciones; //"goles" pero no son goles porque son varios deportes
    private int desempenno; //va de 1 a 100 entre mayor sea mayor posibilidad de ganar el desempate
    
    public EquipoDto() {
        this.name = new SimpleStringProperty("");
        this.photo = new SimpleStringProperty("");
        this.sport = new SimpleObjectProperty<>();
        this.points = 0;
        this.anotaciones = 0;
        this.desempenno = 1;//del 1 al 100, entre m[as alto m[as probabilidades de ganar
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

    public DeporteDto getSport() {
        return sport.get();
    }
    
    public void setSport(DeporteDto sport){
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
    
    public ObjectProperty<DeporteDto> getSportProperty() {
        return sport;
    }
    
    public void sumPuntos(int valor) {
        valor += getPuntos();
        setPuntos(valor);
    }
}
