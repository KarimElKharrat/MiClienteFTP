package dad.miclienteftp.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import dad.miclienteftp.MiClienteFTPApp;
import dad.miclienteftp.model.Directorio;
import dad.miclienteftp.model.Tipo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class RootController implements Initializable {

	// controllers

	private LoginController loginController = new LoginController();

	// model

	private ObjectProperty<FTPClient> cliente = new SimpleObjectProperty<>();
	private ObjectProperty<Directorio> directorioSeleccionado = new SimpleObjectProperty<>();
	private BooleanProperty conectado = new SimpleBooleanProperty();
	private StringProperty directorio = new SimpleStringProperty();
	private ListProperty<Directorio> directorios = new SimpleListProperty<>(FXCollections.observableArrayList());
	private int i = 0;
	
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
    
    @FXML
    private Button descargarButton;

	public RootController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RootView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// bindings
		
		directoriosTable.itemsProperty().bind(directorios);
		infoLabel.textProperty().bind(directorio);
		directorioSeleccionado.bind(directoriosTable.getSelectionModel().selectedItemProperty());

		conectarItem.disableProperty().bind(conectado);
		desconectarItem.disableProperty().bind(conectado.not());
		descargarButton.disableProperty().bind(directorioSeleccionado.isNull());
		
		cliente.bindBidirectional(loginController.clienteProperty());

		// set cell value factory
		
		nombreColumn.setCellValueFactory(v -> v.getValue().nombreProperty());
		tamanioColumn.setCellValueFactory(v -> v.getValue().tamanioProperty());
		tipoColumn.setCellValueFactory(v -> v.getValue().tipoProperty());
		
		// listeners

		cliente.addListener(this::onClienteChanged);

		conectado.addListener(this::onConectadoChanged);
		
		directoriosTable.getSelectionModel().selectedItemProperty().addListener(this::onSelectedChangedListener);
		
		directorioSeleccionado.addListener(this::onSeleccionadoChanged);

		// load data
		
		directorio.set("Desconectado");
		
	}

	private void onSeleccionadoChanged(ObservableValue<? extends Directorio> o, Directorio ov, Directorio nv) {
		
		if(ov != null)
			descargarButton.disableProperty().unbind();
		if(nv != null)
			descargarButton.disableProperty().bind(nv.tipoProperty().isNotEqualTo(Tipo.FICHERO));
		
	}

	private void onSelectedChangedListener(ObservableValue<? extends Directorio> o, Directorio ov, Directorio nv) {
		i = 0;
	}

	private void onConectadoChanged(ObservableValue<? extends Boolean> o, Boolean ov, Boolean nv) {
		if (nv) {
			try {
				loadDirectory();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadDirectory() throws IOException {
		
		directorio.set(cliente.get().printWorkingDirectory());
		directorios.clear();
		
		FTPFile[] ficheros = cliente.get().listFiles();

		Arrays.stream(ficheros).forEach(fichero -> {
			directorios.add(new Directorio(fichero.getName(), fichero.getSize(), fichero.getType()));
		});

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
			directorios.clear();
			directorio.set("Desconectado");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	void onDescargarAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Guardar archivo");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setInitialFileName(directorioSeleccionado.get().getNombre());
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
		
		File selectedFile = fileChooser.showSaveDialog(MiClienteFTPApp.primaryStage);
    	if(selectedFile != null) {
    		descargar(selectedFile);
    	}
	}
	
	private void descargar(File selectedFile) {
		try {
			File descarga = new File(selectedFile.getPath());
			FileOutputStream flujo = new FileOutputStream(descarga);
			cliente.get().retrieveFile(directorioSeleccionado.get().getNombre(), flujo);
			flujo.flush();
			flujo.close();
			
			descargadoConExito();
		} catch (IOException e) {
			descargadoError();
		}
	}
	
	private void descargadoConExito() {
		Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Descargado");
    	alert.setHeaderText("El fichero ha sido descargado.");
    	alert.initOwner(MiClienteFTPApp.primaryStage);
    	alert.showAndWait();
	}
	
	private void descargadoError() {
		Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("ERROR");
    	alert.setHeaderText("El fichero no ha podido ser descargado.");
    	alert.initOwner(MiClienteFTPApp.primaryStage);
    	alert.showAndWait();
	}

	@FXML
    void onMouseClicked(MouseEvent event) throws IOException {
		i++;
		if (i == 2 && directorioSeleccionado.get().getNombre() != null && (directorioSeleccionado.get().getTipo().equals(Tipo.DIRECTORIO) || directorioSeleccionado.get().getTipo().equals(Tipo.ENLACE))) {
			
			if(directorioSeleccionado.get().getNombre().equals(".."))
				cliente.get().changeToParentDirectory();
			else {
				cliente.get().changeWorkingDirectory(
					cliente.get().printWorkingDirectory().equals("/") ? 
						cliente.get().printWorkingDirectory() + directorioSeleccionado.get().getNombre() :
						cliente.get().printWorkingDirectory() + "/" + directorioSeleccionado.get().getNombre()
				);
			}
			
			loadDirectory();
			
	    }
    }

	public void onCloseRequest() {
		try {
			if(cliente.get() != null)
				cliente.get().disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
