import components.SportsFacilityCustomer;
import components.graphics.StadiumComponent;
import components.Ticket;
import components.Match;
import components.SeatPosition;
import components.exceptions.PostoIndisponibileException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Frame in cui viene visualizzato uno stadio e i suoi posti
 * @author Roberto Tarullo, Pasquale Turi
 */
public class StadiumFrame extends JFrame{
    /**
     * Crea un oggetto di tipo JFrame contenente uno stadio e i suoi posti
     * @param stadiumMatch lo stadio da rappresentare
     * @param order indica se il frame è stato creato per un acquisto o per una prenotazione
     * @param sportsFacility la gestione sportiva su cui operare in caso di acquisto/prenotazione
     */
    protected StadiumFrame(Match stadiumMatch, boolean order, SportsFacilityCustomer sportsFacility){
        for(int i=0; i<sportsFacility.getTickets().size(); i++){  // scorri tutti i biglietti e seleziona quelli della partita
            if(sportsFacility.getTickets().get(i).getMatch().equals(stadiumMatch))
                tickets.add(sportsFacility.getTickets().get(i));
        }
        this.order = order;
        this.sportsFacility = sportsFacility;
        this.match = stadiumMatch;
        
        c = new GridBagConstraints[5];
        c[0] = new GridBagConstraints(); // costraint per la tribuna ovest
        c[1] = new GridBagConstraints(); // costraint per la tribuna nord
        c[2] = new GridBagConstraints(); // costraint per la tribuna est
        c[3] = new GridBagConstraints(); // costraint per la tribuna sud
        c[4] = new GridBagConstraints(); // costraint per il pannello interno
            
        // regole per la tribuna ovest
        c[0].gridx = 0;
        c[0].gridy = 1;
        c[0].gridwidth = 1;
        c[0].gridheight = 1;
        c[0].weightx = 0.05;
        c[0].weighty = 0.90;
            
        // regole per la tribuna nord
        c[1].gridx = 1;
        c[1].gridy = 0;
        c[1].gridwidth = 1;
        c[1].gridheight = 1;
        c[1].weightx = 0.90;
        c[1].weighty = 0.05;
            
        // regole per la tribuna est
        c[2].gridx = 2;
        c[2].gridy = 1;
        c[2].gridwidth = 1;
        c[2].gridheight = 1;
        c[2].weightx = 0.05;
        c[2].weighty = 0.90;
            
        // regole per la tribuna sud
        c[3].gridx = 1; 
        c[3].gridy = 2;
        c[3].gridwidth = 1;
        c[3].gridheight = 1;
        c[3].weightx = 0.90;
        c[3].weighty = 0.05;
            
        // regole per il pannello interno
        c[4].gridx = 1;
        c[4].gridy = 1;
        c[4].gridwidth = 1;
        c[4].gridheight = 1;
        c[4].weightx = 0.9;
        c[4].weighty = 0.9;
        c[4].fill = GridBagConstraints.BOTH;
        
        // crea pannello per il solo campo sportivo
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new GridLayout(1,1));
        fieldPanel.add(new StadiumComponent());
        
        // aggiungi al frame il pannello dello stadio
        JPanel mainPanel = createStadiumPanel(fieldPanel, match.getStadium().getCapacity());
        JScrollPane jsp = new JScrollPane(mainPanel);
        add(jsp);
        
        int FRAME_WIDTH = (WIDTH_SEATS*BUTTONSIZE)+2*(row+1)*(BUTTONSIZE+OFFSET) + 30;
        int FRAME_HEIGHT = (HEIGHT_SEATS*BUTTONSIZE)+2*(row+1)*(BUTTONSIZE+OFFSET) + 30;
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if(FRAME_WIDTH>dim.width || FRAME_HEIGHT>dim.height){
            FRAME_WIDTH = dim.width;
            FRAME_HEIGHT = dim.height;
        }
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocation(dim.width/2-FRAME_WIDTH/2, dim.height/2-FRAME_WIDTH/2);
    }
    
    /**
     * Crea ricorsivamente un pannello con la rappresentazione dello stadio
     * Ad ogni passo ricorsivo viene aggiunta una fila di sedie
     * @param centerPanel pannello da posizionare al centro dello stadio, la rappresentazione del campo
     * @param seatsToDraw il numero di sedie da rappresentare
     * @return 
     */
    private JPanel createStadiumPanel(JPanel centerPanel, int seatsToDraw){
        if(seatsToDraw<=0)
            return centerPanel;
        
        int nordSeats = WIDTH_SEATS+ADDED_SEATS*row, sudSeats = nordSeats;
        int ovestSeats = HEIGHT_SEATS+ADDED_SEATS*row, estSeats = ovestSeats;
        
        // se è l'ultimo passo ricorsivo
        if(seatsToDraw <= nordSeats + estSeats + sudSeats + ovestSeats){ 
            if(seatsToDraw<=nordSeats){
                nordSeats = seatsToDraw; 
                sudSeats = estSeats = ovestSeats = 0;
            }
            else{
                if(seatsToDraw<=nordSeats+estSeats){
                    estSeats = seatsToDraw-nordSeats;
                    ovestSeats = sudSeats = 0;
                }
                else{ 
                    if(seatsToDraw<=nordSeats+estSeats+sudSeats){
                        sudSeats = seatsToDraw-nordSeats-estSeats;
                        ovestSeats = 0;
                    }
                    else // se <= nord + est + sud + ovest
                        ovestSeats = seatsToDraw-nordSeats-estSeats-sudSeats;
                }
            }   
        }

        JPanel nordPanel = createSeatsPanel(nordSeats, 0, row, WIDTH_SEATS+ADDED_SEATS*row); // crea tribuna nord
        JPanel estPanel = createSeatsPanel(estSeats, 1, row, HEIGHT_SEATS+ADDED_SEATS*row); // crea tribuna est
        JPanel sudPanel = createSeatsPanel(sudSeats, 2, row, WIDTH_SEATS+ADDED_SEATS*row); // crea tribuna sud
        JPanel ovestPanel = createSeatsPanel(ovestSeats, 3, row, HEIGHT_SEATS+ADDED_SEATS*row); // crea tribuna ovest
        seatsToDraw -= (nordSeats + sudSeats + estSeats + ovestSeats); // sedie rimanenti da rappresentare

        JPanel stadiumPanel = new JPanel();
        stadiumPanel.setLayout(new GridBagLayout());
        stadiumPanel.add(ovestPanel, c[0]);
        stadiumPanel.add(nordPanel, c[1]);
        stadiumPanel.add(estPanel, c[2]);
        stadiumPanel.add(sudPanel, c[3]);
        stadiumPanel.add(centerPanel, c[4]);
        
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.add(stadiumPanel);
        row++;

        return createStadiumPanel(wrapperPanel, seatsToDraw);
    }
    
    /**
     * Crea una fila di posti a sedere tramite pulsanti cliccabili, che permettono l'acquisto o la prenotazione di un biglietto
     * Un posto è rappresentato come verde se disponibile, giallo se già prenotato e rosso se già acquistato
     * @param seats il numero di posti effettivi da rappresentare
     * @param pos collocazione tribuna della fila (nord, est, sud, ovest)
     * @param n il numero identificativo del posto all'interno della fila
     * @param total il numero di posti massimi per la fila
     * @return la fila dei posti a sedere
     */
    private JPanel createSeatsPanel(int seats, int pos, int n, int total){
        JPanel seatsPanel = new JPanel();
        if(pos == 0 || pos == 2) 
            seatsPanel.setLayout(new GridLayout(1,total));
        else
            seatsPanel.setLayout(new GridLayout(total,1));
        
        if(seats==0){
            for(int i=0; i<total; i++){
                JButton seat = new JButton();
                seat.setPreferredSize(new Dimension(BUTTONSIZE, BUTTONSIZE));
                seat.setVisible(false); 
                seatsPanel.add(seat);
            }
        } else{ //disegna i posti
            for(int i=0; i<seats; i++){
                boolean found = false;
                JButton seat = new JButton();
                SeatPosition position = new SeatPosition(n, pos, i); // posizione del posto (numeroFila_collocazioneTribuna_numeroPosto)
                seat.addActionListener(new SeatButtonListener(position, seat)); 
                seat.setPreferredSize(new Dimension(BUTTONSIZE, BUTTONSIZE));
                for(int j=0; !found && j<tickets.size(); j++){ // scorri tutti i biglietti
                    if(tickets.get(j).getPosition().equals(position)){ // se il posto è prenotato/acquistato
                        found = true; // corrispondenza con un biglietto trovata
                        if(tickets.get(j).isOrdered()) // se ordinato colora rosso
                            seat.setBackground(Color.RED);
                        else 
                            seat.setBackground(Color.YELLOW);  // se prenotato colora giallo
                    }
                }
                if(!found) // se non c'è nessun biglietto prenotato o ordinato nel posto attuale colora verde
                    seat.setBackground(Color.GREEN);
                seatsPanel.add(seat);
            }
        }        
        return seatsPanel;
    }
    
    /**
     * Permette di acquistare o prenotare un biglietto nel posto scelto
     */
    private class SeatButtonListener implements ActionListener{
        /**
         * Costruisce il listener per il posto a sedere
         * @param position la collocazione del posto
         * @param seat posto a sedere cliccato
         */
        public SeatButtonListener(SeatPosition position, JButton seat){
            this.position = position;
            this.seat = seat;
        }
        public void actionPerformed(ActionEvent event){
            if(JOptionPane.showConfirmDialog(null, "Confermare l'operazione?", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                try{
                    if(!order){
                        sportsFacility.preorder(match, position);
                        seat.setBackground(Color.YELLOW);
                        JOptionPane.showMessageDialog(null, "Prenotazione effettuata con successo");
                    }
                    else{
                        try{ // se è prenotato prova ad acquistare
                            sportsFacility.pay(match, position);
                        }
                        catch(NoSuchElementException e){ // se il posto non è prenotato dall'utente
                            sportsFacility.order(match, position);
                        }
                        seat.setBackground(Color.RED);
                        JOptionPane.showMessageDialog(null, "Acquisto effettuato con successo");
                    }
                }
                catch(PostoIndisponibileException | IllegalArgumentException e){
                    JOptionPane.showMessageDialog(null, e.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private final JButton seat;
        private final SeatPosition position;
    }
    
    private final Match match; // partita da rappresentare
    private final SportsFacilityCustomer sportsFacility; // gestione sportiva su cui operare
    private final ArrayList<Ticket> tickets = new ArrayList<>();; // biglietti della partita
    private final boolean order; // acquisto o prenotazione
    private final GridBagConstraints[] c; // regole per disegnare le file
    private int row = 0; // numero dell'ultima fila [0,N]
    private final int WIDTH_SEATS = 31; // fila di sedie minima in orizzontale
    private final int HEIGHT_SEATS = 23; // fila di sedie minima in verticale
    private final int ADDED_SEATS = 2; // sedie aggiunte ad ogni fila
    private final int OFFSET = 5; // spazio di default tra una fila e l'altra
    private final int BUTTONSIZE = 10; // dimensione della sedia
}