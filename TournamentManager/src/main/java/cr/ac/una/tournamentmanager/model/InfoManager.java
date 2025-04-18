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

    public static ArrayList<SportDto> GetSportList() {
        ArrayList<SportDto> sports = (ArrayList<SportDto>) AppContext.getInstance().get("fullSportArrayList");
        if (sports == null) {
            sports = new ArrayList<SportDto>();
            AppContext.getInstance().set("fullSportArrayList", sports);
        }
        return sports;
    }

    public static ArrayList<TeamDto> GetTeamList() {
        ArrayList<TeamDto> teams = (ArrayList<TeamDto>) AppContext.getInstance().get("fullTeamArrayList");
        if (teams == null) {
            teams = new ArrayList<TeamDto>();
            AppContext.getInstance().set("fullTeamArrayList", teams);
        }
        return teams;
    }

    public static ArrayList<TourneyDto> GetTournamentList() {
        ArrayList<TourneyDto> tournaments = (ArrayList<TourneyDto>) AppContext.getInstance().get("fullTournamentsArrayList");
        if (tournaments == null) {
            tournaments = new ArrayList<TourneyDto>();
            AppContext.getInstance().set("fullTournamentsArrayList", tournaments);
        }
        return tournaments;
    }

    public static void SetSportList(ArrayList<SportDto> sports) {
        if (sports == null) {
            sports = new ArrayList<SportDto>();
        }
        AppContext.getInstance().set("fullSportArrayList", sports);
    }

    public static void SetTeamList(ArrayList<TeamDto> teams) {
        if (teams == null) {
            teams = new ArrayList<TeamDto>();
        }
        AppContext.getInstance().set("fullTeamArrayList", teams);
    }

    public static void SetTournamentList(ArrayList<TourneyDto> tournaments) {
        if (tournaments == null) {
            tournaments = new ArrayList<TourneyDto>();
        }
        AppContext.getInstance().set("fullTournamentsArrayList", tournaments);
    }

    public static String GetSportName(int sportID) {
        ArrayList<SportDto> sports = GetSportList();
        for (SportDto sport : sports) {
            if (sport.getID() == sportID) {
                return sport.getName();
            }
        }
        return "";
    }

 public static int GetSportID(String sportName) {
        ArrayList<SportDto> sports = GetSportList();
        for (SportDto sport : sports) {
            if (sport.getName().equals(sportName)) {
                return sport.getID();
            }
        }
        return 0;
 }

}

