import components.SportsFacilityManager;
import components.SportsFacilityCustomer;
import components.Ticket;
import components.Stadium;
import components.Match;
import components.exceptions.PostoIndisponibileException;
import components.exceptions.ScontoMiglioreException;
import components.Customer;
import components.SeatPosition;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Consente di scegliere se avviare l'interfaccia a linea di comando di GestioneSportiva in modalità cliente o gestore.
 * Se si sceglie la modalità utente verrà chiesto il nome utente.
 * Se il nome utente non è registrato su file verrà chiesta e memorizzata la categoria dell'utente.
 * @author Roberto Tarullo, Pasquale Turi
 */
public class StarterCLI{
    public static void main(String[] args){
        final String path="C:\\Users\\Roberto Tarullo\\Desktop\\";
        
        String user;
        String customer=null;
        int choice = 0;
        boolean back = false;
        Scanner input = new Scanner(System.in);
        
	do{
            try{
                System.out.println("Selezionare modalità:\n(1) Cliente\n(2) Gestore");
                choice = input.nextInt();
            }
            catch(InputMismatchException e){
                input.nextLine();
            }
        } while(!(choice==1 || choice ==2));
        
        ///////////////////////////////////////
        //          MODALITà CLIENTE        //
        //////////////////////////////////////
        
        if(choice==1){
            System.out.print("Username: ");
            input.nextLine();
            user = input.nextLine();
               
            // cerca utente su file
            ArrayList<Customer> users = loadUsers(path); // carica gli utenti da file
            for(int i=0; customer==null && i<users.size(); i++){ // cerca tra gli utenti registrati una corrispondenza
                if(users.get(i).getUsername().equalsIgnoreCase(user))
                    customer = users.get(i).getType();
            }
            if(customer==null){ // se non è stata trovata alcuna corrispondenza
                System.out.print("Categoria cliente: ");
                customer = input.nextLine();
                saveUsers(path, users); // salva nuovo utente su file
            }
            
            SportsFacilityCustomer test = new SportsFacilityCustomer(path, user, customer);
            do{
                do{
                    try{
                        System.out.println("=================== MODALITA' CLIENTE ===================");
                        System.out.println("(1) Visualizza le partite programmate");
                        System.out.println("(2) Visualizza la lista delle partite non ancora iniziate");
                        System.out.println("(3) Visualizza i dettagli di una determinata partita");
                        System.out.println("(4) Visualizza le prenotazioni effettuate");
                        System.out.println("(5) Visualizza gli acquisti effettuati");
                        System.out.println("(6) Cancella la prenotazione di una partita");
                        System.out.println("(7) Esci");
                        choice = input.nextInt();
                    }
                    catch(InputMismatchException e){
                        input.nextLine();
                    }
                } while(!(choice <= 7 && choice > 0 ));
		
                if(choice == 1){ // Visualizza le partite programmate
                    do{
                        do{
                            try{
                                System.out.println("(1) Visualizza tutte le partite programmate in una determinata settimana");
                                System.out.println("(2) Visualizza tutte le partite che si svolgono in un determinato stadio");
                                System.out.println("(3) Torna al menu principale");
                                choice = input.nextInt();
                            }
                            catch(InputMismatchException e){
                                input.nextLine();
                                choice = 0;
                            }
                        } while(choice < 1 || choice > 3);
                        if(choice == 1){ // Visualizza tutte le partite programmate in una determinata settimana
                            int day=0, month=0, year=0;
                            String dateAsString;
                            ArrayList<Match> weekMatches = null;

                            do{
                                System.out.print("Inserire la data del primo giorno della settimana (DD/MM/YYYY): ");
                                dateAsString = input.next();
                                try{
                                    day = Integer.parseInt(dateAsString.substring(0, 2));
                                    month = Integer.parseInt(dateAsString.substring(3, 5));
                                    year = Integer.parseInt(dateAsString.substring(6, 10));
                                    weekMatches = test.getWeekMatches(day, month, year);
                                }
                                catch (IllegalArgumentException e){
                                    System.out.println("ERROR: integers excepted in format (DD/MM/YYYY)");
                                }
                            } while(weekMatches == null);

                            if(weekMatches.isEmpty())
                                System.out.println("\nNessuna partita trovata nella settimana a partire dal " + dateAsString + "\n");
                            else{
                                for(int i=0; i < weekMatches.size(); i++)
                                    System.out.println(i+1 + "\t" + test.getMatchInfo(weekMatches.get(i)));
                                System.out.println();
                            }
                        }
                        else if(choice == 2){ // Visualizza tutte le partite che si svolgono in un determinato stadio
                            System.out.println("Selezionare stadio:");
                            Stadium s;
                            do{
                                for(int i=0; i < test.getStadiums().size(); i++) // stampa un elenco degli stadi
                                    System.out.println("(" + (i+1) + ")\t" + test.getStadiums().get(i).toString());
                                try{
                                    choice = input.nextInt();
                                }
                                catch(InputMismatchException e){
                                    System.out.println("Invalid choice");
                                    input.nextLine();
                                    choice = 0;
                                }
                            } while(choice < 1 || choice > test.getStadiums().size());
                            
                            s = test.getStadiums().get(choice - 1); // seleziona lo stadio scelto
                            
                            ArrayList<Match> stadiumMatches = test.getStadiumMatches(s); // recupera tutte le partite dello stadio scelto
                            
                            if(stadiumMatches.isEmpty()) // Se non c'è nessuna partita
                                System.out.println("Nessuna partita trovata nello stadio " + s.toString() + "\n");
                            else{ // altrimenti stampa tutte le partite dello stadio
                                for(int i=0; i < stadiumMatches.size(); i++)
                                    System.out.println(i+1 + "\t" + test.getMatchInfo(stadiumMatches.get(i)));
                                System.out.println();
                            }
                        }
                        else if(choice == 3)
                            back = true;
                    } while(!back);
                }
                else if(choice == 2){ // Visualizza la lista delle partite non ancora iniziate
                    do{
                        do{
                            System.out.println("(1) Visualizza in ordine cronologico");
                            System.out.println("(2) Visualizza in ordine crescente rispetto all’identificativo di stadio");
                            System.out.println("(3) Visualizza in ordine lessicografico crescente rispetto al nome delle squadre che si affrontano");
                            System.out.println("(4) Torna al menu principale");
                            try{
                                choice = input.nextInt();
                            }
                            catch(InputMismatchException e){
                                input.nextLine();
                                choice = 0;
                            }
                        }
                        while(choice < 1 || choice > 4);

                        if(choice == 1){ // ordine cronologico
                            ArrayList<Match> matchlist = test.getUpcomingMatches();
                            if(matchlist.isEmpty())
                                System.out.println("Nessuna partita trovata\n");
                            else{
                                for(int i=0; i < matchlist.size(); i++)
                                    System.out.println(i+1 + "\t" + test.getMatchInfo(matchlist.get(i)));
                                System.out.println();
                            }					
                        }
                        else if(choice == 2){ //ordine identificativo di stadio
                            ArrayList<Match> matchlist = test.getStadiumSortedMatches();
                            if(matchlist.isEmpty())
                                System.out.println("Nessuna partita trovata\n");
                            else{
                                for(int i=0; i < matchlist.size(); i++)
                                    System.out.println("ID stadio: " + matchlist.get(i).getStadium().getID() + "\t" + test.getMatchInfo(matchlist.get(i)));
                                System.out.println();
                            }
                        }
                        else if(choice == 3){ // Ordine lessicografico crescente rispetto al nome delle squadre che si affrontano
                            ArrayList<Match> matchlist = test.getLexicographicallySortedMatches();
                            if(matchlist.isEmpty())
                                System.out.println("Nessuna partita trovata\n");
                            else{
                                for(int i=0; i < matchlist.size(); i++)
                                    System.out.println((i+1) + "\t" + test.getMatchInfo(matchlist.get(i)));
                                System.out.println();
                            }
                        }
                        else if(choice == 4)
                            back = true;
                    } while(!back);
                }
                else if(choice == 3){ //  Visualizza i dettagli di una determinata partita
                    ArrayList<Match> upcomingMatches = test.getUpcomingMatches();
                    int index = -1;

                    if(upcomingMatches.isEmpty())
                        System.out.println("Nessuna partita ancora non iniziata trovata\n");
                    else{
                        for(int i=0; i < upcomingMatches.size(); i++)
                            System.out.println("(" + (i+1) + ")" + "\t" + upcomingMatches.get(i).toString());
                        do{
                            System.out.print("\nSelezionare una partita: ");
                            try{
                                index = input.nextInt() - 1;
                            }
                            catch(InputMismatchException e){
                                input.nextLine();
                            }
                        }
                        while(index < 0 || index > upcomingMatches.size()-1);
                        do{
                            do{
                                System.out.println(test.getMatchInfo(upcomingMatches.get(index)));
                                System.out.println("Posti disponibili: " + upcomingMatches.get(index).getAvailable() + "\n");
                                System.out.println("(1) Prenota un biglietto");
                                System.out.println("(2) Paga un biglietto già prenotato");
                                System.out.println("(3) Acquista un biglietto senza prenotazione");
                                System.out.println("(4) Torna al menu principale");
                                try{
                                    choice = input.nextInt();
                                }
                                catch(InputMismatchException e){
                                    input.nextLine();
                                }
                            }
                            while(choice < 1 || choice > 4);
                            int row=-1, grandseat=-1, number=-1;
                            if(choice==1 || choice==3){
                                int rows; // numero di file
                                int drawnSeats = (31+23)*2; // numero minimo di posti da rappresentare
                                for(rows=1; drawnSeats < upcomingMatches.get(index).getStadium().getCapacity(); rows++)
                                    drawnSeats += (31+23)*2 + 8*rows; // numero delle sedie rappresentate
                                System.out.print("Fila: [1-" + rows + "]: ");
                                row = input.nextInt() - 1;
                                input.nextLine();
                                String tribuna;
                                do{
                                    System.out.print("Tribuna: [NORD | SUD | EST | OVEST]: ");
                                    tribuna = input.nextLine();
                                } while(!tribuna.equalsIgnoreCase("nord") && !tribuna.equalsIgnoreCase("sud") && !tribuna.equalsIgnoreCase("est") && !tribuna.equalsIgnoreCase("ovest"));
                                if(tribuna.equalsIgnoreCase("nord"))
                                    grandseat = 0;
                                else if(tribuna.equalsIgnoreCase("est"))
                                    grandseat = 1;
                                else if(tribuna.equalsIgnoreCase("sud"))
                                    grandseat = 2;
                                else if(tribuna.equalsIgnoreCase("ovest"))
                                    grandseat = 3;
                                System.out.print("Posto: [1-" + (upcomingMatches.get(index).getStadium().getCapacity()) + "]: ");
                                number = input.nextInt() - 1;
                            }
                            if(choice == 1){ // prenota un biglietto
                                do{
                                    System.out.println("Confermare prenotazione?");
                                    System.out.println("(1) Sì");
                                    System.out.println("(2) No");
                                    try{
                                        choice = input.nextInt();
                                    }
                                    catch(InputMismatchException e){
                                        input.nextLine();
                                    }
                                }
                                while(!(choice == 1 || choice == 2));

                                if(choice == 1){
                                    try{
                                        test.preorder(upcomingMatches.get(index), new SeatPosition(row, grandseat, number));
                                        System.out.println("Prenotazione effettuata con successo");
                                    }
                                    catch(PostoIndisponibileException | IllegalArgumentException | NullPointerException e){
                                        System.out.println(e.toString());
                                    }
                                    back = true;
                                }
                            }
                            else if(choice == 2){ // Paga un biglietto prenotato
                                do{
                                    System.out.println("Confermare acquisto?");
                                    System.out.println("(1) Sì");
                                    System.out.println("(2) No");
                                    try{
                                        choice = input.nextInt();
                                    }
                                    catch(InputMismatchException e){
                                        input.nextLine();
                                    }
                                }
                                while(!(choice == 1 || choice == 2));
                                if(choice == 1){
                                    try{
                                        test.pay(upcomingMatches.get(index), new SeatPosition(row, grandseat, number));
                                        System.out.println("Acquisto effettuato con successo");
                                    }
                                    catch(NoSuchElementException e){
                                        System.out.println(e.toString());
                                    }
                                    back = true;
                                }
                            }
                            else if(choice == 3){ // Acquista un biglietto
                                do{
                                    System.out.println("Confermare acquisto?");
                                    System.out.println("(1) Sì");
                                    System.out.println("(2) No");
                                    try{
                                        choice = input.nextInt();
                                    }
                                    catch(InputMismatchException e){
                                        input.nextLine();
                                    }
                                }
                                while(!(choice == 1 || choice == 2));
                                if(choice == 1){
                                    try{
                                        test.order(upcomingMatches.get(index), new SeatPosition(row, grandseat, number));
                                        System.out.println("Acquisto effettuato con successo");
                                    }
                                    catch(PostoIndisponibileException | IllegalArgumentException | NullPointerException e){
                                        System.out.println(e.toString());
                                    }
                                    back = true;
                                }
                            }
                            else if(choice == 4)
                                back = true;
                        } while(!back);
                    }
                }
                else if(choice == 4){ // visualizza le prenotazioni effettuate
                    ArrayList<Ticket> preorders = test.getPreorders();
                    if(preorders.isEmpty())
                        System.out.println("Nessuna prenotazione effettuata\n");
                    else{
                        System.out.println("Prenotazioni effettuate:");
                        for(int i=0; i < preorders.size(); i++)
                            System.out.println(test.getMatchInfo(preorders.get(i).getMatch()));
                        System.out.println();
                    }
                }
                else if(choice == 5){ // visualizzare gli acquisti effettuati
                    ArrayList<Ticket> orders = test.getOrders();
                    if(orders.isEmpty())
                        System.out.println("Nessun acquisto effettuato\n");
                    else{
                        double totalPrice = 0;
                        System.out.println("Acquisti effettuati:");
                        for(int i=0; i < orders.size(); i++){
                            System.out.println(i+1 + "\t" + orders.get(i).getMatch().toString() + " (Subtotale: €" + new DecimalFormat("##.##").format(orders.get(i).getOrderPrice()) + ")");
                            totalPrice += orders.get(i).getOrderPrice();
                        }
                        System.out.println("Totale: €" + new DecimalFormat("##.##").format(totalPrice) + "\n");
                    }
                }
                else if(choice == 6){ // cancellare la prenotazione di una partita;
                    ArrayList<Ticket> preorders = test.getPreorders();
                    if(preorders.isEmpty())
                        System.out.println("Nessuna partita prenotata\n");
                    else{
                        do{
                            int i;
                            System.out.println("Selezionare la prenotazione da cancellare:");
                            for(i=0; i < preorders.size(); i++)
                                System.out.println("(" + (i+1) + ")\t" + test.getMatchInfo(preorders.get(i).getMatch()));
                            System.out.println("(" + (i+1) + ")\tTorna al menu principale");
                            try{
                                choice = input.nextInt();
                            }
                            catch(InputMismatchException e){
                                input.nextLine();
                            }
                        } while(choice < 1 || choice > preorders.size()+1);
                        if(choice != preorders.size()+1)
                            test.cancelPreorder(preorders.get(choice-1).getMatch(), preorders.get(choice-1).getPosition());
                    }
                }
                else if(choice == 7)
                    break;
                back = false;
            } while(!back);
        }
			
	///////////////////////////////////////
	//          MODALITà GESTORE        //
	//////////////////////////////////////
			
	else if(choice==2){
            SportsFacilityManager test = new SportsFacilityManager(path);
            do{
                do{
                    try{
                        System.out.println("======================= MODALITA' GESTORE =======================");
                        System.out.println("(1) Inserisci partita nel calendario delle partite programmate");
                        System.out.println("(2) Visualizza un elenco ordinato delle partite");
                        System.out.println("(3) Attiva politiche di sconto sui biglietti");
                        System.out.println("(4) Assegna un prezzo alle partite che si svolgono in un dato stadio");
                        System.out.println("(5) Aumenta o riduci la capienza degli stadi");
                        System.out.println("(6) Visualizza l’incasso");
                        System.out.println("(7) Esci");	
                        choice = input.nextInt();
                    }
                    catch(InputMismatchException e){
                        input.nextLine();
                    }
                } while(!(choice >= 1 && choice <= 7));

                if(choice == 1){ // Inserisci partita nel calendario delle partite programmate
                    int day=0, month=0, year=0, hour=0, minute=0, index=-1;
                    double price=-1;
                    String dateAsString, timeAsString;
                    boolean inputok = false;
                    
                    do{
                        try{
                            System.out.println("Selezionare stadio: ");
                            for(int i=0; i < test.getStadiums().size(); i++) // produce un elenco di tutti gli stadi
                                System.out.println("(" + (i+1) + ")\t" + test.getStadiums().get(i).toString());
                            index = input.nextInt() - 1;
                        }
                        catch(InputMismatchException e){
                            input.nextLine();
                        }
                    } while(index < 0 || index > test.getStadiums().size()-1);
                    Stadium s = test.getStadiums().get(index);
                    do{
                        do{
                            System.out.print("Data inserimento (DD/MM/YYYY): ");
                            try{
                                dateAsString = input.next();
                                if(dateAsString.length()>10)
                                    throw new IllegalArgumentException();
                                day = Integer.parseInt(dateAsString.substring(0, 2));
                                month = Integer.parseInt(dateAsString.substring(3, 5));
                                year = Integer.parseInt(dateAsString.substring(6, 10));
                                inputok = true;
                            }
                            catch(IllegalArgumentException | StringIndexOutOfBoundsException e){
                                System.out.println("Inserire una data valida nel formato GG/MM/AAAA\n");
                            }
                        } while(!inputok);

                        inputok=false;
                        do{
                            System.out.print("Ora inserimento (HH:MM): ");
                            try{
                                timeAsString = input.next();
                                if(timeAsString.length()>5)
                                    throw new IllegalArgumentException();
                                hour = Integer.parseInt(timeAsString.substring(0, 2));
                                minute = Integer.parseInt(timeAsString.substring(3, 5));
                                inputok = true;
                            }
                            catch(IllegalArgumentException | StringIndexOutOfBoundsException e){
                                System.out.println("Inserire un orario valido nel formato HH:MM\n");
                            }
                        } while(!inputok);
                        input.nextLine();
                        System.out.print("Squadra in casa: ");
                        String team1 = input.nextLine();
                        System.out.print("Squadra ospite: ");
                        String team2 = input.nextLine();
                        System.out.print("Prezzo: ");
                        inputok=false;
                        do{
                            try{
                                price = input.nextDouble();
                                inputok=true;
                            }
                            catch(InputMismatchException e){
                                input.nextLine();
                                System.out.print("Please enter a number: ");
                            }
                        } while(!inputok);
                        inputok = false;
                        try{
                            test.addMatch(s, day, month, year, hour, minute, team1, team2, price);
                            inputok = true;
                        }
                        catch(IllegalArgumentException | NullPointerException e){
                            System.out.println(e.toString());
                        }
                    } while(!inputok);
                    System.out.println("\nInserimento partita effettuato con successo!\n");
                }
                else if(choice == 2){ // Visualizza un elenco ordinato delle partite
                    do{
                        System.out.println("(1) Visualizza in ordine cronologico");
                        System.out.println("(2) Visualizza in base alla capienza degli stadi");
                        System.out.println("(3) Torna al menu principale");
                        try{
                            choice = input.nextInt();
                        }
                        catch(InputMismatchException e){
                            input.nextLine();
                        }
                    }
                    while(!(choice >= 1 || choice <= 3));
                    if(choice == 2){ // in base alla capienza degli stadi
                        ArrayList<Match> sortedMatches = test.getCapacitySortedMatches();
                        if(sortedMatches.isEmpty())
                            System.out.println("Nessuna partita trovata\n");
                        else{
                            for(int i=0; i < sortedMatches.size(); i++)
                                System.out.println("Capienza: " + sortedMatches.get(i).getStadium().getCapacity() + "\t" + test.getMatchInfo(sortedMatches.get(i)));
                            System.out.println();
                        }
                    }
                    else if(choice == 1){ // in ordine cronologico
                        ArrayList<Match> sortedMatches = test.getDateSortedMatches();
                        if(sortedMatches.size() == 0)
                            System.out.println("Nessuna partita trovata\n");
                        else{
                            for(int i=0; i < sortedMatches.size(); i++)
                                System.out.println(i+1 + "\t" + test.getMatchInfo(sortedMatches.get(i)));
                            System.out.println();
                        }
                    }
                }
                else if(choice == 3){ // attivare politiche di sconto sui biglietti;
                    do{
                        try{
                            System.out.println("(1) Attiva per una singola partita"); 
                            System.out.println("(2) Attiva per tutte le partite di uno stadio"); 
                            System.out.println("(3) Attiva per le partite in una fascia giornaliera");
                            System.out.println("(4) Attiva per le partite di uno o più giorni della settimana");
                            System.out.println("(5) Attiva per una categoria di clienti"); 
                            System.out.println("(6) Torna al menu principale"); 
                            choice = input.nextInt();
                        }
                        catch(InputMismatchException e){
                            input.nextLine();
                        }
                    }
                    while(!(choice >= 1 && choice <= 6));

                    if(choice == 1){ // Attiva per una singola partita
                        if(test.getMatches().isEmpty())
                            System.out.println("Nessuna partita trovata\n");
                        else{
                            int index;
                            System.out.println("Selezionare una partita:");
                            do{
                                for(int i=0; i < test.getMatches().size(); i++)
                                    System.out.println("(" + (i+1) + ")" + "\t" + test.getMatchInfo(test.getMatches().get(i)));
                                index = input.nextInt()-1;
                            }
                            while(index < 0 || index > test.getMatches().size()-1);

                            System.out.print("Percentuale di sconto: ");
                            double p = input.nextDouble();
                            try{				
                                test.enableDiscount(test.getMatches().get(index), p);
                                System.out.println("\nSconto applicato con successo\n");
                            }
                            catch(ScontoMiglioreException | IllegalArgumentException e){
                                System.out.println(e.toString());
                            }
                        }
                    }
                    else if(choice == 2){ // Attiva per tutte le partite di uno stadio
                        int index;
                        do{
                            for(int i=0; i < test.getStadiums().size(); i++)
                                System.out.println("(" + (i+1) + ")" + "\t" + test.getStadiums().get(i).toString());
                            index = input.nextInt()-1;
                        } while(index < 0 || index > test.getStadiums().size()-1);
                        System.out.print("Percentuale di sconto: ");
                        double p = input.nextDouble();
                        try{
                            test.enableDiscount(test.getStadiums().get(index), p);
                            System.out.println("\nSconto applicato con successo\n");
                        }
                        catch(ScontoMiglioreException | IllegalArgumentException e){
                            System.out.println(e.toString());
                        }
                    }
                    else if(choice == 3){ // Attiva per le partite in una fascia giornaliera
                        boolean repeat = false;
                        do{
                            do{
                                System.out.println("Selezionare fascia giornaliera: ");
                                System.out.println("(1) Mattina (6:00-11:59)");
                                System.out.println("(2) Pomeriggio (12:00-17:59)");
                                System.out.println("(3) Sera (18:00-23:59)");
                                try{
                                    choice = input.nextInt();
                                }
                                catch(InputMismatchException e){
                                    input.nextLine();
                                }
                            } while(choice<1 || choice >3);
                            System.out.print("Percentuale di sconto: ");
                            double p = input.nextDouble();
                            try{
                                test.enablePartOfTheDayDiscount(choice, p);
                                System.out.println("\nSconto applicato con successo\n");
                            }
                            catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e){
                                System.out.println(e.toString());
                                repeat = true;
                            }
                            catch(ScontoMiglioreException e){
                                System.out.println(e.toString());
                            }
                        } while(repeat);
                    }
                    else if(choice == 4){ // Attiva per le partite di uno o più giorni della settimana
                        boolean[] week = new boolean[7];
                        do{
                            System.out.println("Scegliere giorni:");
                            System.out.print("(1) Lunedì");
                            if(week[1])
                                System.out.println(" x");
                            else
                                System.out.println();
                            System.out.print("(2) Martedì");
                            if(week[2])
                                System.out.println(" x");
                            else
                                System.out.println();
                            System.out.print("(3) Mercoledì");
                            if(week[3])
                                System.out.println(" x");
                            else
                                System.out.println();
                            System.out.print("(4) Giovedì");
                            if(week[4])
                                System.out.println(" x");
                            else
                                System.out.println();
                            System.out.print("(5) Venerdì");
                            if(week[5])
                                System.out.println(" x");
                            else
                                System.out.println();
                            System.out.print("(6) Sabato");
                            if(week[6])
                                System.out.println(" x");
                            else
                                System.out.println();
                            System.out.print("(7) Domenica");
                            if(week[0])
                                System.out.println(" x");
                            else
                                System.out.println("");
                            System.out.println("(8) Fatto");

                            choice = input.nextInt();
                            
                            if(choice == 1 && !week[1])
                                week[1] = true;
                            else if(choice == 1 && week[1])
                                week[1] = false;
                            else if(choice == 2 && !week[2])
                                week[2] = true;
                            else if(choice == 2 && week[2])
                                week[2] = false;
                            else if(choice == 3 && !week[3])
                                week[3] = true;
                            else if(choice == 3 && week[3])
                                week[3] = false;
                            else if(choice == 4 && !week[4])
                                week[4] = true;
                            else if(choice == 4 && week[4])
                                week[4] = false;
                            else if(choice == 5 && !week[5])
                                week[5] = true;
                            else if(choice == 5 && week[5])
                                week[5] = false;
                            else if(choice == 6 && !week[6])
                                week[6] = true;
                            else if(choice == 6 && week[6])
                                week[6] = false;
                            else if(choice == 7 && !week[0])
                                week[0] = true;
                            else if(choice == 7 && week[0])
                                week[0] = false;
                        } while(choice != 8);
                        System.out.print("Percentuale di sconto: ");
                        double p = input.nextDouble();
                        for(int i=0; i<7; i++){
                            if(week[i]){
                                try{
                                    test.enableDayDiscount(i, p);
                                    System.out.println("Sconto applicato con successo!");
                                }
                                catch(ScontoMiglioreException | IllegalArgumentException | ArrayIndexOutOfBoundsException e){
                                    System.out.println(e.toString());
                                }
                            }
                        }
                    }
                    else if(choice == 5){ // Attiva per una categoria di clienti
                        System.out.print("Categoria cliente: ");
                        input.nextLine();
                        String str = input.nextLine();
                        System.out.print("Percentuale di sconto: ");
                        double p = input.nextDouble();
                        try{
                            test.enableDiscount(str, p);
                            System.out.println("Sconto applicato con successo!");
                        }
                        catch(ScontoMiglioreException | IllegalArgumentException e){
                            System.out.println(e.toString());
                        }
                    }
                }
                else if(choice == 4){ // assegnare un prezzo alle partite che si svolgono in un determinato stadio
                    System.out.println("Seleziona stadio:");
                    for(int i=0; i < test.getStadiums().size(); i++) // produce un elenco di tutti gli stadi
                        System.out.println("(" + (i+1) + ")\t" + test.getStadiums().get(i).toString());
                        int index = input.nextInt()-1;
                        System.out.print("Nuovo prezzo: ");
                        double price = input.nextDouble();
                        try{
                            test.setPrice(test.getStadiums().get(index), price);
                            System.out.println("Prezzo aggiornato con successo per tutte le partite dello stadio");
                        }
                        catch(IllegalArgumentException | NoSuchElementException e){
                            System.out.println(e.toString());
                        }
                }
                else if(choice == 5){ // aumentare o ridurre la capienza degli stadi;
                    System.out.println("Selezionare quale stadio modificare:");
                    int index, n;
                    do{
                        for(int i=0; i < test.getStadiums().size(); i++) // produce un elenco di tutti gli stadi
                            System.out.println("(" + (i+1) + ")\t" + test.getStadiums().get(i).toString() + " (Capienza: " + test.getStadiums().get(i).getCapacity() + " posti)");
                        index = input.nextInt()-1;
                    }
                    while(index < 0 || index > test.getStadiums().size()-1);
                    
                    do{
                        System.out.println("(1) Aggiungi posti");
                        System.out.println("(2) Rimuovi posti");
                        System.out.println("(3) Annulla e torna al menu principale");
                        choice = input.nextInt();
                    }while(!(choice == 1 || choice == 2 || choice == 3));

                    if(choice == 1){
                        do{
                            System.out.print("Inserisci una quantità: ");
                            n = input.nextInt();
                        } while(n<1);
                        test.setCapacity(test.getStadiums().get(index), Math.abs(n));
                        System.out.println("\nLo stadio " + test.getStadiums().get(index).toString() + " ha ora " + test.getStadiums().get(index).getCapacity() + " posti\n");
                    }
                    else if(choice == 2){
                        do{
                            System.out.print("Inserisci una quantità: ");
                            n = input.nextInt();
                        } while(n<1);
                        try{
                            test.setCapacity(test.getStadiums().get(index), Math.abs(n) - 2*Math.abs(n));
                            System.out.println("\nLo stadio " + test.getStadiums().get(index).toString() + " ha ora " + test.getStadiums().get(index).getCapacity() + " posti\n");
                        }
                        catch(IllegalArgumentException e){
                            System.out.println(e.toString());
                            System.out.println("\nLo stadio " + test.getStadiums().get(index).toString() + " non è stato modificato.\n");
                        }
                    }
                }
                else if(choice == 6){ // visualizzare l'incasso
                    do{
                        System.out.println("(1) Visualizza l'incasso totale");
                        System.out.println("(2) Visualizza l'incasso per ogni stadio");
                        System.out.println("(3) Torna al menu principale");

                        choice = input.nextInt();
                        if(choice == 1) // visualizza incasso totale
                        System.out.println("Incasso totale: " + test.getIncome() + "\n");
                        else if (choice == 2){ // visualizza incasso per ogni stadio
                            for(int i=0; i < test.getStadiums().size(); i++)
                                System.out.println("Incasso per " + test.getStadiums().get(i).toString() + ": " + test.getStadiumIncome(test.getStadiums().get(i)));
                            System.out.println();
                        }
                        else if(choice == 3)
                            back = true;
                    } while(!back);
                }
                else if(choice == 7)
                    break;
                back = false;
            } while(!back);
	}
        input.close();
    }
    
    /**
     * Carica da file una lista degli utenti registrati
     * @param path percorso della cartella in cui sono salvati i file
     * @return la lista degli utenti registrati
     */
    private static ArrayList<Customer> loadUsers(String path){
        int n;
        ArrayList<Customer> users = new ArrayList<>();
        try{
            Scanner fileScanner = new Scanner(new File(path + "users.dat")); // carica il file degli utenti registrati
            while(fileScanner.hasNextLine()){ // finchè il file finisce
                n = Integer.parseInt(fileScanner.nextLine()); // numero di utenti registrati
                fileScanner.nextLine(); // riga vuota
                for(int i=0; i<n; i++){
                    String name = fileScanner.nextLine(); // nome utente
                    String type = fileScanner.nextLine(); // categoria utente
                    users.add(new Customer(name, type));
                    fileScanner.nextLine(); // riga vuota
                }
            }
            fileScanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        return users;
    }
    
    /**
     * Scrive su file una lista degli utenti registrati
     * @param path Percorso della cartella in cui sono salvati i file
     * @param users la lista degli utenti da salvare
     */
    private static void saveUsers(String path, ArrayList<Customer> users){
        // salva nuovo utente su file
        try{
            PrintWriter out = new PrintWriter(path + "users.dat");
            out.println(users.size()); // stampa nuovo numero utenti
            out.println(); // stampa riga vuota
            for(int i=0; i < users.size(); i++){
                out.println(users.get(i).getUsername());
                out.println(users.get(i).getType());
                out.println(); // stampa riga vuota
            }
            out.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}