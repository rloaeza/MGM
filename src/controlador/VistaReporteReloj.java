package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import modelo.Personal;
import modelo.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class VistaReporteReloj extends Controlador implements Initializable {


    @FXML
    private AnchorPane Pane;

    @FXML
    private JFXTreeTableView<VistaReporte> TablaReporte;

    @FXML
    private Label Titulo;

    @FXML
    private Label Descripcion;

    @FXML
    private JFXDatePicker FechaInicio;

    @FXML
    private JFXDatePicker FechaFin;

    @FXML
    private JFXComboBox<Personal> CBPersonal;

    Map<String,Object> paramsJSONReporte = new LinkedHashMap<>();
    private double gTotal;

    @FXML
    void generarReporte(ActionEvent event) throws IOException {
        paramsJSONReporte.clear();
        paramsJSONReporte.put("fechaInicial", FechaInicio.getValue());
        paramsJSONReporte.put("fechaFinal", FechaFin.getValue());

        paramsJSONReporte.put("idPersonal", CBPersonal.getValue().getIdPersonal());
        Descripcion.setText(

        "Vendedor: " +CBPersonal.getValue().toString() +
        " Periodo "+FechaInicio.getValue()+" a "  + FechaFin.getValue());

        cargarDatos();
    }


    @FXML
    void imprimir(ActionEvent event) {
        prepararPDF(true, false);
    }

    @FXML
    void descargar(ActionEvent event) {
        prepararPDF(false, true);
    }

    @FXML
    void aceptar(ActionEvent event) throws IOException {

        quitarVistas();
        Funciones.CargarVistaAnterior(Pane, getClass().getResource( parametros.get(0).get("vista").toString() ), new InicioAdministrador());

    }
    private ObservableList<VistaReporte> listaReporte;
    private String[] titulos;
    private void prepararPDF(boolean imprimir, boolean guardar) {
        String tituloAnterior = "";
        ArrayList<PDFvalores> valoresPDF = new ArrayList<>();
        Map<String, String> valorPDf = new LinkedHashMap<>();



        String predeterminados = "celdaTitulo="+(String) parametros.get(0).get("Titulo") +
                "@celdaDescripcion="+LocalDate.now();
        valoresPDF.add(new PDFvalores("-1", predeterminados) );


        int row = 0;
        for(VistaReporte fila: listaReporte) {


            String valor="";
            for(int col=0; col<titulos.length; col++) {

                String t = titulos[col].split(":")[0].replace(" ", "");
                String v = fila.getDato(t)==null?"":fila.getDato(t);
                valor += t+"="+v+"@";
                // System.out.print(row+", "+col+"="+valor+"\t");

            }
            valoresPDF.add(new PDFvalores(row+"" ,valor));
            row++;
        }


        File file = null;
        String destino=null;
        if(guardar) {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Portable Document Format(*.pdf)", "*.pdf"));
            file = fileChooser.showSaveDialog(Pane.getScene().getWindow());
            if(file == null)
                return;

            destino = file.getAbsolutePath();
        }
        try {
            Funciones.llenarPDF2((String) parametros.get(0).get("pdf"),  valoresPDF, imprimir, guardar?destino:null);
        } catch (Exception e) {
            e.printStackTrace();
        }



        /*
        ArrayList<PDFvalores> valoresPDF = new ArrayList<>();
        Map<String, String> valorPDf = new LinkedHashMap<>();

        int index=0;
        for(modelo.VistaReporte elemento : listaReporte) {


            for(String t: titulos) {
                String titulo = (t.split(":")[0]).replace(" ","");
                String valor = (valorPDf.get(titulo)==null?"":valorPDf.get(titulo)+"\n")+elemento.getDato(titulo);
                if((index+1)%Configuraciones.lineasPorReporte==0)
                    valor += "@";
                valorPDf.put(titulo, valor);
            }

            index++;


        }


        for(String t: titulos) {
            String titulo = (t.split(":")[0]).replace(" ","");
            valoresPDF.add(new PDFvalores(titulo, valorPDf.get(titulo)));
        }


        valoresPDF.add(new PDFvalores("Titulo", (String) parametros.get(0).get("Titulo")));
        valoresPDF.add(new PDFvalores("Informacion", Descripcion.getText()));
        valoresPDF.add(new PDFvalores("GTotal", gTotal+""));




        File file = null;
        String destino=null;
        if(guardar) {
            final FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Portable Document Format(*.pdf)", "*.pdf"));



            file = fileChooser.showSaveDialog(Pane.getScene().getWindow());
            if(file == null)
                return;

            destino = file.getAbsolutePath();


        }


        try {


            Funciones.llenarPDF((String) parametros.get(0).get("pdf"),  valoresPDF, imprimir, guardar?destino:null);
        } catch (Exception e) {
            e.printStackTrace();
        }


         */

    }

    @Override
    public void init() {

        listaReporte = FXCollections.observableArrayList();
        TreeItem<VistaReporte> root = new RecursiveTreeItem<>(listaReporte, RecursiveTreeObject::getChildren);

        titulos= (String[]) parametros.get(0).get("titulos");

        Titulo.setText((String) parametros.get(0).get("clinicaDescripcion"));

        double anchoTabla = TablaReporte.getPrefWidth();

        for(String t : titulos) {
            String titulo = t.split(":")[0];
            double ancho = Double.valueOf(t.split(":")[1]);
            String alineacion = t.split(":")[2];
            JFXTreeTableColumn<VistaReporte, String> column = new JFXTreeTableColumn<>(titulo);

            column.setPrefWidth(( (ancho*anchoTabla)/100) -1);
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getDato(titulo.replace(" ", ""))));
            column.setStyle("-fx-alignment: "+alineacion+";");
            TablaReporte.getColumns().add(column);
        }


        TablaReporte.setRoot(root);

        TablaReporte.setEditable(true);
        TablaReporte.setShowRoot(false);
        TablaReporte.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);

        Titulo.setText("Reporte personal");
        //FechaInicio.setValue(LocalDate.now().minusMonths(1) );
        FechaInicio.setValue(LocalDate.now() );
        FechaFin.setValue(LocalDate.now() );


        try {
            cargarDatosPersonal();
            generarReporte(null);
        } catch (IOException  e) {
            e.printStackTrace();
        }


        TablaReporte.setOnMouseClicked(new EventHandler<>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount()==1)
                    return;
                try {
                    Configuraciones.idVentaProductos = Integer.valueOf(TablaReporte.getSelectionModel().getSelectedItem().getValue().getDato("IdVenta"));
                    Map<String, Object> paramsVista = new LinkedHashMap<>();
                    paramsVista.put("idClinica", Configuraciones.idClinica);
                    paramsVista.put("vista", "/vista/venta_editar_unica.fxml");
                    Funciones.CargarVista((AnchorPane) Pane, getClass().getResource(paramsVista.get("vista").toString()), paramsVista, new VentaEditarUnica());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void cargarDatos() throws IOException {
        String actividad = (String) parametros.get(0).get("reporte");
        listaReporte.clear();
        paramsJSONReporte.put("Actividad", actividad);

        paramsJSONReporte.put("lu1", "9:00");
        paramsJSONReporte.put("ma1", "9:00");
        paramsJSONReporte.put("mi1", "9:00");
        paramsJSONReporte.put("ju1", "9:00");
        paramsJSONReporte.put("vi1", "9:00");
        paramsJSONReporte.put("sa1", "9:00");
        paramsJSONReporte.put("do1", "9:00");

        paramsJSONReporte.put("lu2", "19:00");
        paramsJSONReporte.put("ma2", "19:00");
        paramsJSONReporte.put("mi2", "19:00");
        paramsJSONReporte.put("ju2", "19:00");
        paramsJSONReporte.put("vi2", "19:00");
        paramsJSONReporte.put("sa2", "19:00");
        paramsJSONReporte.put("do2", "19:00");

        paramsJSONReporte.put("idClinica", Configuraciones.idClinica);
        JsonArray rootArray = Funciones.consultarBD(paramsJSONReporte);

        gTotal = 0;
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int t = rootArray.size();
            for(int i = 1; i< t; i++) {
                Map<String, Object> v = new LinkedHashMap<>();
                v= new Gson().fromJson(rootArray.get(i).getAsJsonObject(), v.getClass());



                listaReporte.add(new VistaReporte(v));
            }
        }
    }


    private void cargarDatosPersonal() throws IOException {

        ObservableList<Personal> listaPersonal = FXCollections.observableArrayList();

        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Personal: Lista con horarios");
        paramsJSON.put("idClinica", Configuraciones.idClinica);
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int t = rootArray.size();
            for(int i = 1; i< t; i++) {
                listaPersonal.add(new Gson().fromJson(rootArray.get(i).getAsJsonObject(), Personal.class) );
            }
        }

        CBPersonal.setItems(listaPersonal);
        CBPersonal.getSelectionModel().select(0);

    }

}
