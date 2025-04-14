package cr.ac.una.tournamentmanager.model;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class PartidoDto {
    
    private Timer timer;
    private int delay;
    private boolean timesUp;
    
    public PartidoDto(int timeInSeconds) {
        this.timer = new Timer();
        this.delay = timeInSeconds * 1000;
        this.timesUp = false;
    }
    
    
    
    public void startGame(ArrayList<EquipoDto> competingTeams, int teams) {
        ArrayList<Integer> marcador;
        marcador = new ArrayList<>();
        marcador.add(0,0); marcador.add(1,0);
        
        while (!timesUp) {
            if(false) {//equipo 0 marca
                marcador.set(0, marcador.get(0) + 1);
            }
            if(false) {//equipo 1 marca
                marcador.set(1, marcador.get(1) + 1);
            }
            
            
            try {
                Thread.sleep(1000); // Pausa de 1 segundo para simular el desarrollo del partido
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() { //codigo que se ejecuta al terminar el tiempo
                timesUp = true; //cancela el while
                
                //suma 3-2 pts al ganador //elimina al perdedor de los competidores
                if (marcador.get(0) == marcador.get(1)){
                    //metodo de desempate //ganador +2 pts
                } else if (marcador.get(0) > marcador.get(1)) {
                   competingTeams.get(teams).sumPoints(3); 
                   competingTeams.remove(teams + 1); 
                } else {
                   competingTeams.get(teams + 1).sumPoints(3); 
                   competingTeams.remove(teams);
                }
            }
        }, delay);
    }
    

    
    
    
    
}
