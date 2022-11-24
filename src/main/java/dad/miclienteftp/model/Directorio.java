package dad.miclienteftp.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Directorio {

	private StringProperty nombre = new SimpleStringProperty();
	private LongProperty tamanio = new SimpleLongProperty();
	private ObjectProperty<Tipo> tipo = new SimpleObjectProperty<>();
	
	public Directorio(String nombre, long tamanio, int tipo) {
		
		setNombre(nombre);
		setTamanio(tamanio);

		for(Tipo t:Tipo.values()) {
			if(t.getValor() == tipo)
				setTipo(t);
		}
	}
	
	public final StringProperty nombreProperty() {
		return this.nombre;
	}
	
	public final String getNombre() {
		return this.nombreProperty().get();
	}
	
	public final void setNombre(final String nombre) {
		this.nombreProperty().set(nombre);
	}
	
	public final LongProperty tamanioProperty() {
		return this.tamanio;
	}
	
	public final long getTamanio() {
		return this.tamanioProperty().get();
	}
	
	public final void setTamanio(final long tamanio) {
		this.tamanioProperty().set(tamanio);
	}
	
	public final ObjectProperty<Tipo> tipoProperty() {
		return this.tipo;
	}
	
	public final Tipo getTipo() {
		return this.tipoProperty().get();
	}
	
	public final void setTipo(final Tipo tipo) {
		this.tipoProperty().set(tipo);
	}
	
	
}
