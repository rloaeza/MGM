package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import modelo.Caja;
import modelo.Configuraciones;
import modelo.Funciones;
import modelo.Personal;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CorteCaja extends Controlador implements Initializable {

    @FXML
    private AnchorPane Pane;

    @FXML
    private JFXTextField Monto;

    @FXML
    private Label TituloCaja;

    @FXML
    private Label Vendedor;

    @FXML
    private JFXComboBox<Caja> CBCajas;

    @FXML
    private JFXPasswordField VendedorClave;

    @FXML
    private JFXComboBox<Personal> Supervisor;

    @FXML
    private JFXPasswordField SupervisorClave;

    @FXML
    void Aceptar(ActionEvent event) throws IOException {
        if(VendedorClave.getText().isEmpty())
            return;
        if(Supervisor.getSelectionModel().getSelectedIndex()==-1)
            return;
        if(SupervisorClave.getText().isEmpty())
            return;

        if(Monto.getText().isEmpty())
            return;

        if(CBCajas.getSelectionModel().getSelectedIndex()==-1)
            return;


        if(VendedorClave.getText().equals(Configuraciones.clavePersonal) && SupervisorClave.getText().equals(Supervisor.getValue().getClave()))
        {

            Map<String,Object> paramsJSON = new LinkedHashMap<>();
            paramsJSON.put("Actividad", "CajaCorte: Insertar");
            paramsJSON.put("idCaja", CBCajas.getValue().getIdCaja());
            paramsJSON.put("idPersonal", Configuraciones.idPersonal);
            paramsJSON.put("idPersonalAut", Supervisor.getValue().getIdPersonal());
            paramsJSON.put("monto", Monto.getText());
            if(Configuraciones.abriendoCaja)
                paramsJSON.put("tipo", 1);
            else
                paramsJSON.put("tipo", 2);
            JsonArray rootArray = Funciones.consultarBD(paramsJSON);

            Configuraciones.corteCajaValido = true;

            cerrar();
        }

    }

    @FXML
    void Cancelar(ActionEvent event) {
        Configuraciones.corteCajaValido=false;
        cerrar();
    }

    private void cargarDatos() throws IOException {

        Vendedor.setText(Configuraciones.nombrePersonal);

        ObservableList<Personal> listaPersonalSupervisor = FXCollections.observableArrayList();

        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Personal: Lista con tipo");
        paramsJSON.put("idClinica", Configuraciones.idClinica);
        paramsJSON.put("tipo", 1);
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int t = rootArray.size();
            for(int i = 1; i< t; i++) {
                listaPersonalSupervisor.add(new Gson().fromJson(rootArray.get(i).getAsJsonObject(), modelo.Personal.class) );
            }
        }
        Supervisor.setItems(listaPersonalSupervisor);




        ObservableList<modelo.Caja> listaCajas = FXCollections.observableArrayList();

        paramsJSON.clear();
        paramsJSON.put("Actividad", "Caja: Lista");
        paramsJSON.put("idClinica", Configuraciones.idClinica);
        rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int t = rootArray.size();
            for(int i = 1; i< t; i++) {
                listaCajas.add(new Gson().fromJson(rootArray.get(i).getAsJsonObject(), modelo.Caja.class) );
            }
        }
        CBCajas.setItems(listaCajas);





    }
    private void cerrar() {
        Stage window = (Stage)(Pane.getScene().getWindow());
        parametros.remove(0);
        window.close();
    }

    @Override
    public void init() {
        try {
            cargarDatos();
            if(Configuraciones.abriendoCaja)
                TituloCaja.setText(Configuraciones.corteCajaAbrir);
            else
                TituloCaja.setText(Configuraciones.corteCajaCerrar);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

