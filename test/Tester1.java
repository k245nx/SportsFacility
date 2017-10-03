import components.DayOfWeekDiscount;
import components.SportsFacilityManager;
import components.SportsFacilityCustomer;
import components.Match;
import components.PartOfDayDiscount;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * File di test per la classe SportsFacilityCustomer
 * @author Roberto Tarullo, Pasquale Turi
 */
public class Tester1{
    public static void main(String[] args){
        final String path="C:\\Users\\Roberto Tarullo\\Desktop\\";
        
        SportsFacilityManager manager = new SportsFacilityManager(path); // crea un nuovo oggetto per la modalità gestore
        
        // aggiungi 5 stadi
        manager.addStadium("Empire Stadium", 10);
        manager.addStadium("Maine Road", 1000);
        manager.addStadium("Wedaustadion", 500);
        manager.addStadium("Roker Park", 300);
        manager.addStadium("Arsenal Stadium", 700);
        
        // Inserisci 10 parite
        manager.addMatch(manager.getStadiums().get(1), 23, 3, 2016, 16, 30, "Inter", "Milan", 100);
        manager.addMatch(manager.getStadiums().get(4), 24, 4, 2016, 9, 30, "Palermo", "Juventus", 100);
        manager.addMatch(manager.getStadiums().get(1), 15, 3, 2016, 8, 30, "Arsenal", "Real Madrid", 100);
        manager.addMatch(manager.getStadiums().get(0), 17, 3, 2016, 21, 30, "Francia", "Italia", 100);
        manager.addMatch(manager.getStadiums().get(2), 20, 5, 2016, 22, 30, "Livorno", "Napoli", 100);
        manager.addMatch(manager.getStadiums().get(3), 23, 3, 2016, 0, 30, "Bologna", "Avellino", 100);
        manager.addMatch(manager.getStadiums().get(3), 24, 5, 2016, 6, 30, "Roma", "Lazio", 100);
        manager.addMatch(manager.getStadiums().get(4), 15, 4, 2016, 7, 30, "Sampdoria", "Torino FC", 100);
        manager.addMatch(manager.getStadiums().get(0), 17, 3, 2016, 15, 30, "Udinese", "Sassuolo", 100);
        manager.addMatch(manager.getStadiums().get(4), 20, 6, 2016, 2, 30, "Juventus", "Napoli", 100);
        
        // test partite in base alla capienza degli stadi
        System.out.println("\nElenco delle partite in base alla capienza degli stadi:");
        for(int i=0; i < manager.getCapacitySortedMatches().size(); i++)
            System.out.println("Capienza: " + manager.getCapacitySortedMatches().get(i).getStadium().getCapacity() + "\t" + manager.getMatchInfo(manager.getCapacitySortedMatches().get(i)));
        System.out.println();
        
        // test partite in ordine cronologico
        System.out.println("Elenco delle partite in ordine cronologico:");
        for(int i=0; i < manager.getDateSortedMatches().size(); i++)
            System.out.println(i+1 + "\t" + manager.getDateSortedMatches().get(i).toString());
        System.out.println();
        
        // attiva politica di sconto per una partita
        manager.enableDiscount(manager.getMatches().get(0), 20);
        System.out.println("Sconto del 20% applicato sulla partita " + manager.getMatches().get(0).toString());
        
        // attiva politica di sconto per tutte le partite di uno stadio
        manager.enableDiscount(manager.getStadiums().get(0), 10);
        System.out.println("Sconto del 10% applicato su tutte le partite dello stadio " + manager.getStadiums().get(0).toString());
        
        // Attiva politica di sconto per tutte le partite appartenenti a una fascia giornaliera
        manager.enablePartOfTheDayDiscount(PartOfDayDiscount.MORNING, 30);
        System.out.println("Sconto del 30% applicato a tutte le partite che si svolgono di mattina");
        
        // Attiva politica di sconto per tutte le partite che si svolgono in un giorno della settimana
        manager.enableDayDiscount(DayOfWeekDiscount.FRIDAY, 40);
        System.out.println("Sconto del 40% applicato a tutte le partite che si svolgono il venerdì");
        
        // Attiva politica di sconto per tutti i clienti appartenenti ad una categoria
        manager.enableDiscount("Studente", 5);
        System.out.println("Sconto del 5% applicato a tutti i clienti che appartengono alla categoria Studente");
        System.out.println();
        
        // Assegna un prezzo a tutte le partite che si svolgono in un determinato stadio
        manager.setPrice(manager.getStadiums().get(4), 10);
        System.out.println("Prezzo base di tutte le partite che si svolgono nell'Arsenal Stadium aggiornato a €10");
        System.out.println("Elenco prezzi base delle partite per Arsenal Stadium:");
        for(int i=0; i<manager.getMatches().size(); i++){
            if(manager.getMatches().get(i).getStadium().equals(manager.getStadiums().get(4)))
            System.out.println(manager.getMatches().get(i) + " - €" + manager.getMatches().get(i).getPrice());
        }
        System.out.println();
        
        // Riduci la capienza di uno stadio
        manager.setCapacity(manager.getStadiums().get(3), -100);
        System.out.println("Capienza del Roker Park ridotta di 100");
        System.out.println("Capienza: " + manager.getStadiums().get(3).getCapacity() + "(excepted: 200)");
        System.out.println();
        
        // registra nuovo utente con nome utente "turi" e categoria "Studente"
        try{
            PrintWriter out = new PrintWriter(path + "users.dat");
            out.println("1"); // stampa nuovo numero utenti
            out.println(); // stampa riga vuota
            out.println("turi");
            out.println("Studente");
            out.println(); // stampa riga vuota
            out.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
        // visualizzo l'elenco delle partite come cliente studente
        SportsFacilityCustomer customer = new SportsFacilityCustomer(path, "turi", "Studente"); // crea un nuovo oggetto per la modalità cliente
        System.out.println("Elenco delle partite come cliente studente:");
        ArrayList<Match> matchlist = customer.getUpcomingMatches();
        for(int i=0; i < matchlist.size(); i++)
            System.out.println(i+1 + "\t" + customer.getMatchInfo(matchlist.get(i)));
        System.out.println();
    }
}
