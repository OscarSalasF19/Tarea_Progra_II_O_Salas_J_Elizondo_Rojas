package cr.ac.una.tournamentmanager.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class EquipoDto {
    
    private StringProperty nombre;
    private StringProperty foto;
    private ObjectProperty<DeporteDto> sport;
    private int puntos; //de la clasificacion general
    private int anotaciones; //"goles" pero no son goles porque son varios deportes
    private int desempenno; //va de 1 a 100 entre mayor sea mayor posibilidad de ganar el desempate
    
    public EquipoDto() {
        this.nombre = new SimpleStringProperty("");
        this.foto = new SimpleStringProperty("");
        this.sport = new SimpleObjectProperty<>();
        this.puntos = 0;
        this.anotaciones = 0;
        this.desempenno = 1;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getFoto() {
        return foto.get();
    }

    public void setFoto(String foto) {
        this.foto.set(foto);
    }

    public DeporteDto getSport() {
        return sport.get();
    }
    
    public void setSport(DeporteDto sport){
        this.sport.set(sport);
    }
    
    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
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
        return nombre;
    }

    public StringProperty getFotoProperty() {
        return foto;
    }
    
    public ObjectProperty<DeporteDto> getSportProperty() {
        return sport;
    }
    
    public void sumPuntos(int valor) {
        valor += getPuntos();
        setPuntos(valor);
    }
}
