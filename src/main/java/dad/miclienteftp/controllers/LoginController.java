package dad.miclienteftp.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.net.ftp.FTPClient;

import dad.miclienteftp.MiClienteFTPApp;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

public class LoginController implements Initializable {

	// model

	private StringProperty servidor = new SimpleStringProperty();
	private IntegerProperty puerto = new SimpleIntegerProperty();
	private StringProperty usuario = new SimpleStringProperty();
	private StringProperty password = new SimpleStringProperty();

	private ObjectProperty<FTPClient> cliente = new SimpleObjectProperty<>();

	// view

	@FXML
	private GridPane view;

	@FXML
	private TextField servidorText;

	@FXML
	private TextField puertoText;

	@FXML
	private TextField usuarioText;

	@FXML
	private PasswordField passwordText;

	@FXML
	private Button conectarButton;

	@FXML
	private Button cancelarButton;

	private Stage stage;

	public LoginController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// bindings

		servidor.bind(servidorText.textProperty());
		Bindings.bindBidirectional(puertoText.textProperty(), puerto, new NumberStringConverter());
		usuario.bind(usuarioText.textProperty());
		password.bind(passwordText.textProperty());

		// creamos el stage para este controlador

		stage = new Stage();
		stage.setTitle("Conectar al servidor");
		stage.setScene(new Scene(getView()));
		stage.initOwner(MiClienteFTPApp.primaryStage);
		stage.initModality(Modality.APPLICATION_MODAL);

	}

	public GridPane getView() {
		return view;
	}

	@FXML
	void onCancelarButton(ActionEvent event) {
		stage.close();
	}

	@FXML
	void onConectarButton(ActionEvent event) {
		try {
			FTPClient ftp = new FTPClient();
			ftp.connect(servidor.get(), puerto.get());
			ftp.login(usuario.get(), password.get());
			cliente.set(ftp);
			stage.close();
			exito();
		} catch (IOException e) {
			error();
			cliente.set(null);
		}
	}
	
	public void exito() {
		Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Conexión");
    	alert.setHeaderText("Conexción establecida con éxito.");
    	alert.initOwner(MiClienteFTPApp.primaryStage);
    	
    	alert.showAndWait();
	}
	
	public void error() {
		Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Error");
    	alert.setHeaderText("No se pudo conectar: " + servidor.get());
    	alert.initOwner(MiClienteFTPApp.primaryStage);
    	
    	alert.showAndWait();
	}

	public void show() {
		stage.showAndWait();
	}

	public ObjectProperty<FTPClient> clienteProperty() {
		return this.cliente;
	}

	public FTPClient getCliente() {
		return this.clienteProperty().get();
	}

	public void setCliente(final FTPClient cliente) {
		this.clienteProperty().set(cliente);
	}

}
