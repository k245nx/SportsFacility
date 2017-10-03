import components.SportsFacilityManager;
import components.exceptions.ScontoMiglioreException;
import components.Stadium;
import components.Match;
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
import java.util.NoSuchElementException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Finestra per il pannello di controllo per la modalità gestore
 * @author Roberto Tarullo, Pasquale Turi
 */
public class ManagerFrame extends JFrame{
    protected ManagerFrame(SportsFacilityManager sportsFacilityObject){
        sportsFacility = sportsFacilityObject;

        setLayout(new GridBagLayout());
        setTitle("Struttura sportiva (modalità gestore)");
        dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-FRAME_WIDTH/2, dim.height/2-FRAME_HEIGHT/2);
        
        // creo costraint per il layout
        GridBagConstraints c1 = new GridBagConstraints(); 
        GridBagConstraints c2 = new GridBagConstraints(); 
        
        // Setta regole per matchPanel
        c1.fill = GridBagConstraints.BOTH;
        c1.weightx = 0.7;
        c1.weighty = 0.8;
        c1.gridx = 0;
        c1.gridy = 0;
        
        add(createMatchPanel(sportsFacility.getMatches()), c1); // aggiungo il pannello dei match
        
        if(!sportsFacility.getMatches().isEmpty())
            table.setRowSelectionInterval(0, 0); // seleziona la prima riga
        
        // setta regole per optionsPanel
        c2.fill = GridBagConstraints.VERTICAL;
        c2.insets = new Insets(0, 10, 0, 10);
        c2.ipadx = 0;
        
        add(createOptionsPanel(), c2); // aggiungo il pannello delle opzioni
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }
    
    /**
     * Crea un modello di tabella tale che impedisca di modificare le celle
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
        table.setRowSelectionAllowed(false);
        table.setFocusable(false);
        rewriteTable();
        
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
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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
        
        return scrollPanel;
    }
    
    /**
     * Crea pannello laterale dei pulsanti di controllo
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
        
        addMatchButton = new JButton("Inserisci partita");
        class addMatchButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                JFrame addMatchFrame = createAddMatchFrame();
                addMatchFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                addMatchFrame.setVisible(true);
            }
        }
        addMatchButton.addActionListener(new addMatchButtonListener());
        
        enableDiscountButton = new JButton("Attiva sconto");
        class enableDiscountButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                JFrame discountFrame = createDiscountFrame();
                discountFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                discountFrame.setVisible(true);
            }
        }
        enableDiscountButton.addActionListener(new enableDiscountButtonListener());
        
        setPriceButton = new JButton("Assegna prezzo a stadio");
        class setPriceButtonnListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                JFrame stadiumPriceFrame = createStadiumPriceFrame();
                stadiumPriceFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                stadiumPriceFrame.setVisible(true);
            }
        } 
        setPriceButton.addActionListener(new setPriceButtonnListener());
        
        setCapacityButton = new JButton("Modifica capienza stadio");
        class setCapacityButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                JFrame capacityFrame = createSetCapacityFrame();
                capacityFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                capacityFrame.setVisible(true);
            }
        }
        setCapacityButton.addActionListener(new setCapacityButtonListener());
        
        incomeButton = new JButton("Visualizza incassi");
        class incomeButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                JFrame incomeFrame = createIncomeFrame();
                incomeFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                incomeFrame.setVisible(true);
            }
        }
        incomeButton.addActionListener(new incomeButtonListener());
        
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        optionsPanel.add(addMatchButton, c);
        
        c.gridy = 1;
        optionsPanel.add(enableDiscountButton, c);
        
        c.gridy = 3;
        optionsPanel.add(setPriceButton, c);
        
        c.gridy = 2;
        optionsPanel.add(setCapacityButton, c);
        
        c.gridy = 4;
        optionsPanel.add(incomeButton, c);
        
        c.gridy = 5;
        optionsPanel.add(exitButton, c);
        
        return optionsPanel;
    }
    
    /**
     * Aggiorna la tabella delle partite
     */
    private void rewriteTable(){
        ArrayList<Match> matchesList = sportsFacility.getMatches();
        ((DefaultTableModel)table.getModel()).setRowCount(0); // cancella tutte le righe della tabella
        ((DefaultTableModel)table.getModel()).setRowCount(matchesList.size()); // setta il nuovo numero di righe
        for(int i=0; i<matchesList.size(); i++){ // riempi le righe per tutti i match
            table.getModel().setValueAt(matchesList.get(i).getDate(), i, 0);
            table.getModel().setValueAt(new GregorianCalendar(2006, 6, 6, matchesList.get(i).getDate().get(Calendar.HOUR_OF_DAY), matchesList.get(i).getDate().get(Calendar.MINUTE)), i, 1);
            table.getModel().setValueAt(matchesList.get(i).getHomeTeam(), i, 2);
            table.getModel().setValueAt(matchesList.get(i).getGuestTeam(), i, 3);
            table.getModel().setValueAt(matchesList.get(i).getStadium().getID(), i, 4);
            table.getModel().setValueAt(matchesList.get(i).getStadium().toString(), i, 5);
            table.getModel().setValueAt(matchesList.get(i).getAvailable(), i, 6);
            table.getModel().setValueAt(new DecimalFormat("##.##").format(matchesList.get(i).getPrice()), i, 7);
        }
        if(!matchesList.isEmpty()) // se c'è almeno una partita da visualizzare
            table.setRowSelectionInterval(0, 0); // seleziona la prima riga
    }
    
    /**
     * Crea finestra per visualizzare gli incassi
     * @return la finestra degli incassi
     */
    private JFrame createIncomeFrame(){
        JFrame incomeFrame = new JFrame();
        incomeFrame.setSize(300, 150);
        incomeFrame.setLocation(dim.width/2-350/2, dim.height/2-170/2);
        incomeFrame.setTitle("Incasso");
        
        JLabel totalIncomeLabel = new JLabel("Incasso totale: €" + new DecimalFormat("##.##").format(sportsFacility.getIncome()));
        JComboBox stadiumComboBox = new JComboBox();
        for(int i=0; i<sportsFacility.getStadiums().size(); i++)
            stadiumComboBox.addItem(sportsFacility.getStadiums().get(i));
        JLabel stadiumIncomeLabel = new JLabel("Incasso stadio: €" + new DecimalFormat("##.##").format(sportsFacility.getStadiumIncome((Stadium)stadiumComboBox.getSelectedItem())));
        stadiumComboBox.addActionListener(
            new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    stadiumIncomeLabel.setText("Incasso stadio: €" + new DecimalFormat("##.##").format(sportsFacility.getStadiumIncome((Stadium)stadiumComboBox.getSelectedItem())));
                }
            }
        );
        incomeFrame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5,5,0,0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        incomeFrame.add(totalIncomeLabel, c);
        c.gridwidth = 1;
        c.gridy++;
        incomeFrame.add(stadiumIncomeLabel, c);
        c.gridx++;
        incomeFrame.add(stadiumComboBox, c);
        
        return incomeFrame;
    }
    
    /**
     * Crea finestra per modificare la capienza di uno stadio
     * @return la finestra di modifica della capienza
     */
    private JFrame createSetCapacityFrame(){
        JFrame capacityFrame = new JFrame();
        capacityFrame.setSize(350, 170);
        capacityFrame.setLocation(dim.width/2-350/2, dim.height/2-170/2);
        capacityFrame.setTitle("Modifica capacità stadio");
        
        JLabel stadiumLabel = new JLabel("Stadio: ");
        JComboBox stadiumComboBox = new JComboBox();
        JLabel capacityLabel = new JLabel("Quantità: ");
        JTextField capacityField = new JTextField(4);
        for(int i=0; i<sportsFacility.getStadiums().size(); i++)
            stadiumComboBox.addItem(sportsFacility.getStadiums().get(i));
        
        JButton addCapacityButton = new JButton("Aggiungi posti");
        JButton removeCapacityButton = new JButton("Rimuovi posti");
        class addCapacityButtonListener implements ActionListener{
            public addCapacityButtonListener(boolean type){
                this.type = type;
            }
            public void actionPerformed(ActionEvent event){
                try{
                    int diff;
                    if(Integer.parseInt(capacityField.getText())<=0)
                        throw new IllegalArgumentException("La quantità non può assumere valore negativo");
                    if(!type)
                        diff = Integer.parseInt(capacityField.getText()) - 2*Integer.parseInt(capacityField.getText());
                    else
                        diff = Integer.parseInt(capacityField.getText());
                    sportsFacility.setCapacity((Stadium)stadiumComboBox.getSelectedItem(), diff);
                    JOptionPane.showMessageDialog(null, "Capienza stadio modificata con successo");
                    rewriteTable();
                }
                catch(IllegalArgumentException e){
                    JOptionPane.showMessageDialog(null, e.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
            private final boolean type;
        }
        addCapacityButton.addActionListener(new addCapacityButtonListener(true));
        removeCapacityButton.addActionListener(new addCapacityButtonListener(false));
        
        capacityFrame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5,5,0,0);
        c.gridx = 0;
        c.gridy = 0;
        capacityFrame.add(stadiumLabel, c);
        c.gridx++;
        capacityFrame.add(stadiumComboBox, c);
        c.gridy++;
        c.gridx--;
        capacityFrame.add(capacityLabel, c);
        c.gridx++;
        capacityFrame.add(capacityField, c);
        c.gridy++;
        c.gridx--;
        capacityFrame.add(addCapacityButton, c);
        c.gridx++;
        capacityFrame.add(removeCapacityButton, c);
        
        return capacityFrame;
    }
    
    /**
     * Crea finestra per modificare il prezzo delle partite di uno stadio
     * @return la finestra di modifica del prezzo delle partite di uno stadio
     */
    private JFrame createStadiumPriceFrame(){
        JFrame capacityFrame = new JFrame();
        capacityFrame.setSize(310, 170);
        capacityFrame.setLocation(dim.width/2-310/2, dim.height/2-170/2);
        capacityFrame.setTitle("Modifica prezzo stadio");
        
        JLabel stadiumLabel = new JLabel("Stadio: ");
        JComboBox stadiumComboBox = new JComboBox();
        JLabel capacityLabel = new JLabel("Nuovo prezzo: ");
        JTextField capacityField = new JTextField(4);
        for(int i=0; i<sportsFacility.getStadiums().size(); i++)
            stadiumComboBox.addItem(sportsFacility.getStadiums().get(i));
        
        JButton setStadiumPriceButton = new JButton("Applica");
        class setPriceButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                try{
                    sportsFacility.setPrice((Stadium)stadiumComboBox.getSelectedItem(), Double.parseDouble(capacityField.getText()));
                    JOptionPane.showMessageDialog(null, "Prezzi delle partite modificati con successo");
                    rewriteTable();
                }
                catch(IllegalArgumentException | NoSuchElementException e){
                    JOptionPane.showMessageDialog(null, e.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        setStadiumPriceButton.addActionListener(new setPriceButtonListener());
        
        capacityFrame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5,5,0,0);
        c.gridx = 0;
        c.gridy = 0;
        capacityFrame.add(stadiumLabel, c);
        c.gridx++;
        capacityFrame.add(stadiumComboBox, c);
        c.gridy++;
        c.gridx--;
        capacityFrame.add(capacityLabel, c);
        c.gridx++;
        capacityFrame.add(capacityField, c);
        c.gridy++;
        c.gridx--;
        c.gridwidth=2;
        capacityFrame.add(setStadiumPriceButton, c);
        
        return capacityFrame;
    }
    
    /**
     * Crea finestra per applicare le politiche di sconto volute sulle partite
     * @return la finestra dell'attivazione degli sconti
     */
    private JFrame createDiscountFrame(){
        JFrame discountFrame= new JFrame();
        discountFrame.setSize(600, 260);
        discountFrame.setLocation(dim.width/2-600/2, dim.height/2-260/2);
        discountFrame.setTitle("Attiva politiche di sconto");
        
        JLabel discountLabel = new JLabel("Percentuale di sconto: ");
        JTextField discountField = new JTextField(4);
        
        JLabel matchLabel = new JLabel("Partita: ");
        JComboBox matchComboBox = new JComboBox();
        matchComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        for(int i=0; i<sportsFacility.getMatches().size(); i++)
            matchComboBox.addItem(sportsFacility.getMatches().get(i));
        JLabel stadiumLabel = new JLabel("Stadio: ");
        JComboBox stadiumComboBox = new JComboBox();
        for(int i=0; i<sportsFacility.getStadiums().size(); i++)
            stadiumComboBox.addItem(sportsFacility.getStadiums().get(i));
        JLabel partofdayLabel = new JLabel("Fascia giornaliera: ");
        JComboBox partofdayComboBox = new JComboBox();
        partofdayComboBox.setPrototypeDisplayValue("Pomeriggio");
        partofdayComboBox.addItem("Mattina");
        partofdayComboBox.addItem("Pomeriggio");
        partofdayComboBox.addItem("Sera");
        JLabel dayLabel = new JLabel("Giorno della settimana: ");
        JComboBox dayComboBox = new JComboBox();
        dayComboBox.setPrototypeDisplayValue("Mercoledì");
        dayComboBox.addItem("Lunedì");
        dayComboBox.addItem("Martedì");
        dayComboBox.addItem("Mercoledì");
        dayComboBox.addItem("Giovedì");
        dayComboBox.addItem("Venerdì");
        dayComboBox.addItem("Sabato");
        dayComboBox.addItem("Domenica");
        JLabel customerLabel = new JLabel("Categoria clienti: ");
        JTextField customerField = new JTextField(10);
        JButton activateMatchButton = new JButton("Attiva");
        class activateMatchButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                try{				
                    sportsFacility.enableDiscount((Match)matchComboBox.getSelectedItem(), Double.parseDouble(discountField.getText()));
                    JOptionPane.showMessageDialog(null, "Sconto applicato con successo");
                }
                catch(ScontoMiglioreException | IllegalArgumentException e){
                    JOptionPane.showMessageDialog(null, e.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        activateMatchButton.addActionListener(new activateMatchButtonListener());
        JButton activateStadiumButton = new JButton("Attiva");
        class activateStadiumButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                try{				
                    sportsFacility.enableDiscount((Stadium)stadiumComboBox.getSelectedItem(), Double.parseDouble(discountField.getText()));
                    JOptionPane.showMessageDialog(null, "Sconto applicato con successo");
                }
                catch(ScontoMiglioreException | IllegalArgumentException e){
                    JOptionPane.showMessageDialog(null, e.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        activateStadiumButton.addActionListener(new activateStadiumButtonListener());
        JButton activatePartofdayButton = new JButton("Attiva");
        class activatePartofdayButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                int partofday=-1;
                if(partofdayComboBox.getSelectedItem().equals("Mattina"))
                    partofday = 0;
                else if(partofdayComboBox.getSelectedItem().equals("Pomeriggio"))
                    partofday = 1;
                else if(partofdayComboBox.getSelectedItem().equals("Sera"))
                    partofday = 2;
                try{				
                    sportsFacility.enablePartOfTheDayDiscount(partofday, Double.parseDouble(discountField.getText()));
                    JOptionPane.showMessageDialog(null, "Sconto applicato con successo");
                }
                catch(ScontoMiglioreException | IllegalArgumentException e){
                    JOptionPane.showMessageDialog(null, e.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        activatePartofdayButton.addActionListener(new activatePartofdayButtonListener());
        JButton activateDayButton = new JButton("Attiva");
        class activateDayButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                int day=-1;
                if(dayComboBox.getSelectedItem().equals("Lunedì"))
                    day = 1;
                else if(dayComboBox.getSelectedItem().equals("Martedì"))
                    day = 2;
                else if(dayComboBox.getSelectedItem().equals("Mercoledì"))
                    day = 3;
                else if(dayComboBox.getSelectedItem().equals("Mercoledì"))
                    day = 4;
                else if(dayComboBox.getSelectedItem().equals("Giovedì"))
                    day = 5;
                else if(dayComboBox.getSelectedItem().equals("Sabato"))
                    day = 6;
                else if(dayComboBox.getSelectedItem().equals("Domenica"))
                    day = 0;
                try{				
                    sportsFacility.enableDayDiscount(day, Double.parseDouble(discountField.getText()));
                    JOptionPane.showMessageDialog(null, "Sconto applicato con successo");
                }
                catch(ScontoMiglioreException | IllegalArgumentException e){
                    JOptionPane.showMessageDialog(null, e.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        activateDayButton.addActionListener(new activateDayButtonListener());
        JButton activateCustomerButton = new JButton("Attiva");
        class activateCustomerButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                try{				
                    sportsFacility.enableDiscount(customerField.getText(), Double.parseDouble(discountField.getText()));
                    JOptionPane.showMessageDialog(null, "Sconto applicato con successo");
                }
                catch(ScontoMiglioreException | IllegalArgumentException | NullPointerException e){
                    JOptionPane.showMessageDialog(null, e.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        activateCustomerButton.addActionListener(new activateCustomerButtonListener());
        
        JPanel discountPanel = new JPanel();
        discountPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,0,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridx = c.gridy = 0;
        discountPanel.add(discountLabel, c);
        c.gridx++;
        discountPanel.add(discountField, c);
        
        c.gridx = 0;
        c.gridy++;
        discountPanel.add(matchLabel, c);
        c.gridx++;
        discountPanel.add(matchComboBox, c);
        c.gridx++;
        discountPanel.add(activateMatchButton, c);
        
        c.gridx = 0;
        c.gridy++;
        discountPanel.add(stadiumLabel, c);
        c.gridx++;
        discountPanel.add(stadiumComboBox, c);
        c.gridx++;
        discountPanel.add(activateStadiumButton, c);
        
        c.gridx = 0;
        c.gridy++;
        discountPanel.add(partofdayLabel, c);
        c.gridx++;
        discountPanel.add(partofdayComboBox, c);
        c.gridx++;
        discountPanel.add(activatePartofdayButton, c);
        
        c.gridx = 0;
        c.gridy++;
        discountPanel.add(dayLabel, c);
        c.gridx++;
        discountPanel.add(dayComboBox, c);
        c.gridx++;
        discountPanel.add(activateDayButton, c);
        
        c.gridx = 0;
        c.gridy++;
        discountPanel.add(customerLabel, c);
        c.gridx++;
        discountPanel.add(customerField, c);
        c.gridx++;
        discountPanel.add(activateCustomerButton, c);
        
        discountFrame.add(discountPanel);
        
        return discountFrame;
    }
    
    /**
     * Crea finestra per aggiungere una partita
     * @return la finestra di aggiunta partita
     */
    private JFrame createAddMatchFrame(){
        JFrame addMatchFrame= new JFrame();
        addMatchFrame.setSize(200, 400);
        addMatchFrame.setTitle("Inserisci partita");
        addMatchFrame.setLocation(dim.width/2-200/2, dim.height/2-400/2);
        JLabel stadiumLabel = new JLabel("Stadio: ");
        JComboBox stadiumComboBox = new JComboBox();
        for(int i=0; i<sportsFacility.getStadiums().size(); i++)
            stadiumComboBox.addItem(sportsFacility.getStadiums().get(i));
        JLabel dateLabel = new JLabel("Data: ");
        JTextField dateField = new JTextField(6);
        JLabel timeLabel = new JLabel("Ora: ");
        JTextField timeField = new JTextField(3);
        JLabel team1Label = new JLabel("Squadra in casa: ");
        JTextField team1Field = new JTextField(10);
        JLabel team2Label = new JLabel("Squadra ospite: ");
        JTextField team2Field = new JTextField(10);
        JLabel priceLabel = new JLabel("Prezzo: ");
        JTextField priceField = new JTextField(6);
        JButton okButton = new JButton("Inserisci");
        class okButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                int day, month, year, hour, minute;
                double price;
                try{
                    String dateAsString = dateField.getText();
                    if(dateAsString.length()!=10)
                        throw new IllegalArgumentException();
                    day = Integer.parseInt(dateAsString.substring(0, 2));
                    month = Integer.parseInt(dateAsString.substring(3, 5));
                    year = Integer.parseInt(dateAsString.substring(6, 10));

                    String timeAsString = timeField.getText();
                    if(timeAsString.length()!=5)
                        throw new IllegalArgumentException();
                    hour = Integer.parseInt(timeAsString.substring(0, 2));
                    minute = Integer.parseInt(timeAsString.substring(3, 5));

                    price = Double.parseDouble(priceField.getText());

                    sportsFacility.addMatch((Stadium)stadiumComboBox.getSelectedItem(), day, month, year, hour, minute, team1Field.getText(), team2Field.getText(), price);
                    JOptionPane.showMessageDialog(null, "Inserimento effettuato con successo");
                    addMatchFrame.setVisible(false);
                    rewriteTable();
                }
                catch(IllegalArgumentException | StringIndexOutOfBoundsException | NullPointerException e){
                    JOptionPane.showMessageDialog(null, e.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        okButton.addActionListener(new okButtonListener());

        JPanel addMatchPanel = new JPanel();
        addMatchPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,0,0);
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridy=0;
        addMatchPanel.add(stadiumLabel, c);
        c.gridy++;
        addMatchPanel.add(stadiumComboBox, c);
        c.gridy++;
        addMatchPanel.add(dateLabel, c);
        c.gridy++;
        addMatchPanel.add(dateField, c);
        c.gridy++;
        addMatchPanel.add(timeLabel, c);
        c.gridy++;
        addMatchPanel.add(timeField, c);
        c.gridy++;  
        addMatchPanel.add(team1Label, c);
        c.gridy++;
        addMatchPanel.add(team1Field, c);
        c.gridy++;
        addMatchPanel.add(team2Label, c);
        c.gridy++;
        addMatchPanel.add(team2Field, c);
        c.gridy++;
        addMatchPanel.add(priceLabel, c);
        c.gridy++;
        addMatchPanel.add(priceField, c);
        c.gridy++;
        c.insets = new Insets(10,5,0,0);
        addMatchPanel.add(okButton, c);
        addMatchFrame.add(addMatchPanel);
        
        return addMatchFrame;
    }
    
    private final SportsFacilityManager sportsFacility;
    private JTable table;
    private JButton addMatchButton;
    private JButton enableDiscountButton;
    private JButton setPriceButton;
    private JButton setCapacityButton;
    private JButton incomeButton;
    private JButton exitButton;
    private static final int FRAME_WIDTH = 780;
    private static final int FRAME_HEIGHT = 420;
    private final int NUMBER_OF_COLUMNS = 8;
    private final Dimension dim;
}