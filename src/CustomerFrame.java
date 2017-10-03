import components.SportsFacilityCustomer;
import components.Ticket;
import components.Stadium;
import components.Match;
import components.SeatPosition;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Pannello di controllo per la modalità cliente
 * @author Roberto Tarullo, Pasquale Turi
 */
public class CustomerFrame extends JFrame{
    /**
     * Crea una finestra per la modalità cliente di GestioneSportiva
     * @param sportsFacilityObject la gestione sportiva da usare
     */
    protected CustomerFrame(SportsFacilityCustomer sportsFacilityObject){
        sportsFacility = sportsFacilityObject;
        
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        
        setLayout(new GridBagLayout());
        setTitle("Struttura sportiva (modalità utente)");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-FRAME_WIDTH/2, dim.height/2-FRAME_HEIGHT/2);
        
        // creo costraint per il GridBagLayout
        GridBagConstraints c1 = new GridBagConstraints(); 
        GridBagConstraints c2 = new GridBagConstraints(); 
        GridBagConstraints c3 = new GridBagConstraints(); 
        
        // Setta regole per matchPanel
        c1.fill = GridBagConstraints.BOTH;
        c1.weightx = 0.7;
        c1.weighty = 0.8;
        c1.gridx = 0;
        c1.gridy = 0;
        
        // aggiungo il pannello dei match
        add(createMatchPanel(sportsFacility.getMatches()), c1); 
        
        // seleziona la prima riga all'avvio
        if(sportsFacility.getMatches().isEmpty())
            state = -1;
        else{
            // aggiorno il match selezionato con quello della prima riga
            selectedMatchStadium = (int)(table.getValueAt(table.getSelectedRow(), 4));
            selectedMatchDate = (GregorianCalendar)(table.getValueAt(table.getSelectedRow(), 0));
            table.setRowSelectionInterval(0, 0); // seleziona la prima riga
            state = 0;
            table.getTableHeader().getColumnModel().getColumn(6).setHeaderValue("Posti disponibili");
            table.getTableHeader().repaint();
        }
        
        // setta regole per optionsPanel
        c2.fill = GridBagConstraints.VERTICAL;
        c2.insets = new Insets(0, 10, 0, 10);
        c2.ipadx = 0;
        
        // aggiungo il pannello delle opzioni
        add(createOptionsPanel(), c2);
        
        // setta regole per filterPanel
        c3.fill = GridBagConstraints.BOTH;
        c3.gridx = 0;
        c3.gridy = 1;
        c3.weightx = 0.05;
        c3.weighty = 0.02;
        add(createFilterPanel(), c3);
    }
    
    /**
     * Crea un modello di tabella tale che impedica di modificare le celle
     */
    public class NonEditableTableModel extends DefaultTableModel {
        public NonEditableTableModel(Object[][] tableData, Object[] colNames) {
           super(tableData, colNames);
        }
        @Override
        public boolean isCellEditable(int row, int column) {
           return false;
        }
    }
    
    /**
     * Crea il pannello in cui viene visualizzata la tabella che mostra le partite
     * @param matchesToDisplay lista delle partite da visualizzare inizialmente
     * @return il pannello con la tabella delle partite
     */
    private JScrollPane createMatchPanel(ArrayList<Match> matchesToDisplay){
        String[] columnNames = {"Data", "Ora", "In casa", "Ospite", "ID", "Stadio", "Posti disponibili", "Prezzo €"};
        Object[][] data = new Object[matchesToDisplay.size()][NUMBER_OF_COLUMNS];
        table = new JTable(new NonEditableTableModel(data, columnNames)); // aggiungi tutto ad una tabella non modificabile
        table.setAutoCreateRowSorter(true); // rende possibili gli ordinamenti delle partite nella tabella attraverso il metodo compareTo()
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // permette di selezionare solo una riga per volta
        table.setFocusable(false); // impedisce di selezionare le celle
        rewriteTable(matchesToDisplay);
        
        // crea regole per rappresentare in tabella date di tipo GregorianCalendar
        TableCellRenderer dateCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                if(value instanceof GregorianCalendar) { // se il valore è di tipo gregorian calendar
                    String dateAsString = "";
                    if(((GregorianCalendar)value).get(Calendar.DATE) < 10) // se il giorno ha una cifra rappresentalo con uno zero davanti
                        dateAsString += "0" + ((GregorianCalendar)value).get(Calendar.DATE);
                    else // se ha due cifre stampalo com'è
                        dateAsString += (((GregorianCalendar)value).get(Calendar.DATE));
                    if(((GregorianCalendar)value).get(Calendar.MONTH) < 9) // se il mese ha una cifra rappresentalo con uno zero davanti
                        dateAsString += "/0" + (((GregorianCalendar)value).get(Calendar.MONTH)+1);
                    else // se il mese ha due cifre
                        dateAsString += "/" + (((GregorianCalendar)value).get(Calendar.MONTH)+1);
                    dateAsString += "/" + ((GregorianCalendar)value).get(Calendar.YEAR);
                    value = dateAsString;
                }
                return super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
            }
        };
        table.getColumnModel().getColumn(0).setCellRenderer(dateCellRenderer); // imposta le regole appena definite sulla prima colonna
        
        // crea regole per rappresentare in tabella orari di tipo GregorianCalendar
        TableCellRenderer timeCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                if(value instanceof GregorianCalendar) { // se il valore inserito è di tipo gregorian calendar
                    String timeAsString = "";
                    if(((GregorianCalendar)value).get(Calendar.MINUTE) < 10) // aggiungi uno zero se il minuto ha una sola cifra
                        timeAsString += ((GregorianCalendar)value).get(Calendar.HOUR_OF_DAY) + ":" + "0" + ((GregorianCalendar)value).get(Calendar.MINUTE); 
                    else
                        timeAsString += ((GregorianCalendar)value).get(Calendar.HOUR_OF_DAY) + ":" + ((GregorianCalendar)value).get(Calendar.MINUTE);
                    
                    value = timeAsString;
                }
                return super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
            }
        };
        table.getColumnModel().getColumn(1).setCellRenderer(timeCellRenderer); // imposta le regole appena definite sulla seconda colonna
        
        // setta la larghezza iniziale delle colonne
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(35);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setPreferredWidth(90);
        
        JScrollPane scrollPanel = new JScrollPane(table); // crea scrollPanel e aggiungici la tabella creata
        scrollPanel.setBorder(new TitledBorder(new EtchedBorder(), "Partite")); // aggiungi bordo con titolo e contorno
        table.setFillsViewportHeight(true); // la tabella usa l'intera larghezza del container
        
        // Listener per la riga selezionata
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if(table.getSelectedRow() > -1){
                    selectedMatchDate = (GregorianCalendar)(table.getValueAt(table.getSelectedRow(), 0)); // memorizza data della partita selezionata
                    selectedMatchStadium = (int)(table.getValueAt(table.getSelectedRow(), 4));// memorizza stadio della partita selezionata
                    if(state==1 || state==2){
                        int row=-1, grandseat=-1, number=-1, j=0, rowLenght=-1;
                        String selectedPositionAsString = (String)(table.getValueAt(table.getSelectedRow(), 6));
                        for(int i=0; i<selectedPositionAsString.length(); i++){
                            if(selectedPositionAsString.charAt(i)=='-'){
                                if(j==0){
                                    row = Integer.parseInt(selectedPositionAsString.substring(0, i));
                                    rowLenght = i;
                                    j++;
                                }
                                else{
                                    grandseat = Integer.parseInt(selectedPositionAsString.substring(rowLenght+1, i));
                                    number = Integer.parseInt(selectedPositionAsString.substring(i+1));
                                }
                            }
                        }
                        selectedPosition = new SeatPosition(row, grandseat, number);
                    }
                }
                
            }
        });
        return scrollPanel;
    }
    
    /**
     * Crea pannello laterale dei pulsanti
     * @return il pannello laterale
     */
    private JPanel createOptionsPanel(){
        JPanel optionsPanel = new JPanel();
        
        optionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        exitButton = new JButton("Esci"); // creo pulsante
        class ExitButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                System.exit(0);
            }
        }
        exitButton.addActionListener(new ExitButtonListener()); // associo il pulsante al listener
        
        preordersButton = new JButton("Prenotazioni effettuate");
        class PreordersButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                ArrayList<Ticket> preorders = sportsFacility.getPreorders();
                ArrayList<Match> preorderedMatches = new ArrayList<>();

                for(int i=0; i < preorders.size(); i++)
                    preorderedMatches.add(preorders.get(i).getMatch());
                preorderButton.setText("Cancella prenotazione");
                if(preorderedMatches.isEmpty())
                    state = -1;
                else
                    state = 2;
                rewriteTable(preorderedMatches);
                table.getTableHeader().getColumnModel().getColumn(6).setHeaderValue("Posizione");
                table.getTableHeader().repaint();
            }
        }
        preordersButton.addActionListener(new PreordersButtonListener());
        
        ordersButton = new JButton("Acquisti effettuati");
        class OrdersButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                preorderButton.setText("Prenota");
                ArrayList<Match> orderedMatches = new ArrayList<>();

                for(int i=0; i < sportsFacility.getOrders().size(); i++)
                    orderedMatches.add(sportsFacility.getOrders().get(i).getMatch());
                if(orderedMatches.isEmpty())
                    state = -1;
                else
                    state = 1;
                rewriteTable(orderedMatches);
                table.getTableHeader().getColumnModel().getColumn(6).setHeaderValue("Posizione");
                table.getTableHeader().repaint();
            }
        }
        ordersButton.addActionListener(new OrdersButtonListener());
        
        viewMatchButton = new JButton("Visualizza tutte le partite");
        class viewMatchesButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                preorderButton.setText("Prenota");
                if(sportsFacility.getUpcomingMatches().isEmpty())
                    state=-1;
                else
                    state=0;
                rewriteTable(sportsFacility.getMatches());
                table.getTableHeader().getColumnModel().getColumn(6).setHeaderValue("Posti disponibili");
                table.getTableHeader().repaint();
            }
        } 
        viewMatchButton.addActionListener(new viewMatchesButtonListener());
        
        class OrderButtonListener implements ActionListener{
            public OrderButtonListener(boolean order){
                this.order = order;
            }
            public void actionPerformed(ActionEvent event){
                int i=0; 
                while(!(sportsFacility.getMatches().get(i).getStadium().getID() == selectedMatchStadium && sportsFacility.getMatches().get(i).getDate().equals(selectedMatchDate)))
                    i++;
                if(state == 0){
                    // Crea frame dello stadio generato
                    StadiumFrame orderFrame = new StadiumFrame(sportsFacility.getMatches().get(i), order, sportsFacility);
                    orderFrame.setTitle(sportsFacility.getMatches().get(i).getStadium().toString());
                    orderFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    orderFrame.setVisible(true);
                }
                else if(state==1 || state==-1)
                    JOptionPane.showMessageDialog(null, "Impossibile effettuare l'operazione", "Errore", JOptionPane.ERROR_MESSAGE);
                else if(state==2){
                    if(!order){
                        if(JOptionPane.showConfirmDialog(null, "Confermare l'operazione?", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                            sportsFacility.cancelPreorder(sportsFacility.getMatches().get(i), selectedPosition);
                            JOptionPane.showMessageDialog(null, "Cancellazione effettuata con successo");
                        }
                    }
                    else{
                        if(JOptionPane.showConfirmDialog(null, "Confermare l'operazione?", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                            sportsFacility.pay(sportsFacility.getMatches().get(i), selectedPosition);
                            JOptionPane.showMessageDialog(null, "Acquisto effettuato con successo");
                        }
                    }
                    // aggiorna la tabella
                    ArrayList<Ticket> preorders = sportsFacility.getPreorders();
                    ArrayList<Match> preorderedMatches = new ArrayList<>();
                    for(i=0; i < preorders.size(); i++)
                        preorderedMatches.add(preorders.get(i).getMatch());
                    rewriteTable(preorderedMatches);
                }
            }
            private final boolean order;
        }
        
        orderButton = new JButton("Acquista");
        orderButton.addActionListener(new OrderButtonListener(true));
        preorderButton = new JButton("Prenota");
        preorderButton.addActionListener(new OrderButtonListener(false));
        
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        optionsPanel.add(orderButton, c);
        c.gridy = 1;
        optionsPanel.add(preorderButton, c);
        c.gridy = 3;
        optionsPanel.add(preordersButton, c);
        c.gridy = 2;
        optionsPanel.add(ordersButton, c);
        c.gridy = 4;
        optionsPanel.add(viewMatchButton, c);
        c.gridy = 5;
        optionsPanel.add(exitButton, c);
        
        return optionsPanel;
    }
    
    /**
     * Aggiorna la tabella con un array di partite
     * @param matchesList lista delle partite da visualizzare nella tabella
     */
    private void rewriteTable(ArrayList<Match> matchesList){
        ((DefaultTableModel)table.getModel()).setRowCount(0); // cancella tutte le righe della tabella
        ((DefaultTableModel)table.getModel()).setRowCount(matchesList.size()); // setta il nuovo numero di righe
        for(int i=0; i<matchesList.size(); i++){ // riempi le righe per tutti i match
            table.getModel().setValueAt(matchesList.get(i).getDate(), i, 0);
            table.getModel().setValueAt(new GregorianCalendar(2006, 6, 6, matchesList.get(i).getDate().get(Calendar.HOUR_OF_DAY), matchesList.get(i).getDate().get(Calendar.MINUTE)), i, 1);
            table.getModel().setValueAt(matchesList.get(i).getHomeTeam(), i, 2);
            table.getModel().setValueAt(matchesList.get(i).getGuestTeam(), i, 3);
            table.getModel().setValueAt(matchesList.get(i).getStadium().getID(), i, 4);
            table.getModel().setValueAt(matchesList.get(i).getStadium().toString(), i, 5);
            if(state==1 || state==2){
                SeatPosition position;
                if(state==1)
                    position = sportsFacility.getOrders().get(i).getPosition();
                else
                    position = sportsFacility.getPreorders().get(i).getPosition();
                String positionAsString = position.toString();
                table.getModel().setValueAt(positionAsString, i, 6);
            }
            else
                table.getModel().setValueAt(matchesList.get(i).getAvailable(), i, 6);
            if(state==0 || state==2){
                if(sportsFacility.getBestDiscount(matchesList.get(i)) == 0)
                    table.getModel().setValueAt(new DecimalFormat("##.##").format(matchesList.get(i).getPrice()), i, 7);
                else{
                    double sconto = sportsFacility.getBestDiscount(matchesList.get(i));
                    String price = new DecimalFormat("##.##").format(sportsFacility.getBestPrice(matchesList.get(i))) + " (-" + new DecimalFormat("##.#").format(sconto) + "%)";
                    table.getModel().setValueAt(price, i, 7);
                }  
            }
            else if(state==1)
                table.getModel().setValueAt(new DecimalFormat("##.##").format(sportsFacility.getOrders().get(i).getOrderPrice()), i, 7);
        }
        if(!matchesList.isEmpty()) // se non c'è nessunaa partita da visualizzare
            table.setRowSelectionInterval(0, 0); // seleziona la prima riga
    }
    
    /**
     * Crea pannello delle opzioni
     * @return crea pannello delle opzioni
     */
    private JPanel createFilterPanel(){
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Filtra tutte le partite"));
        
        JLabel weekLabel = new JLabel("Settimana: ");
        weekField = new JTextField(7);
        weekField.setText("GG/MM/AAAA");
        filterWeekButton = new JButton("Filtra");
        class FilterWeekButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                int day, month, year;
                String dateAsString;
                preorderButton.setText("Prenota");
                try{
                    dateAsString = weekField.getText();
                    if(dateAsString.length()>10)
                        throw new IllegalArgumentException();
                    day = Integer.parseInt(dateAsString.substring(0, 2));
                    month = Integer.parseInt(dateAsString.substring(3, 5));
                    year = Integer.parseInt(dateAsString.substring(6, 10));
                    if(sportsFacility.getWeekMatches(day, month, year).isEmpty())
                        state = -1;
                    else
                        state = 0;
                    rewriteTable(sportsFacility.getWeekMatches(day, month, year));
                    table.getTableHeader().getColumnModel().getColumn(6).setHeaderValue("Posti disponibili");
                    table.getTableHeader().repaint();
                }
                catch(IllegalArgumentException | StringIndexOutOfBoundsException e){
                    System.out.println(e.toString());
                    JOptionPane.showMessageDialog(null, "Inserire una data valida nel formato GG/MM/AAAA", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        filterWeekButton.addActionListener(new FilterWeekButtonListener());
        
        JLabel stadiumLabel = new JLabel("Stadio: ");
        stadiumsCombo = new JComboBox();
        for(int i=0; i<sportsFacility.getStadiums().size(); i++)
            stadiumsCombo.addItem(sportsFacility.getStadiums().get(i));
        JButton filterStadiumsButton = new JButton("Filtra");
        class FilterStadiumsButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                preorderButton.setText("Prenota");
                Stadium selectedStadium = (Stadium)stadiumsCombo.getSelectedItem();
                if(sportsFacility.getStadiumMatches(selectedStadium).isEmpty())
                    state = -1;
                else
                    state = 0;
                rewriteTable(sportsFacility.getStadiumMatches(selectedStadium));
                table.getTableHeader().getColumnModel().getColumn(6).setHeaderValue("Posti disponibili");
                table.getTableHeader().repaint();
            }
        }
        filterStadiumsButton.addActionListener(new FilterStadiumsButtonListener());
        
        upcomingButton = new JButton("Solo non iniziate");
        class UpcomingMatchesButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                preorderButton.setText("Prenota");
                if(sportsFacility.getUpcomingMatches().isEmpty())
                    state=-1;
                else
                    state=0;
                rewriteTable(sportsFacility.getUpcomingMatches());
            }
        } 
        upcomingButton.addActionListener(new UpcomingMatchesButtonListener());
        
        panel.add(upcomingButton);
        panel.add(weekLabel);
        panel.add(weekField);
        panel.add(filterWeekButton);
        panel.add(stadiumLabel);
        panel.add(stadiumsCombo);
        panel.add(filterStadiumsButton);

        return panel;
    }
    
    private final SportsFacilityCustomer sportsFacility;
    private int selectedMatchStadium; // id dello stadio della partita selezionata
    private GregorianCalendar selectedMatchDate; // data della partita selezionata
    private SeatPosition selectedPosition;
    private JTable table;
    private JComboBox stadiumsCombo;
    private JTextField weekField;
    private JButton preorderButton;
    private JButton orderButton;
    private JButton viewMatchButton;
    private JButton filterWeekButton;
    private JButton preordersButton;
    private JButton ordersButton;
    private JButton exitButton;
    private JButton upcomingButton;
    private static final int FRAME_WIDTH = 830;
    private static final int FRAME_HEIGHT = 420;
    private final int NUMBER_OF_COLUMNS = 8;
    private int state = 0; // tiene traccia di cosa si sta visualizzando (0=tutte le partite, 1=acquisti, 2=prenotazioni, -1=lista vuota)
}