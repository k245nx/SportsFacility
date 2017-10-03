package components.exceptions;

/**
 * Eccezione da lanciare nel caso in cui lo sconto scelto è meno conveniente dello sconto attualmente in vigore
 * @author Roberto Tarullo, Pasquale Turi
 */
public class ScontoMiglioreException extends RuntimeException{
	public ScontoMiglioreException(){
		super("Esiste già uno sconto migliore o uguale di quello specificato");
	}
	public ScontoMiglioreException(String msg){
		super(msg);
	}
}
