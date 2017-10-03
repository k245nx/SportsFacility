import components.SportsFacilityCustomer;
import components.Match;
import components.SeatPosition;
import components.Ticket;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * File di test per la classe SportsFacilityCustomer
 * @author Roberto Tarullo, Pasquale Turi
 */
public class Tester2 {
    public static void main(String[] args){
        final String path="C:\\Users\\Roberto Tarullo\\Desktop\\";
    
        SportsFacilityCustomer customer = new SportsFacilityCustomer(path, "turi", "Studente"); // crea un nuovo oggetto per la modalità cliente

        // lista partite in una determinata settimana
        ArrayList<Match> weekMatches = customer.getWeekMatches(14, 3, 2016);
        System.out.println("\nLista delle partite nella settimana a partire dal 14/03/2016:");
        for(int i=0; i < weekMatches.size(); i++)
            System.out.println(i+1 + "\t" + customer.getMatchInfo(weekMatches.get(i)));
        System.out.println();

        // lista partite in un determinato stadio
        ArrayList<Match> stadiumMatches = customer.getStadiumMatches(customer.getStadiums().get(0));
        System.out.println("Lista delle partite nello stadio Empire Stadium:");
        for(int i=0; i < stadiumMatches.size(); i++)
            System.out.println(i+1 + "\t" + customer.getMatchInfo(stadiumMatches.get(i)));
        System.out.println();

        // ordine cronologico
        ArrayList<Match> matchlist = customer.getUpcomingMatches();
        System.out.println("Ordine cronologico:");
        for(int i=0; i < matchlist.size(); i++)
            System.out.println(i+1 + "\t" + customer.getMatchInfo(matchlist.get(i)));
        System.out.println();

        // ordine identificativo di stadio
        matchlist = customer.getStadiumSortedMatches();
        System.out.println("Ordine identificativo di stadio:");
        for(int i=0; i < matchlist.size(); i++)
            System.out.println("ID: " + matchlist.get(i).getStadium().getID() + "\t" + customer.getMatchInfo(matchlist.get(i)));
        System.out.println();

        // Ordine lessicografico crescente rispetto al nome delle squadre che si affrontano
        matchlist = customer.getLexicographicallySortedMatches();
        System.out.println("Ordine lessicografico crescente rispetto al nome delle squadre che si affrontano:");
        for(int i=0; i < matchlist.size(); i++)
            System.out.println((i+1) + "\t" + customer.getMatchInfo(matchlist.get(i)));
        System.out.println();

        // Prenota due biglietti
        System.out.println("Prenoto due biglietti... ");
        SeatPosition pos = new SeatPosition(0, 0, 0); // prima fila, tribuna nord, primo posto
        customer.preorder(customer.getMatches().get(0), pos);
        SeatPosition pos1 = new SeatPosition(2, 0, 0); // seconda fila, tribuna nord, primo posto
        customer.preorder(customer.getMatches().get(0), pos1);
        
        // prenotazioni effettuate
        System.out.println("Prenotazioni effettuate: ");
        ArrayList<Ticket> preorders = customer.getPreorders();
        for(int i=0; i < preorders.size(); i++)
            System.out.println((i+1) + "\t" + customer.getMatchInfo(preorders.get(i).getMatch()));
        System.out.println();

        // Acquista un biglietto direttamente
        System.out.println("Acquisto un biglietto senza averlo prenotato...");
        SeatPosition pos2 = new SeatPosition(1, 0, 0); // seconda fila, tribuna nord, primo posto
        customer.order(customer.getMatches().get(0), pos2);
        
        // acquisti effettuati
        System.out.println("Acquisti effettuati: ");
        ArrayList<Ticket> orders = customer.getOrders();
        for(int i=0; i < orders.size(); i++)
            System.out.println(i+1 + "\t" + orders.get(i).getMatch().toString() + " (€" + new DecimalFormat("##.##").format(orders.get(i).getOrderPrice()) + ")");
        System.out.println();

        // Paga un biglietto già prenotato
        System.out.println("Pago il primo biglietto già prenotato...");
        customer.pay(customer.getMatches().get(0), pos);
        
        // prenotazioni effettuate
        System.out.println("Prenotazioni effettuate: ");
        preorders = customer.getPreorders();
        for(int i=0; i < preorders.size(); i++)
            System.out.println((i+1) + "\t" + customer.getMatchInfo(preorders.get(i).getMatch()));
        
        // acquisti effettuati
        System.out.println("Acquisti effettuati: ");
        orders = customer.getOrders();
        for(int i=0; i < orders.size(); i++)
            System.out.println(i+1 + "\t" + orders.get(i).getMatch().toString() + " (€" + new DecimalFormat("##.##").format(orders.get(i).getOrderPrice()) + ")");
        System.out.println();

        // cancella prenotazione partita
        System.out.println("Cancello la prenotazione rimasta...");
        customer.cancelPreorder(preorders.get(0).getMatch(), pos1);
        System.out.println("Prenotazioni: ");
        if(customer.getPreorders().isEmpty())
            System.out.println("Nessuna prenotazione trovata\n");
    }
}
