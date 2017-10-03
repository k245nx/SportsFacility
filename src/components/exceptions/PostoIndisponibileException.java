package components.exceptions;

/**
 * Eccezione da lanciare nel caso in cui il posto selezionato sia già prenotato o acquistato
 * @author Roberto Tarullo, Pasquale Turi
 */
public class PostoIndisponibileException extends RuntimeException{
	public PostoIndisponibileException(){
		super("Posto non disponibile");
	}
	public PostoIndisponibileException(String msg){
		super(msg);
	}
}
