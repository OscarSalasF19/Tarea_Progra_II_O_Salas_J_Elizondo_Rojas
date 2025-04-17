/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.tournamentmanager.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cr.ac.una.tournamentmanager.Util.Mensaje;
import cr.ac.una.tournamentmanager.util.AppContext;
import javafx.scene.control.Alert;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author oscar
 */
public class InfoManager {


    public InfoManager() {

    }

    public void saveSports() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("Sports.json")) {
            gson.toJson(AppContext.getInstance().get("fullSportArrayList"), writer);
            writer.flush();
            Mensaje message = new Mensaje();
            message.show(Alert.AlertType.CONFIRMATION, "saved succesfully", "all sports were saved succesfully");
        } catch (IOException s) {
            Mensaje message = new Mensaje();
            message.show(Alert.AlertType.ERROR, "Error Saving", "sports were not able to save");
        }
    }

    public void LoadSports() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type sportList = new TypeToken<ArrayList<SportDto>>() {
        }.getType();
        ArrayList<SportDto> sports = new ArrayList<SportDto>();

        try (FileReader reader = new FileReader("Sports.json")) {
            sports = gson.fromJson(reader, sportList);
        } catch (IOException e) {

        }
        AppContext.getInstance().set("fullSportArrayList", sports);
    }

    public void SaveTournaments() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("Tournament.json")) {
            gson.toJson(AppContext.getInstance().get("FullTournamentsArrayList"));
            writer.flush();
            Mensaje message = new Mensaje();
            message.show(Alert.AlertType.CONFIRMATION, "saved succesfully", "all tournaments were saved succesfully");
        } catch (IOException s) {
            Mensaje message = new Mensaje();
            message.show(Alert.AlertType.ERROR, "Error Saving", "Tournaments were not able to save");
        }
    }

    public void loadTournaments() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type tournamentList = new TypeToken<ArrayList<TourneyDto>>() {
        }.getType();
        ArrayList<TourneyDto> tournaments = new ArrayList<TourneyDto>();

        try (FileReader reader = new FileReader("Sports.json")) {
            tournaments = gson.fromJson(reader, tournamentList);
        } catch (IOException e) {

        }
        AppContext.getInstance().set("fullTournamentsArrayList", tournaments);
    }
}

