package dad.miclienteftp;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class RootController implements Initializable {

	// controllers

	private LoginController loginController = new LoginController();

	// model

	private ObjectProperty<FTPClient> cliente = new SimpleObjectProperty<>();
	private BooleanProperty conectado = new SimpleBooleanProperty();
	private StringProperty directorio = new SimpleStringProperty();
//	private ListProperty<FTPFile> directorios = new SimpleListProperty<>();
	private ListProperty<Directorio> directorios = new SimpleListProperty<>();

	// view

	@FXML
	private BorderPane view;
	
	@FXML
	private MenuItem conectarItem, desconectarItem;
	
	@FXML
	private Label infoLabel;
	
	@FXML
    private TableView<Directorio> directoriosTable;
	
	@FXML
    private TableColumn<Directorio, String> nombreColumn;

    @FXML
    private TableColumn<Directorio, Number> tamanioColumn;

    @FXML
    private TableColumn<Directorio, Tipo> tipoColumn;

	public RootController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RootView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// bindings
		
		directoriosTable.itemsProperty().bind(directorios);
		nombreColumn.setCellValueFactory(v -> v.getValue().nombreProperty());
		tamanioColumn.setCellValueFactory(v -> v.getValue().tamanioProperty());
		tipoColumn.setCellValueFactory(v -> v.getValue().tipoProperty());
		
		infoLabel.textProperty()
				.bind(Bindings.when(conectado).then(directorio.get()).otherwise("Desconectado"));

		conectarItem.disableProperty().bind(conectado);
		desconectarItem.disableProperty().bind(conectado.not());

		cliente.bindBidirectional(loginController.clienteProperty());

		// listeners

		cliente.addListener((o, ov, nv) -> onClienteChanged(o, ov, nv));

		conectado.addListener((o, ov, nv) -> onConectadoChanged(o, ov, nv));

	}

	private void onConectadoChanged(ObservableValue<? extends Boolean> o, Boolean ov, Boolean nv) {

		if (nv) {

			// recuperar un listado de los ficheros y directorios del directorio actual del
			// servidor

			try {
				directorio.set(cliente.get().printWorkingDirectory());
				
				FTPFile[] ficheros = cliente.get().listFiles();

				// recorrer el listado de archivos recuperados
				System.out.format("+------------------------+%n");
				System.out.format("| Archivos del servidor: |%n");
				System.out.format("+------------------------+-----------------+----------------+-----------------+%n");
				System.out.format("| Nombre                                   | Tamaño (bytes) | Tipo            |%n");
				System.out.format("+------------------------------------------+----------------+-----------------+%n");
				Arrays.stream(ficheros).forEach(fichero -> {
					
					System.out.format("| %-40s | %-14d | %-15s |%n", fichero.getName(), fichero.getSize(),
							fichero.getType());
				});
				System.out.format("+------------------------------------------+----------------+-----------------+%n");

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void onClienteChanged(ObservableValue<? extends FTPClient> o, FTPClient ov, FTPClient nv) {

		if (nv != null && nv.isConnected()) {

			conectado.set(true);

		} else {

			conectado.set(false);

		}

	}

	public BorderPane getView() {
		return view;
	}

	@FXML
	void onConectarAction(ActionEvent event) {

		loginController.show();

	}

	@FXML
	void onDesconectarAction(ActionEvent event) {

		try {
			cliente.get().disconnect();
			cliente.set(null);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@FXML
	void onDescargarAction(ActionEvent event) {

		//TODO implementar
		
	}

}
