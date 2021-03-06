package controlador;

import com.google.gson.JsonArray;
import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.Configuraciones;
import modelo.Funciones;
import modelo.Personal;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class InicioAdministrador extends  Controlador implements Initializable {


    @FXML
    private Pane Pane;

    private Personal usuario;


    @FXML
    private JFXButton BarraFecha;

    @FXML
    private JFXButton BarraAlmacen;

    @FXML
    private JFXButton BarraPagosTratamientos;

    @FXML
    void cambiarUsuario(ActionEvent event) throws IOException {



        Parent root = FXMLLoader.load(getClass().getResource("/vista/inicio_sesion.fxml"));
        Stage escenario = (Stage) Pane.getScene().getWindow();
        double h = escenario.getHeight();
        double w = escenario.getWidth();
        escenario.setScene(new Scene(root, w, h));
        escenario.setMaximized(true);



    }

    @FXML
    void cambiarClinica(ActionEvent event) throws IOException {
        int idClinicaAnt = Configuraciones.idClinica;
        Map<String, Object> paramsAlert = new LinkedHashMap<>();
        paramsAlert.put("titulo", "Seleccionar clínica");

        paramsAlert.put("vista", "/vista/selec_clinica.fxml");

        Funciones.display(paramsAlert, getClass().getResource("/vista/selec_clinica.fxml"), new SelecClinica(), 762, 300);


        if(Configuraciones.idClinica!= idClinicaAnt) {
            Map<String,Object> paramsFijarClinica = new LinkedHashMap<>();
            paramsFijarClinica.put("Actividad", "Fijar idClinica de Mac");
            Funciones.consultarBD(paramsFijarClinica);

            ((Stage)Pane.getScene().getWindow()).setTitle(Configuraciones.nombrePersonal + " / " + Configuraciones.nombreClinica);


            cerrarSesion(null);
        }


    }
    @FXML
    void cerrarSesion(ActionEvent event) throws IOException {

        if(Configuraciones.cajaAbierta)
            if(!cerrarCaja())
                return;




        Parent root = FXMLLoader.load(getClass().getResource("/vista/inicio_sesion.fxml"));
        Stage escenario = (Stage) Pane.getScene().getWindow();
        double h = escenario.getHeight();
        double w = escenario.getWidth();
        escenario.setScene(new Scene(root, w, h));
        escenario.setMaximized(true);



    }

    @FXML
    void salir(ActionEvent event) {
        //Platform.exit();
        System.exit(1);
    }





    @FXML
    void catalogoTiposDeProductos(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);

        paramsVista.put("vista", "/vista/tipos_productos.fxml");
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new controlador.TiposProductos());


    }
    @FXML
    void catalogoPersonal(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();

        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("idPersonal", Configuraciones.idPersonal);
        paramsVista.put("vista", "/vista/personal.fxml");


        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource("/vista/personal.fxml"), paramsVista, new controlador.Personal());
    }
    @FXML
    void catalogoPacientes(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("idPersonal", Configuraciones.idPersonal);

        paramsVista.put("vista", "/vista/pacientes.fxml");
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new Pacientes());
    }
    @FXML
    void catalogoTratamientos(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/tratamientos.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new Productos());
    }

    @FXML
    void costoTratamientos(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/costo_tratamientos.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new CostoTratamientos());
    }

    @FXML
    void catalogoServicios(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/catalogo_servicios.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new CatalogoServicios());
    }

    @FXML
    void catalogoDescuentos(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/descuentos.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new Descuentos());
    }

    @FXML
    void almacenEntradas(ActionEvent event) throws IOException {

        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/almacen_entradas.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new AlmacenEntradas());

/*
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/almacen_entrada_2.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new AlmacenEntrada2());
*/

    }


    @FXML
    void almacenExistencias(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/almacen_existencias.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new AlmacenExistencias());
    }


    @FXML
    void almacenSalidas(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/almacen_salidas.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new AlmacenSalidas());
    }

    @FXML
    void costoProductosTodos(ActionEvent event) throws IOException {



        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/costo_productos_todos.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new CostoProductosTodos());

    }
    @FXML
    void costoProductos(ActionEvent event) throws IOException {

        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/costo_productos.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new CostoProductos());


    }

    @FXML
    void relojChecadorEntradaSalida(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", 1);
        paramsVista.put("vista", "/vista/reloj_checador.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new RelojChecador());
    }

    @FXML
    void VentaMostrador(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();


        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("idPersonal", Configuraciones.idPersonal);

        paramsVista.put("vista", "/vista/venta_mostrador2.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new VentaMostrador2());
    }

    @FXML
    void relojChecadorFijarHorarios(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/horarios.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new Horarios());
    }

    @FXML
    void AgendarCitas(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/citas.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new Citas());
    }

    @FXML
    void pagoTratamientos(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/deuda_tratamientos.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new DeudaTratamientos());
    }

    @FXML
    void ReporteHistorialDeCompras(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/vista_reporte_paciente.fxml");
        paramsVista.put("Titulo", Configuraciones.reporteEncabezado+"Reporte: Paciente");
        //paramsVista.put("titulos", new String[]{"Fecha:20:CENTER-LEFT", "Paciente:50:CENTER", "Importe:15:CENTER", "Clinica:40:CENTER", "Producto:100:CENTER-LEFT"});
        paramsVista.put("titulos", new String[]{"Fecha:20:CENTER-LEFT", "Clinica:40:CENTER", "Importe:15:CENTER", "Producto:100:CENTER-LEFT"});
        paramsVista.put("pdf", "formatos/reporte_paciente.pdf");
        paramsVista.put("reporte", "Reporte: Paciente");
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new VistaReporteExistencia());
    }


    @FXML
    void ReporteVentaGeneral(ActionEvent event) throws IOException {

        Funciones.print("Preparando para cargar reporteVentasGenerales\n");
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/vista_reporte_general.fxml" );
        paramsVista.put("Titulo", Configuraciones.reporteEncabezado+"Reporte: Ventas general");
        paramsVista.put("titulos", new String[]{"Venta:10:CENTER","Paciente:40:CENTER-LEFT","Tratamiento:20:CENTER", "Producto:20:CENTER", "Efectivo:20:CENTER", "Tarjeta:20:CENTER","Deposito:20:CENTER","Transferencia:20:CENTER", "Descuento:15:CENTER", "DescuentoEfectivo:15:CENTER", "Total:20:CENTER"});


        paramsVista.put("pdf", "formatos/reporte_venta_2.pdf");

        paramsVista.put("reporteVentas", "Reporte: General completo");
        paramsVista.put("reporteVentasCanceladas", "Reporte: General completo canceladas");
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new VistaReporteGeneral());

        /*
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/vista_reporte_general.fxml" );
        paramsVista.put("Titulo", Configuraciones.reporteEncabezado+"Reporte: Ventas general");
        paramsVista.put("titulos", new String[]{"Venta:10:CENTER","Paciente:40:CENTER-LEFT","Tratamiento:20:CENTER", "Producto:20:CENTER", "Efectivo:20:CENTER", "Tarjeta:20:CENTER", "Total:20:CENTER"});
        paramsVista.put("pdf", "formatos/reporte_venta_2.pdf");
        paramsVista.put("reporteVentas", "Reporte: General completo");
        paramsVista.put("reporteVentasCanceladas", "Reporte: General completo canceladas");
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new VistaReporteGeneral());
        /*
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/vista_reporte_general_completo.fxml" );
        paramsVista.put("Titulo", Configuraciones.reporteEncabezado+"Reporte: Ventas general completo");
        paramsVista.put("titulos", new String[]{"Venta:10:CENTER","Paciente:40:CENTER-LEFT","Tratamiento:20:CENTER", "Producto:20:CENTER", "Efectivo:20:CENTER", "Tarjeta:20:CENTER", "Total:20:CENTER"});
        paramsVista.put("pdf", "formatos/reporte_venta_2.pdf");
        paramsVista.put("reporte", "Reporte: General completo");
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new VistaReporteGeneralCompleto());
         */
    }



    @FXML
    void ReporteExistenciaAlmacen(ActionEvent event) throws IOException {

        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/vista_reportes.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new VistaReportes());

    }

    @FXML
    void catalogoCajas(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("idClinica", Configuraciones.idClinica);
        paramsVista.put("vista", "/vista/cajas.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new Cajas());
    }



    public void setUsuario(Personal p) {
        this.usuario = p;
    }

    public void init() {

        //Estado.setText(usuario.getProducto());
        ((Stage)Pane.getScene().getWindow()).setTitle(Configuraciones.nombrePersonal + " / " + Configuraciones.nombreClinica);





        /*
        Stage escenario = (Stage) Pane.getScene().getWindow();

        escenario.widthProperty().addListener((observable, oldValue, newValue) -> {
            if(Pane.getChildren().size()>0) {
                AnchorPane root = (AnchorPane) Pane.getChildren().get(0);
                root.setPrefHeight(Pane.getHeight());
                root.setPrefWidth(Pane.getWidth());
            }
        });

        */

        Pane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            KeyCode kc = ke.getCode();
            try {
                if (kc == KeyCode.F1 ) {
                    VentaMostrador(null);
                    ke.consume();
                }
                else if( kc==KeyCode.F2) {
                    AgendarCitas(null);
                    ke.consume();
                }
                else if( kc==KeyCode.F5) {
                    ReporteExistenciaAlmacen(null);
                    ke.consume();
                }
                else if( kc==KeyCode.F6) {
                    relojChecadorEntradaSalida(null);
                    ke.consume();
                }

            }catch (IOException ioex) {

            }

        });





    }


    @FXML
    void f1(ActionEvent event) throws IOException {
        VentaMostrador(null);
    }
    @FXML
    void f2(ActionEvent event) throws IOException {
        AgendarCitas(null);
    }
    @FXML
    void f5(ActionEvent event) throws IOException {
        ReporteExistenciaAlmacen(null);
    }

    @FXML
    void f6(ActionEvent event) throws IOException {
        relojChecadorEntradaSalida(null);
    }

    @FXML
    void f7(ActionEvent event) throws IOException {
        Map<String,Object> paramsVista = new LinkedHashMap<>();
        paramsVista.put("vista", "/vista/venta_editar.fxml" );
        Funciones.CargarVista((AnchorPane)Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new VentaEditar());
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        BarraFecha.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(1000), ae -> {
            BarraFecha.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        } ));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();


        Timeline t2 = new Timeline(new KeyFrame(Duration.millis(10000), ae -> {

            try {
                if(verificarAlmacen()) {
                    BarraAlmacen.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)));
                }
                else {
                    BarraAlmacen.setBackground(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } ));
        t2.setCycleCount(Animation.INDEFINITE);
        //t2.play();

/*

        Timeline t3 = new Timeline(new KeyFrame(Duration.millis(1000), ae -> {
            try {
                Map<String, Object> paramsVista = new LinkedHashMap<>();
                paramsVista.put("idClinica", parametros.get(0).get("idClinica"));
                paramsVista.put("idPersonal", parametros.get(0).get("idPersonal"));
                paramsVista.put("clinicaDescripcion", parametros.get(0).get("clinicaDescripcion"));
                paramsVista.put("vista", "/vista/citas.fxml");
                Funciones.CargarVista((AnchorPane) Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new Citas());
            }catch (IOException ioE) {

            }
        } ));
        t3.setCycleCount(1);
        t3.play();
*/


        Timeline tPagos = new Timeline(new KeyFrame(Duration.millis(10000), ae -> {

            try {
                if(verificarPagos()) {
                    BarraPagosTratamientos.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)));
                }
                else {
                    BarraPagosTratamientos.setBackground(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } ));
        tPagos.setCycleCount(Animation.INDEFINITE);
        //tPagos.play();






        Timeline tInicio = new Timeline(new KeyFrame(Duration.millis(100), ae -> {

            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);


            if(Configuraciones.tipoUsuarioActivo ==  Configuraciones.tipoVendedor)
            {

                try {
                    VentaMostrador(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            Map<String,Object> paramsAlert = new LinkedHashMap<>();
            paramsAlert.put("titulo", "Bienvenido");
            paramsAlert.put("vista", "/vista/alert_box.fxml");


            int r = new Random().nextInt(Configuraciones.motd.length);
            if(timeOfDay >= 0 && timeOfDay < 12){
                paramsAlert.put("texto", "Buenos dias "+ Configuraciones.nombrePersonal + "\n\n" + Configuraciones.motd[r] );
            }
            else if(timeOfDay >= 12 && timeOfDay < 20) {
                paramsAlert.put("texto", "Buenas tardes " + Configuraciones.nombrePersonal + "\n\n" + Configuraciones.motd[r] );
            }
            else if(timeOfDay >= 20 && timeOfDay < 24){
                paramsAlert.put("texto", "Buenas noches " + Configuraciones.nombrePersonal + "\n\n" + Configuraciones.motd[r] );
            }
            try {
                Funciones.displayFP(paramsAlert, this.getClass().getResource("/vista/alert_box.fxml"), new AlertBox() );
            } catch (IOException e) {
                e.printStackTrace();
            }




        } ));
        tInicio.setCycleCount(1);
        tInicio.play();





    }


    private boolean verificarAlmacen() throws IOException {
        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Almacen: Verificar minimos");

        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            return true;
        }
        return false;
    }

    private boolean verificarPagos() throws IOException {
        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Tratamientos recetados: Pagos pendientes");

        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            return true;
        }
        return false;
    }



    private boolean cerrarCaja() throws IOException {

  /*
        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "CajaCorte: Cerrar");
        paramsJSON.put("idCaja", Configuraciones.idCaja);
        Configuraciones.cierreCaja=0;

        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            String s=rootArray.get(1).getAsJsonObject().get("venta").toString();
            s=s.replace("\"", "");
            try {
                Configuraciones.cierreCaja = Double.valueOf(s);
            }catch (NumberFormatException ex) {
                Configuraciones.cierreCaja=0;
            }
        }



        Configuraciones.cierreCaja+=Configuraciones.aperturaCaja;
*/


        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "CajaCorte: Cerrar");
        paramsJSON.put("idCaja", Configuraciones.idCaja);
        paramsJSON.put("idPersonal", Configuraciones.idPersonal);
        Configuraciones.cierreCaja=0;
        Configuraciones.cierreCajaE=0;
        Configuraciones.cierreCajaT=0;

        JsonArray rootArray = Funciones.consultarBD(paramsJSON);

        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            String vE=rootArray.get(1).getAsJsonObject().get("vTotal").toString();
            String vT=rootArray.get(1).getAsJsonObject().get("vTarjeta").toString();
            vE=vE.replace("\"", "");
            vT=vT.replace("\"", "");
            try {
                Configuraciones.cierreCajaE = Double.valueOf(vE);
            }catch (NumberFormatException ex) {
                Configuraciones.cierreCajaE=0;
            }
            try {
                Configuraciones.cierreCajaT = Double.valueOf(vT);
            }catch (NumberFormatException ex) {
                Configuraciones.cierreCajaT=0;
            }
            Configuraciones.cierreCajaE -=Configuraciones.cierreCajaT;
        }



        Configuraciones.cierreCaja=Configuraciones.cierreCajaE+Configuraciones.cierreCajaT+Configuraciones.aperturaCaja;





        Map<String,Object> paramsAlert = new LinkedHashMap<>();
        paramsAlert.put("titulo", "Corte de caja");

        paramsAlert.put("vista", "/vista/corte_caja.fxml");

        Configuraciones.corteCajaValido = false;
        Configuraciones.abriendoCaja = false;
        Funciones.display(paramsAlert, getClass().getResource("/vista/corte_caja.fxml"), new CorteCaja() ,762, 418);
        return Configuraciones.corteCajaValido;
    }

}
