package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.*;
import modelo.Personal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class InicioSesion  extends Controlador  implements  Initializable {

    @FXML
    private AnchorPane Pane;

    @FXML
    private JFXTextField textUsuario;

    @FXML
    private JFXPasswordField textClave;

    @FXML
    private Label Version;

    @FXML
    private JFXComboBox<Clinica> comboClinica;


    @FXML
    void cambiarClinica(ActionEvent event) {
        Configuraciones.idClinica = comboClinica.getSelectionModel().getSelectedItem().getIdClinica();
        Configuraciones.nombreClinica = comboClinica.getSelectionModel().getSelectedItem().getNombre();
        Configuraciones.ticketTituloClinicaThermal = comboClinica.getValue().getDescripcion();
    }

    @FXML
    void entrarSistema(ActionEvent event) throws IOException {


        if( comboClinica.isVisible()) {

            Map<String, Object> paramsAlertClinica = new LinkedHashMap<>();
            paramsAlertClinica.put("titulo", "Información");
            paramsAlertClinica.put("texto", "Esta por entrar a la clinica: " + comboClinica.getValue().getNombre());
            paramsAlertClinica.put("vista", "/vista/alert_box2.fxml");
            Funciones.display(paramsAlertClinica, getClass().getResource("/vista/alert_box2.fxml"), new AlertBox2());


            if (!Configuraciones.clinicaOK)
                return;
        }

        Map<String,Object> params = new LinkedHashMap<>();
        params.put("Actividad", "InicioSesion");
        params.put("usuario", textUsuario.getText());
        params.put("clave", textClave.getText());
        //params.put("mac", Configuraciones.MAC);


        JsonArray rootArray = Funciones.consultarBD(params);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {

            if( comboClinica.isVisible()) {
                Map<String,Object> paramsFijarClinica = new LinkedHashMap<>();
                paramsFijarClinica.put("Actividad", "Fijar idClinica de Mac");
                Funciones.consultarBD(paramsFijarClinica);
            }




            Personal usuario = new Gson().fromJson(rootArray.get(1).getAsJsonObject(), Personal.class);
            params = new LinkedHashMap<>();
            params.put("idPersonal", usuario.getIdPersonal());
            params.put("idClinica", usuario.getIdClinica());

            params.put("clinicaDescripcion", usuario.getTitulo());

            params.put("nombre", usuario.getNombre());
            params.put("vista", "/vista/inicio_resumen.fxml");


            Configuraciones.idPersonal = usuario.getIdPersonal();
            Configuraciones.clinicaDescripcion = usuario.getTitulo();
            Configuraciones.nombrePersonal=usuario.getApellidos()+", "+usuario.getNombre();
            Configuraciones.clavePersonal = usuario.getClave();
            Configuraciones.cajaAbierta=false;
            Configuraciones.tipoUsuarioActivo = usuario.getTipo();



            // cargando...
            Datos.cargarPacientes();
            Datos.cargarPersonal();
            Datos.cargarCajas();
            Datos.cargarProductosConCosto();
            Datos.cargarProductos();
            Datos.cargarDecuentos();


            //Usuario de venta
            if(usuario.getTipo()==Configuraciones.tipoVendedor) {
                Configuraciones.tipoUsuarioActivo = Configuraciones.tipoVendedor;
                Configuraciones.cajaAbierta = false;

                params = new LinkedHashMap<>();
                params.put("Actividad", "CajaCorte: Listar");
                params.put("idPersonal", Configuraciones.idPersonal);

                rootArray = Funciones.consultarBD(params);
                if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {

                    modelo.CorteCaja corteCaja = new Gson().fromJson(rootArray.get(1).getAsJsonObject(), modelo.CorteCaja.class);

                    Configuraciones.impresoraTicket = corteCaja.getImpresoraTicket();
                    Configuraciones.impresoraReporte = corteCaja.getImpresoraReporte();
                    if ( corteCaja.getTipo() == 1) {
                        Configuraciones.cajaAbierta = true;
                        Configuraciones.idCaja = corteCaja.getIdCaja();
                        Configuraciones.aperturaCaja = corteCaja.getMonto();
                    }
                }
                if(Configuraciones.cajaAbierta==false) {


                    Map<String, Object> paramsAlert = new LinkedHashMap<>();
                    paramsAlert.put("titulo", "Corte de caja");

                    paramsAlert.put("vista", "/vista/corte_caja.fxml");

                    Configuraciones.corteCajaValido = false;
                    Configuraciones.abriendoCaja = true;
                    Funciones.display(paramsAlert, getClass().getResource("/vista/corte_caja.fxml"), new CorteCaja(), 762, 418);
                    if (!Configuraciones.corteCajaValido)
                        return;
                    Configuraciones.cajaAbierta = true;


                }


                Map<String,Object> paramsVista = new LinkedHashMap<>();
                paramsVista.put("idPersonal", Configuraciones.idPersonal);
                paramsVista.put("vista", "/vista/inicio_venta.fxml" );
                Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new InicioAdministrador());
            }
            else if(usuario.getTipo()==Configuraciones.tipoSupervisor){


                Map<String, Object> paramsAlert = new LinkedHashMap<>();
                paramsAlert.put("titulo", "Abrir caja");

                paramsAlert.put("vista", "/vista/selec_caja.fxml");

                Configuraciones.corteCajaValido = false;
                Funciones.display(paramsAlert, getClass().getResource("/vista/selec_caja.fxml"), new SelecCaja(), 762, 300);
                if (!Configuraciones.corteCajaValido)
                    return;
                Configuraciones.cajaAbierta = false;

                Funciones.CargarVista(Pane, getClass().getResource("/vista/inicio_supervisor.fxml"), params, new InicioAdministrador());
            }
            else if(usuario.getTipo()==Configuraciones.tipoAdministrador){


                Map<String, Object> paramsAlert = new LinkedHashMap<>();
                paramsAlert.put("titulo", "Abrir caja");

                paramsAlert.put("vista", "/vista/selec_caja.fxml");

                Configuraciones.corteCajaValido = false;
                Funciones.display(paramsAlert, getClass().getResource("/vista/selec_caja.fxml"), new SelecCaja(), 762, 300);
                if (!Configuraciones.corteCajaValido)
                    return;
                Configuraciones.cajaAbierta = false;

                Funciones.CargarVista(Pane, getClass().getResource("/vista/inicio_administrador.fxml"), params, new InicioAdministrador());
            }
        }
        else {
            Map<String,Object> paramsAlert = new LinkedHashMap<>();
            paramsAlert.put("titulo", "Error");
            paramsAlert.put("texto", "Usuario y/o clave incorrectos");
            paramsAlert.put("vista", "/vista/alert_box.fxml");
            Funciones.display(paramsAlert, getClass().getResource("/vista/alert_box.fxml"), new AlertBox() );
        }
    }


    @Override
    public void init() {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            Datos.cargarClinicas();
            Version.setText(Configuraciones.versionText+Configuraciones.versionId);

            Configuraciones.idClinica = cargarIdClinica();
            if( Configuraciones.idClinica > 0) {
                comboClinica.setVisible(false);
                for(Clinica c : Datos.clinicas) {
                    if(c.getIdClinica() == Configuraciones.idClinica) {
                        Configuraciones.nombreClinica = c.getNombre();
                        Configuraciones.ticketTituloClinicaThermal = c.getDescripcion();
                        break;
                    }
                }
                return;
            }


            comboClinica.setItems(Datos.clinicas);
            comboClinica.getSelectionModel().select(0);
            cambiarClinica(null);
            comboClinica.setOnKeyPressed(event -> {
                if ( event.getCode() == KeyCode.ENTER) {
                    try {
                        entrarSistema(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private int cargarIdClinica() throws IOException {
        Map<String,Object> params = new LinkedHashMap<>();

        params.put("Actividad", "IdClinica de Mac");
        JsonArray rootArray = Funciones.consultarBD(params);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int idClinica = rootArray.get(1).getAsJsonObject().get(Funciones.idClinicaMac).getAsInt();
            return idClinica;

        }
        return 0;
    }
}
