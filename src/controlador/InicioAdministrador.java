package controlador;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import modelo.Funciones;
import modelo.Personal;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class InicioAdministrador implements Initializable {

    @FXML
    private Pane Pane;

    @FXML
    private Label Estado;

    private Personal usuario;

    @FXML
    void cerrarSesion(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/vista/inicio_sesion.fxml"));
        Stage escenario = (Stage) Pane.getScene().getWindow();
        escenario.setScene(new Scene(root, Funciones.ancho, Funciones.alto));
    }

    @FXML
    void salir(ActionEvent event) {
        Platform.exit();
    }



    @FXML
    void catalogoTiposDeProductos(ActionEvent event) throws IOException {
        AnchorPane root = FXMLLoader.load(getClass().getResource("/vista/tipos_productos.fxml"));
        root.setPrefHeight(Pane.getHeight());
        root.setPrefWidth(Pane.getWidth());
        Pane.getChildren().setAll(root);


    }


    @FXML
    void catalogoTratamientos(ActionEvent event) throws IOException {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("idClinica", 1);
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource("/vista/tratamientos.fxml"), params, new Productos());
    }

    public void setUsuario(Personal p) {
        this.usuario = p;
    }

    public void init() {
        Estado.setText(usuario.getNombre());
        ((Stage)Pane.getScene().getWindow()).setTitle(usuario.getaPaterno()+ " "+usuario.getaMaterno()+", "+usuario.getNombre());


        Stage escenario = (Stage) Pane.getScene().getWindow();

        escenario.widthProperty().addListener((observable, oldValue, newValue) -> {
            if(Pane.getChildren().size()>0) {
                AnchorPane root = (AnchorPane) Pane.getChildren().get(0);
                root.setPrefHeight(Pane.getHeight());
                root.setPrefWidth(Pane.getWidth());
            }
        });

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


}
