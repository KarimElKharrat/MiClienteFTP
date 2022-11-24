package dad.miclienteftp.model;

public enum Tipo {
	DIRECTORIO(0), 
	FICHERO(1), 
	ENLACE(2);

	private final int valor;
	
	Tipo(int i) {
		valor = i;
	}
	
	public int getValor() {
		return valor;
	}
}
