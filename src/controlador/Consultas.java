package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import modelo.Tratamientos;
import modelo.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Consultas extends Controlador implements Initializable {

    File foto;

    @FXML
    private AnchorPane Pane;

    @FXML
    private Label Paciente;

    @FXML
    private JFXListView<Historial> ListaDeHistorial;

    @FXML
    private JFXTextArea Diagnostico;

    @FXML
    private JFXListView<Tratamientos> ListaDeTratamientos;


    @FXML
    private JFXButton BotonAgregarFoto;

    @FXML
    private JFXButton BotonAgregarTratamiento;

    @FXML
    private Button BotonCargarFoto;

    @FXML
    private Button BotonCrearHistorial;

    @FXML
    private Button BotonGuardarHistorial;

    @FXML
    private Button BotonFinalizar;

    @FXML
    private TilePane HistorialTratamientos;

    @FXML
    private JFXTextArea HistorialDiagnostico;

    @FXML
    private TilePane HistorialFotos;

    @FXML
    private ImageView ImagenCargar;


    @FXML
    void agregarFoto(ActionEvent event) throws IOException {
        if(foto !=null ) {

            //ListaDeHistorial.getSelectionModel().select(0);
            int idHistorial =ListaDeHistorial.getSelectionModel().getSelectedItem().getIdHistorial();
            int idPaciente = ListaDeHistorial.getSelectionModel().getSelectedItem().getIdPaciente();
            String nombre = idPaciente+"_"+foto.getName();


            Map<String,Object> paramsJSON = new LinkedHashMap<>();
            paramsJSON.put("Actividad", "Foto: Agregar");
            paramsJSON.put("archivo", nombre);
            paramsJSON.put("idHistorial", idHistorial);

            JsonArray rootArray = Funciones.consultarBD(paramsJSON);




            SubirFoto subirFoto = new SubirFoto(foto.getAbsolutePath(), nombre);
            subirFoto.setOnRunning(e->{
                BotonCargarFoto.setDisable(true);
                BotonAgregarFoto.setDisable(true);
                BotonAgregarFoto.setText("Subiendo foto...");
            });
            subirFoto.setOnSucceeded(e->{
                BotonCargarFoto.setDisable(false);
                BotonAgregarFoto.setDisable(false);
                BotonAgregarFoto.setText("Agregar foto");
                ImagenCargar.setImage(null);
                try {
                    cargarFotosHistorial();
                } catch (IOException e1) {

                }
            });
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(subirFoto);
            executorService.shutdown();





        }
    }

    @FXML
    void agregarTratamiento(ActionEvent event) throws IOException {
        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Tratamientos recetados: Agregar");
        paramsJSON.put("sesiones",  ListaDeTratamientos.getSelectionModel().getSelectedItem().getSesiones());
        paramsJSON.put("caducidad",  0);
        paramsJSON.put("costo",  ListaDeTratamientos.getSelectionModel().getSelectedItem().getCosto());
        paramsJSON.put("idTratamiento",  ListaDeTratamientos.getSelectionModel().getSelectedItem().getIdTratamiento());
        paramsJSON.put("idHistorial",  ListaDeHistorial.getSelectionModel().getSelectedItem().getIdHistorial());
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);

        cargarTratamientosHistorial();

    }

    @FXML
    void cargarFoto(ActionEvent event)  {
        final FileChooser fileChooser = new FileChooser();
        foto = fileChooser.showOpenDialog(Pane.getScene().getWindow()) ;
        if (foto != null) {
            Image image = new Image(foto.toURI().toString());
            ImagenCargar.setImage(image);
        }

    }

    @FXML
    void crearHistorial(ActionEvent event) throws IOException {
        activarConsulta(true);


        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Historial: Agregar");
        paramsJSON.put("fecha", parametros.get(0).get("fecha").toString());
        paramsJSON.put("descripcion", Diagnostico.getText());
        paramsJSON.put("idPaciente", parametros.get(0).get("idPaciente").toString());
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        cargarDatosHistorial();
        ListaDeHistorial.getSelectionModel().select(0);


    }

    @FXML
    void guardarHistorial(ActionEvent event) {
        actualizarDiagnostico();
    }

    @FXML
    void finalizar(ActionEvent event) throws IOException {
        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Citas: Finalizar");
        paramsJSON.put("idCita", parametros.get(0).get("idCita").toString());
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);

        parametros.remove(0);
        Funciones.CargarVistaAnterior(Pane, getClass().getResource( parametros.get(0).get("vista").toString() ), new InicioAdministrador());

    }



    private void activarConsulta(boolean b) {
        ListaDeTratamientos.setDisable(!b);
        Diagnostico.setDisable(!b);
        BotonAgregarFoto.setDisable(!b);


        BotonAgregarTratamiento.setDisable(!b);
        BotonCargarFoto.setDisable(!b);

        BotonGuardarHistorial.setDisable(!b);

        BotonCrearHistorial.setDisable(b);
    }

    private void cargarDatos() throws IOException {

        ObservableList<Tratamientos> listaTratamientos = FXCollections.observableArrayList();

        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Tratamientos: Lista con costo");
        paramsJSON.put("idClinica", parametros.get(0).get("idClinica").toString());
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int t = rootArray.size();
            for(int i = 1; i< t; i++) {
                listaTratamientos.add(new Gson().fromJson(rootArray.get(i).getAsJsonObject(), modelo.Tratamientos.class) );
            }
        }

        ListaDeTratamientos.setItems(listaTratamientos);

    }

    private void cargarDatosHistorial() throws IOException {

        ObservableList<Historial> listraHistorial = FXCollections.observableArrayList();

        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Historial: Listar");
        paramsJSON.put("idPaciente", parametros.get(0).get("idPaciente").toString());
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int t = rootArray.size();
            for(int i = 1; i< t; i++) {
                listraHistorial.add(new Gson().fromJson(rootArray.get(i).getAsJsonObject(), modelo.Historial.class) );
            }
        }

        ListaDeHistorial.setItems(listraHistorial);

    }
    private void cargarTratamientosHistorial() throws IOException {
        while (HistorialTratamientos.getChildren().size() > 0)
            HistorialTratamientos.getChildren().remove(0);
        Map<String, Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Tratamientos recetados: Listar");
        paramsJSON.put("idHistorial", ListaDeHistorial.getSelectionModel().getSelectedItem().getIdHistorial());
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if (rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt() > 0) {
            int t = rootArray.size();
            for (int i = 1; i < t; i++) {
                modelo.TratamientosRecetados tratamientosRecetados = new Gson().fromJson(rootArray.get(i).getAsJsonObject(), modelo.TratamientosRecetados.class);

                Button b = new Button(tratamientosRecetados.getNombre() + "("+tratamientosRecetados.getSesiones()+" de " + tratamientosRecetados.getSesionesTotales() +" sesiones)");
                b.setOnAction(e->{

                    try {
                        Map<String, Object> paramsAlert = new LinkedHashMap<>();
                        paramsAlert.put("titulo", tratamientosRecetados.getNombre());
                        paramsAlert.put("sesiones", tratamientosRecetados.getSesiones());
                        double costo = Double.valueOf(tratamientosRecetados.getCosto());
                        costo /= tratamientosRecetados.getSesionesTotales();
                        paramsAlert.put("cobro", costo);
                        paramsAlert.put("idTratamientoRecetado", tratamientosRecetados.getIdTratamientoRecetado());
                        paramsAlert.put("vista", "/vista/imagen_detalle.fxml");
                        paramsAlert.put("ancho", "468");
                        paramsAlert.put("alto", "300");
                        Funciones.displayBox(paramsAlert, getClass().getResource("/vista/tratamiento_recetado_detalle.fxml"), new TratamientoRecetadoDetalle());
                        cargarTratamientosHistorial();

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
                HistorialTratamientos.getChildren().add(b);
            }
        }
    }






    private void cargarFotosHistorial() throws IOException {
        while(HistorialFotos.getChildren().size()>0)
            HistorialFotos.getChildren().remove(0);
        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Foto: Listar");
        paramsJSON.put("idHistorial", ListaDeHistorial.getSelectionModel().getSelectedItem().getIdHistorial());
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int t = rootArray.size();
            for(int i = 1; i< t; i++) {
                modelo.HistorialFotos historialFotos =new Gson().fromJson(rootArray.get(i).getAsJsonObject(), modelo.HistorialFotos.class) ;
                CargarFoto cargarFoto = new CargarFoto(Funciones.getURLfoto(historialFotos.getArchivo()));
                cargarFoto.setOnSucceeded(e-> {

                    try {
                        ImageView imageView = new ImageView(cargarFoto.get());

                        imageView.setOnMouseClicked(event -> {

                                    try {
                                        Map<String, Object> paramsAlert = new LinkedHashMap<>();
                                        paramsAlert.put("url", cargarFoto.getUrl().replace("#", ""));
                                        paramsAlert.put("titulo", cargarFoto.getUrl().replace("#", ""));
                                        paramsAlert.put("vista", "/vista/imagen_detalle.fxml");
                                        Funciones.displayImage(paramsAlert, getClass().getResource("/vista/imagen_detalle.fxml"), new ImagenDetalle());
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                        );

                        imageView.setFitWidth(200);
                        imageView.setPreserveRatio(true);
                        HistorialFotos.getChildren().add(imageView);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    } catch (ExecutionException e1) {
                        e1.printStackTrace();
                    }
                });

                ExecutorService executorService = Executors.newFixedThreadPool(1);
                executorService.execute(cargarFoto);

                executorService.shutdown();

            }
        }



    }


    private void actualizarDiagnostico() {
        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Historial: Actualizar");
        paramsJSON.put("descripcion", Diagnostico.getText());
        paramsJSON.put("fecha", parametros.get(0).get("fecha").toString());
        paramsJSON.put("idPaciente", parametros.get(0).get("idPaciente").toString());
        try {
            JsonArray rootArray = Funciones.consultarBD(paramsJSON);
            cargarDatosHistorial();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        try {
            cargarDatos();
            cargarDatosHistorial();

            Paciente.setText(parametros.get(0).get("nombre").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
        Diagnostico.focusedProperty().addListener( (ov, oldv, newV) -> {
            if(!newV) {
                actualizarDiagnostico();
            }
        });
        */

        ListaDeHistorial.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                //activarConsulta(true);
                HistorialDiagnostico.setText(ListaDeHistorial.getSelectionModel().getSelectedItem().getDescripcion());
                try {
                    cargarTratamientosHistorial();
                    cargarFotosHistorial();
                } catch (IOException e) {

                }
            }
        });

    }


}
