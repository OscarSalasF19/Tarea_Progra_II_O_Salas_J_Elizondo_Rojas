package cr.ac.una.tournamentmanager.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class SportDto {

    private final StringProperty name;
    private final StringProperty ballImageURL;

    public SportDto() {
        this.name = new SimpleStringProperty("");
        this.ballImageURL = new SimpleStringProperty("");
    }

    public SportDto(String name, String ballImageURL) {
        this.name = new SimpleStringProperty(name);
        this.ballImageURL = new SimpleStringProperty(ballImageURL);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getBallImageURL() {
        return ballImageURL.get();
    }

    public void setBallImageURL(String ballImageURL) {
        this.ballImageURL.set(ballImageURL);
    }

    public StringProperty getNameProperty() {
        return name;
    }

    public StringProperty getBallImageURLProperty() {
        return ballImageURL;
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
        final SportDto other = (SportDto) obj;
        return Objects.equals(this.name.get(), other.name.get());
    }

    @Override
    public String toString() {
        return "SportDto{" + "name=" + name.get() + ", ballImageURL=" + ballImageURL.get() + '}';
    }

}
