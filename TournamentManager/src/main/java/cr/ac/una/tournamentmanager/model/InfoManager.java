package cr.ac.una.tournamentmanager.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import cr.ac.una.tournamentmanager.Controller.TournamentController;
import cr.ac.una.tournamentmanager.Util.FlowController;
import cr.ac.una.tournamentmanager.util.AppContext;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InfoManager {

    private static final Logger LOGGER = Logger.getLogger(InfoManager.class.getName());

    public InfoManager() {

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
            System.out.println("empty");
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
        System.out.println("\n Se llama a setTeamList\n");
        AppContext.getInstance().set("fullTeamArrayList", teams);
    }

    public static void SetTournamentList(ArrayList<TourneyDto> tournaments) {
        if (tournaments == null) {
            tournaments = new ArrayList<TourneyDto>();
        }
        AppContext.getInstance().set("fullTournamentsArrayList", tournaments);
    }

    public static SportDto GetSport(int sportID) {
        ArrayList<SportDto> sports = GetSportList();
        for (SportDto sport : sports) {
            if (sport.getID() == sportID) {
                return sport;
            }
        }
        return null;
    }

    public static SportDto GetSport(String sportName) {
        if (sportName == null || sportName.trim().isEmpty()) sportName = "NoName";
        ArrayList<SportDto> sports = GetSportList();
        for (SportDto sport : sports) {
            if (sport.getName().trim().equalsIgnoreCase(sportName)) {
                return sport;
            }
        }
        return null;
    }

    public static TeamDto GetTeam(Integer teamID) {
        if (teamID == null || teamID <= 0) teamID = 0;
        ArrayList<TeamDto> teams = GetTeamList();
        for (TeamDto team : teams) {
            if (team.getID() == teamID) {
                return team;
            }
        }
        return null;
    }

    public static void createWinnerCertificate(TeamDto winner, TourneyDto tournament) {
        String folderPath = "src/main/resources/cr/ac/una/tournamentmanager/Resources/Certificados";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs(); // Create the folder if it doesn't exist

        String sanitizedName = winner.getName().replaceAll("\\s+", "_"); // Replace spaces with underscores for the file name
        String fileName = folderPath + File.separator + "Certificado_" + sanitizedName + ".pdf";

        try {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36); // Create a PDF document with margins
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD, Color.BLACK);
            Paragraph title = new Paragraph("🏆 CERTIFICADO DE CAMPEÓN 🏆\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER); // Center-align the title
            document.add(title);

            try {
                String uriPath = winner.getTeamImageURL();
                String imagePath = new File(new URL(uriPath).toURI()).getAbsolutePath();
                Image image = Image.getInstance(imagePath);
                image.scaleToFit(180, 180); // Scale the image to fit within 180x180
                image.setAlignment(Image.ALIGN_CENTER); // Center-align the image
                document.add(image);
            } catch (Exception e) {
                Paragraph error = new Paragraph("\n⚠ No se pudo cargar la imagen del equipo.\n");
                error.setAlignment(Element.ALIGN_CENTER); // Center-align the error message
                document.add(error);
            }

            Font boldFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK);
            Font normalFont = new Font(Font.HELVETICA, 14, Font.NORMAL, Color.BLACK);

            Paragraph teamInfo = new Paragraph();
            teamInfo.setAlignment(Element.ALIGN_CENTER); // Center-align the team information
            teamInfo.add(new Chunk("\nEquipo ganador: ", boldFont));
            teamInfo.add(new Chunk(winner.getName() + "\n", normalFont));
            teamInfo.add(new Chunk("Deporte: ", boldFont));
            teamInfo.add(new Chunk(GetSport(winner.getSportID()).getName() + "\n", normalFont));
            document.add(teamInfo);

            Paragraph tournamentInfo = new Paragraph();
            tournamentInfo.setAlignment(Element.ALIGN_CENTER); // Center-align the tournament information
            tournamentInfo.add(new Chunk("\n📋 Información del Torneo:\n", boldFont));
            tournamentInfo.add(new Chunk("- Número de equipos: " + tournament.getTotalOfTeams() + "\n", normalFont));
            tournamentInfo.add(new Chunk("- Rondas jugadas: " + tournament.findHowManyRounds() + "\n", normalFont));
            tournamentInfo.add(new Chunk("- Fecha: " + tournament.getCreationDate() + "\n", normalFont));
            tournamentInfo.add(new Chunk("- Hora: " + tournament.getCreationTime() + "\n", normalFont));
            document.add(tournamentInfo);

            Paragraph congratulations = new Paragraph("\n¡Felicidades por su excelente desempeño!\n", normalFont);
            congratulations.setAlignment(Element.ALIGN_CENTER); // Center-align the congratulations message
            document.add(congratulations);

            Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC, new Color(102, 102, 102));
            Paragraph footer = new Paragraph("\nEste certificado está avalado por la Asociación de Torneos y Deportes O.S.J.E.J.R\n", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER); // Center-align the footer
            document.add(footer);

            document.close();
            System.out.println("✅ Certificado PDF generado: " + fileName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error al crear el certificado PDF", e);
        }
    }

    public static void deleteSport(int sportID) {
        ArrayList<TourneyDto> tournaments = GetTournamentList();
        for (TourneyDto tourney : tournaments) {
            if (tourney.getSportID() == sportID) tourney.setSportID(0);
        }
        SetTournamentList(tournaments);

        ArrayList<TeamDto> teams = GetTeamList();
        for (TeamDto team : teams) {
            if (team.getSportID() == sportID) team.setSportID(0);
        }
        SetTeamList(teams);

        ArrayList<SportDto> sports = GetSportList();
        sports.removeIf(sport -> sport.getID() == sportID);
        SetSportList(sports);
    }

    public static void deleteTeam(int teamID) {
        ArrayList<TourneyDto> tournaments = GetTournamentList();

        TournamentController tournamentController = (TournamentController) FlowController.getInstance().getController("TournamentView");
        TourneyDto currentTourney = tournamentController.getTourney();
        if (currentTourney != null && currentTourney.getTournamentRoundsID().get(0).contains(teamID)) {
            System.out.println("\nEl equipo " + GetTeam(teamID).getName() + " no se puede eliminar porque está en participando en el torneo actual.");
            return;
        }

        for (TourneyDto tourney : tournaments) {// Look into all tournaments
            if (tourney.getTournamentRoundsID() == null) continue;
            for (ArrayList<Integer> round : tourney.getTournamentRoundsID()) {
                int index = round.indexOf(teamID);// Look for the team you want to delete
                if (index != -1) round.set(index, -teamID);// Replace the team Id
                else break; // Go to next tourney
            }
        }

        ArrayList<TeamDto> teams = GetTeamList();
        teams.removeIf(team -> team.getID() == teamID);
        SetTeamList(teams);
    }

    public static String getFullTorneysData(int teamID) {
        StringBuilder data = new StringBuilder();
        ArrayList<TourneyDto> tournaments = GetTournamentList();
        int tourneyN = 1;

        for (TourneyDto tourney : tournaments) {
            ArrayList<ArrayList<Integer>> rounds = tourney.getTournamentRoundsID();
            if (rounds == null || rounds.isEmpty()) continue;

            if (!rounds.get(0).contains(teamID)) {
                tourneyN++;
                continue;
            }

            int victories = 0;
            int freePass = 0;
            int lastRound = 0;

            for (int r = 0; r < rounds.size() - 1; r++) {
                ArrayList<Integer> rondaActual = rounds.get(r);

                if (rounds.get(r + 1).contains(teamID)) { // Check if the team is in the next round

                    boolean wasFreePass = rounds.get(r).size() % 2 != 0 && rondaActual.get(rondaActual.size() - 1) == teamID;
                    if (rondaActual.get(0) == null && rondaActual.get(1) == teamID) wasFreePass = true;

                    if (wasFreePass) freePass++;
                    else victories++;
                } else {
                    lastRound = r;
                    break;
                }
            }

            if (rounds.get(rounds.size() - 1).contains(teamID)) {
                lastRound = rounds.size() - 1;
            }

            int position = calcTeamPosition(teamID, rounds, lastRound);
            String teamFinishInRound = whitchRoundTeamFinish(lastRound, rounds.size());

            data.append(String.format("Torneo #%d | Posicion: %d° | Victorias: %d | Pases Gratis: %d | Avance: %s\n",
                    tourneyN++, position, victories, freePass, teamFinishInRound));
        }
        System.out.println(data);
        return data.toString();
    }

    private static String whitchRoundTeamFinish(int roundN, int totalRounds) {
        int roundsLeft = totalRounds - roundN;
        switch (roundsLeft) {
            case 1:
                return "Campeón";
            case 2:
                return "Final";
            case 3:
                return "Semifinal";
            case 4:
                return "Cuartos de final";
            default:
                return "Ronda " + (roundN + 1);
        }
    }

    private static int calcTeamPosition(int teamID, ArrayList<ArrayList<Integer>> rounds, int finalRound) {
        int howManyLosersBefore = rounds.get(0).size() - rounds.get(finalRound).size();// Lossers of previous rounds
        howManyLosersBefore += rounds.get(finalRound).indexOf(teamID) / 2;// Lossers in the same round

        return rounds.get(0).size() - howManyLosersBefore;
    }

    public static javafx.scene.image.Image loadImage(String path) {

        String basePath = System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/tournamentmanager/Resources/";
        String imagePath = basePath + path;

        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            try {
                return new javafx.scene.image.Image(imageFile.toURI().toString());
            } catch (Exception e) {
                System.out.println("Error al cargar la imagen: " + imagePath);
            }
        }

        try {
            String defaultImagePath = basePath + "Default-Image.png";
            return new javafx.scene.image.Image(new File(defaultImagePath).toURI().toString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la imagen predeterminada.", e);
            return null;
        }
    }

    private void saveSports() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("Sports.json")) {
            gson.toJson(AppContext.getInstance().get("fullSportArrayList"), writer);

            writer.flush();
            System.out.println("All sports were saved successfully.");
        } catch (IOException s) {
            LOGGER.log(Level.SEVERE, "Error saving sports", s);
        }
    }

    private void LoadSports() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type sportList = new TypeToken<ArrayList<SportDto>>() {
        }.getType();
        ArrayList<SportDto> sports = new ArrayList<>();

        try (FileReader reader = new FileReader("Sports.json")) {
            sports = gson.fromJson(reader, sportList);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading sports", e);
        }
        AppContext.getInstance().set("fullSportArrayList", sports);
    }

    private void SaveTournaments() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("Tournament.json")) {
            gson.toJson(AppContext.getInstance().get("fullTournamentsArrayList"), writer);
            writer.flush();
            System.out.println("All tournaments were saved successfully.");
        } catch (IOException s) {
            LOGGER.log(Level.SEVERE, "Error saving tournaments", s);
        }
    }

    private void loadTournaments() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type tournamentList = new TypeToken<ArrayList<TourneyDto>>() {
        }.getType();
        ArrayList<TourneyDto> tournaments = new ArrayList<>();

        try (FileReader reader = new FileReader("Sports.json")) {
            tournaments = gson.fromJson(reader, tournamentList);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading tournaments", e);
        }
        AppContext.getInstance().set("fullTournamentsArrayList", tournaments);
    }

    private void SaveTeams() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("gson created");

        try (FileWriter writer = new FileWriter("Teams.json")) {
            System.out.println("enter try");
            gson.toJson(AppContext.getInstance().get("fullTeamArrayList"), writer);
            writer.flush();
            System.out.println("All teams were saved successfully.");
        } catch (IOException s) {
            LOGGER.log(Level.SEVERE, "Error saving teams", s);
        }
    }

    private void LoadTeams() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type teamsList = new TypeToken<ArrayList<TeamDto>>() {
        }.getType();
        ArrayList<TeamDto> teams = new ArrayList<TeamDto>();

        try (FileReader reader = new FileReader("Teams.json")) {
            teams = gson.fromJson(reader, teamsList);
        } catch (IOException s) {
            LOGGER.log(Level.WARNING, "Error loading teams", s);
        }
        System.out.println("size loading: " + teams.size());
        for (TeamDto team : teams) {
            System.out.println(team.getName());
        }
        AppContext.getInstance().set("fullTeamArrayList", teams);
    }

    public void LoadInfo() {
        LoadSports();
        System.out.println("sports loaded");
        LoadTeams();
        System.out.println("teams loaded");
        loadTournaments();
        System.out.println("tournaments loaded");
    }

    public void saveInfo() {
        SaveTeams();
        SaveTournaments();
        saveSports();
    }


}
