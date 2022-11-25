package dad.miclienteftp.model;

public enum Tipo {
	FICHERO(0), 
	DIRECTORIO(1), 
	ENLACE(2);

	private final int valor;
	
	Tipo(int i) {
		valor = i;
	}
	
	public int getValor() {
		return valor;
	}
}
