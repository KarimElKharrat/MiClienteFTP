package dad.miclienteftp;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Directorio {

	private StringProperty nombre = new SimpleStringProperty();
	private IntegerProperty tamanio = new SimpleIntegerProperty();
	private ObjectProperty<Tipo> tipo = new SimpleObjectProperty<>();
	
	public final StringProperty nombreProperty() {
		return this.nombre;
	}
	
	public final String getNombre() {
		return this.nombreProperty().get();
	}
	
	public final void setNombre(final String nombre) {
		this.nombreProperty().set(nombre);
	}
	
	public final IntegerProperty tamanioProperty() {
		return this.tamanio;
	}
	
	public final int getTamanio() {
		return this.tamanioProperty().get();
	}
	
	public final void setTamanio(final int tamanio) {
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
