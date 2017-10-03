import components.SportsFacilityManager;
import components.SportsFacilityCustomer;
import components.Customer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Consente di scegliere se avviare l'interfaccia grafica di GestioneSportiva in modalità cliente o gestore.
 * Se si sceglie la modalità utente verrà chiesto il nome utente.
 * Se il nome utente non è registrato su file verrà chiesta e memorizzata la categoria dell'utente.
 * @author Roberto Tarullo, Pasquale Turi
 */
public class StarterGUI{
    public static void main(String[] args){
        final String path = "C:\\Users\\Roberto Tarullo\\Desktop\\";
        
        JFrame frame;
        Object[] options = {"Modalità Cliente", "Modalità Gestore"};
        int n = JOptionPane.showOptionDialog(null, "Scegliere una modalità", "Mode selection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if(n==0){
            String user;
            String customer = null;
            user = JOptionPane.showInputDialog("Username:");
            if(user != null){
                ArrayList<Customer> users = loadUsers(path);
                for(int i=0; customer==null && i<users.size(); i++){ // cerca tra gli utenti registrati una corrispondenza
                    if(users.get(i).getUsername().equalsIgnoreCase(user))
                        customer = users.get(i).getType();
                }
                if(customer==null){ // se non è stata trovata alcuna corrispondenza
                    customer = JOptionPane.showInputDialog("Categoria cliente:"); // chiedi categoria cliente
                    if(customer!=null){
                        users.add(new Customer(user, customer));
                        saveUsers(path, users);
                    }
                }
                if(customer!=null){
                    frame = new CustomerFrame(new SportsFacilityCustomer(path, user, customer));
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setVisible(true);
                }
            }
        }
        else if(n==1){
            frame = new ManagerFrame(new SportsFacilityManager(path));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }
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