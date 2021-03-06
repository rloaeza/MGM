package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jfoenix.controls.JFXPasswordField;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import modelo.Configuraciones;
import modelo.Funciones;
import modelo.Personal;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static modelo.Funciones.FreeSensor;


public class RelojChecador extends Controlador implements Initializable {

    ObservableList<Personal> listaPersonal;


    private int fid;



    @FXML
    private AnchorPane Pane;

    @FXML
    private Label Hora;

    @FXML
    private JFXPasswordField UsuarioEntrada;

    @FXML
    private ImageView imagenHuella;


    @FXML
    void entrar(ActionEvent event) throws IOException {

        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Personal: Lista con codigo");
        paramsJSON.put("idClinica", parametros.get(0).get("idClinica"));
        paramsJSON.put("codigo", UsuarioEntrada.getText());
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        Personal persona=null;
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int t = rootArray.size();
            for(int i = 1; i< t; i++) {
                 persona = new Gson().fromJson(rootArray.get(i).getAsJsonObject(), modelo.Personal.class);
            }
        }



        if(persona!=null) {
            paramsJSON.clear();
            paramsJSON.put("Actividad", "Reloj: Agregar");
            paramsJSON.put("idPersonal", persona.getIdPersonal());
            paramsJSON.put("fecha", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            rootArray = Funciones.consultarBD(paramsJSON);

            Map<String, Object> paramsAlert = new LinkedHashMap<>();
            paramsAlert.put("titulo", "Entrada / Salida");
            paramsAlert.put("texto", "Bienvenido " + persona.getApellidos() + ", " + persona.getNombre());
            paramsAlert.put("vista", "/vista/alert_box.fxml");
            Funciones.displayFP(paramsAlert, getClass().getResource("/vista/alert_box.fxml"), new AlertBox());


        }
        else {
            Map<String, Object> paramsAlert = new LinkedHashMap<>();
            paramsAlert.put("titulo", "Entrada / Salida");
            paramsAlert.put("texto", "Error en código");
            paramsAlert.put("vista", "/vista/alert_box.fxml");
            Funciones.displayFP(paramsAlert, getClass().getResource("/vista/alert_box.fxml"), new AlertBox());
        }

        UsuarioEntrada.setText("");

    }

    @Override
    public void init() {
        Funciones.inicializarFP();
        try {
            cargarDatos();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(Configuraciones.fpActivo) {
            fid = 0;
            int idLista=0;
            for (Personal p : listaPersonal) {
  /*
                byte[] b = new byte[2048];

                b = Base64.getDecoder().decode(p.getHuella0());
                FingerprintSensorEx.DBAdd(Configuraciones.mhDB, fid++, b);
                idHuellas.add(p.getIdPersonal());
*/
                agregarHuella(p.getHuella0(), idLista);
                agregarHuella(p.getHuella1(), idLista);
                agregarHuella(p.getHuella2(), idLista);
                agregarHuella(p.getHuella3(), idLista);
                agregarHuella(p.getHuella4(), idLista);

                idLista++;



            }
        }
    }





    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Hora.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        Hora.setText(new SimpleDateFormat("HH:mm").format(new Date()));
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(1000), ae -> {
            Hora.setText(new SimpleDateFormat("HH:mm").format(new Date()));
        } ));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();




        Timeline t2 = new Timeline(new KeyFrame(Duration.millis(1000), ae -> {

            if(!Configuraciones.fpActivo)
                return;

            Configuraciones.templateLen[0] = 2048;

            if (0 == (Configuraciones.ret = FingerprintSensorEx.AcquireFingerprint(Configuraciones.mhDevice, Configuraciones.imgbuf, Configuraciones.template, Configuraciones.templateLen))) {

                OnCatpureOK(Configuraciones.imgbuf);



                int[] fid = new int[1];
                int[] score = new int [1];
                int ret = FingerprintSensorEx.DBIdentify(Configuraciones.mhDB, Configuraciones.template, fid, score);
                if (ret == 0)
                {
                    //System.out.println("Identify succ, fid=" + fid[0] + ",score=" + score[0] +"\n");
                    UsuarioEntrada.setText(listaPersonal.get(fid[0]).getIdPersonal()+"");
                    try {
                        entrar(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    //System.out.println("Identify fail, errcode=" + ret + "\n");
                }
            }


        } ));
        t2.setCycleCount(Animation.INDEFINITE);
        t2.play();




    }
    private void OnCatpureOK(byte[] imgBuf)
    {
        try {
            Funciones.writeBitmap(imgBuf, Configuraciones.fpWidth, Configuraciones.fpHeight, "fingerprint.bmp");
            File f = new File("fingerprint.bmp");
            imagenHuella.setImage(new Image(f.toURI().toString()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void cargarDatos() throws IOException {

        listaPersonal = FXCollections.observableArrayList();

        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Personal: Lista");
        paramsJSON.put("idClinica", Configuraciones.idClinica);
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int t = rootArray.size();
            for(int i = 1; i< t; i++) {
                listaPersonal.add(new Gson().fromJson(rootArray.get(i).getAsJsonObject(), modelo.Personal.class) );
            }
        }


    }
}
