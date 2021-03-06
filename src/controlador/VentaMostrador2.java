package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.zkteco.biometric.FingerprintSensorEx;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import modelo.*;
import modelo.Pacientes;
import modelo.Personal;
import modelo.VentaMostrador;

import javax.print.DocFlavor;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class VentaMostrador2 extends Controlador implements Initializable {
    private int nVenta=1;
    private int nVentaSelect = 1;
    private boolean pagoValido=false;
    private double ventaMostradorActual = -1;

    private ObservableList<Pacientes> listaDeClientes, listaDeClientesFiltrada;

    @FXML
    private AnchorPane Pane;

    private ArrayList<JFXTreeTableView<modelo.VentaMostrador>> TablasVentas = new ArrayList<>();

    private ArrayList<ObservableList<VentaMostrador>> listasVentasMostrador = new ArrayList<>();

    private ArrayList<Integer> listasVentaCliente = new ArrayList<>();

    private ArrayList<Integer> listasVentaVendedor = new ArrayList<>();


    @FXML
    private JFXComboBox<Personal> ListaDeVendedores;

    @FXML
    private JFXListView<ProductosConCosto> ListaDeProductos;

    @FXML
    private JFXListView<Pacientes> ListaDeClientes;

    @FXML
    private JFXTextField Busqueda;

    @FXML
    private JFXTextField BusquedaCliente;

    @FXML
    private JFXTextField Cantidad;

    @FXML
    private Label CantidadProductos;

    @FXML
    private Label Subtotal;



    @FXML
    private Label Total;


    @FXML
    private JFXTabPane Tabs;

    @FXML
    void TabCambiando(MouseEvent event) {
        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
        calcularTotal();
        actualizarCliente();
        seleccionarVendedor(listasVentaVendedor.get(nVentaSelect));


    }

    private void actualizarCliente() {
        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
        BusquedaCliente.setText("");
        ListaDeClientes.setItems(listaDeClientes);
        ListaDeClientes.getSelectionModel().clearSelection();

        if(listasVentaCliente.get(nVentaSelect)==-1) {
            BusquedaCliente.setText("");
            ListaDeClientes.getSelectionModel().clearSelection();
            return;
        }else {
            for(int index=0; index<ListaDeClientes.getItems().size(); index++) {
                if(listasVentaCliente.get(nVentaSelect) == ListaDeClientes.getItems().get(index).getIdPaciente()) {
                    ListaDeClientes.getSelectionModel().select(index);

                    return;
                }
            }
        }

    }

    @FXML
    void agregarTab(ActionEvent event) {
        Tab tab = new Tab();
        tab.setText("Mostrador");
        nVenta++;



        TablasVentas.add(cargarTabla(new JFXTreeTableView<>() ));

        listasVentaCliente.add(-1);
        tab.setContent(TablasVentas.get(TablasVentas.size()-1));
        Tabs.getTabs().add(tab);
        Tabs.getSelectionModel().select(Tabs.getTabs().size()-1);


        listasVentaVendedor.add(Configuraciones.idPersonal);
        seleccionarVendedor(Configuraciones.idPersonal);
        calcularTotal();
        actualizarCliente();


    }

    private  JFXTreeTableView<VentaMostrador> cargarTabla(JFXTreeTableView<modelo.VentaMostrador> fxTreeTableView) {


        JFXTreeTableColumn<modelo.VentaMostrador, ImageView> columnImagen = new JFXTreeTableColumn<>("Imágen");
        columnImagen.setPrefWidth(80);
        columnImagen.setCellValueFactory(param -> {
            ImageView iv = new ImageView(Funciones.sitio + "../fotos/productos/_P" + param.getValue().getValue().getIdProducto() + ".JPG");
            iv.setFitWidth(60);
            iv.setPreserveRatio(true);
            return new ReadOnlyObjectWrapper<>(iv);

        }
        );
        columnImagen.setStyle("-fx-alignment: CENTER;");






        JFXTreeTableColumn<modelo.VentaMostrador, String> columnCantidad = new JFXTreeTableColumn<>("Cant");
        columnCantidad.setPrefWidth(100);
        columnCantidad.setCellValueFactory((TreeTableColumn.CellDataFeatures<modelo.VentaMostrador, String> param) ->  {
            if (columnCantidad.validateValue(param)) {
                return new ReadOnlyObjectWrapper<String>(param.getValue().getValue().getCantidad());
            }
            else {
                return columnCantidad.getComputedValue(param);

            }
        });

        columnCantidad.setCellFactory((TreeTableColumn<modelo.VentaMostrador, String> param) ->
                new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder())
        );
        columnCantidad.setStyle("-fx-alignment: CENTER;");



        columnCantidad.setOnEditCommit((CellEditEvent<modelo.VentaMostrador, String> t) -> {

                    int index = t.getTreeTablePosition().getRow();
                    double costo = listasVentasMostrador.get(nVentaSelect).get(index).getCosto();

                    int cantidad = Integer.valueOf(t.getNewValue());
                    int idProducto = listasVentasMostrador.get(nVentaSelect).get(index).getIdProducto();
                    String producto = listasVentasMostrador.get(nVentaSelect).get(index).getProducto();

                    modelo.VentaMostrador vM = new modelo.VentaMostrador(idProducto, cantidad,producto,costo);
                    listasVentasMostrador.get(nVentaSelect).remove(index);
                    listasVentasMostrador.get(nVentaSelect).add( vM);
                    t.getTreeTableView().getSelectionModel().select(listasVentasMostrador.get(nVentaSelect).size()-1);

                    if(cantidad<=0) {
                        listasVentasMostrador.get(nVentaSelect).remove(listasVentasMostrador.get(nVentaSelect).size()-1);
                    }
                    calcularTotal();

                }
        );





        JFXTreeTableColumn<modelo.VentaMostrador, String> columnProducto = new JFXTreeTableColumn<>("Producto");
        columnProducto.setPrefWidth(380);
        columnProducto.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getProducto()));

        JFXTreeTableColumn<modelo.VentaMostrador, String> columnCosto = new JFXTreeTableColumn<>("Costo");
        columnCosto.setPrefWidth(150);
        columnCosto.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(Funciones.valorAmoneda(param.getValue().getValue().getCosto())));
        columnCosto.setStyle("-fx-alignment: CENTER;");


        JFXTreeTableColumn<modelo.VentaMostrador, String> columnSubTotal = new JFXTreeTableColumn<>("SubTotal");
        columnSubTotal.setPrefWidth(150);
        columnSubTotal.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(Funciones.valorAmoneda(param.getValue().getValue().getTotal()))

        );
        columnSubTotal.setStyle("-fx-alignment: CENTER;");



        listasVentasMostrador.add(FXCollections.observableArrayList());




        TreeItem<modelo.VentaMostrador> root = new RecursiveTreeItem<>(listasVentasMostrador.get(listasVentasMostrador.size()-1), RecursiveTreeObject::getChildren);



        fxTreeTableView.getColumns().addAll(columnImagen, columnCantidad, columnProducto, columnCosto, columnSubTotal);
        fxTreeTableView.setRoot(root);
        fxTreeTableView.setEditable(true);
        fxTreeTableView.setShowRoot(false);



        return fxTreeTableView;
    }

    @FXML
    void eliminarTab(ActionEvent event) {

        int index = Tabs.getSelectionModel().getSelectedIndex();
        Tabs.getTabs().remove(index);
        listasVentasMostrador.remove(index);
        TablasVentas.remove(index);

        listasVentaCliente.remove(index);
        listasVentaVendedor.remove(index);

        nVenta--;
        if(nVenta==1) {
            agregarTab(null);
        }

        calcularTotal();
        actualizarCliente();
        index = Tabs.getSelectionModel().getSelectedIndex();
        seleccionarVendedor(listasVentaVendedor.get(index));




    }
    @FXML
    void insertar(ActionEvent event) throws IOException {
        for(ProductosConCosto p : ListaDeProductos.getItems()) {
            if(Busqueda.getText().equalsIgnoreCase(p.getClave()) || Busqueda.getText().equalsIgnoreCase(p.getBarCode()))
            {
                agregarProducto(p);
                Busqueda.setText("");
                return;
            }
        }
        cargarDatos(Busqueda.getText());
        if(ListaDeProductos.getItems().size()==0) {
            Funciones.mostrarMSG("Error", "No existen productos con esa búsqueda", 2);
        }
    }




    @FXML
    void seleccionarPersonal(KeyEvent event) {
        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();

        listaDeClientesFiltrada = FXCollections.observableArrayList();
        if(BusquedaCliente.getText().isEmpty()) {
            ListaDeClientes.setItems(listaDeClientes);
            ListaDeClientes.getSelectionModel().clearSelection();
            listasVentaCliente.set(nVentaSelect, -1);
            return;
        }
        String patron = BusquedaCliente.getText().toUpperCase();
        for(Pacientes p : listaDeClientes) {
            if(p.getNombre().toUpperCase().contains(patron) || p.getApellidos().toUpperCase().contains(patron) )
            {
                listaDeClientesFiltrada.add(p);


            }
        }
        ListaDeClientes.setItems(listaDeClientesFiltrada);
        if(listaDeClientesFiltrada.size()>0) {
            ListaDeClientes.getSelectionModel().select(0);
            listasVentaCliente.set(nVentaSelect, listaDeClientesFiltrada.get(0).getIdPaciente() );
        }

    }

    @Override
    public void init() {
        idVistaActual = Math.random();
        sigoPresente();

        if(!Configuraciones.fpActivo) {
            try {
                Funciones.inicializarFP();
            } catch (Error|Exception e) {

            }
        }





        Timeline t2 = new Timeline(new KeyFrame(Duration.millis(1000), ae -> {

            if(!Configuraciones.fpActivo)
                return;

            Configuraciones.templateLen[0] = 2048;

            if (0 == (Configuraciones.ret = FingerprintSensorEx.AcquireFingerprint(Configuraciones.mhDevice, Configuraciones.imgbuf, Configuraciones.template, Configuraciones.templateLen))) {
                int[] fid = new int[1];
                int[] score = new int [1];
                int ret = FingerprintSensorEx.DBIdentify(Configuraciones.mhDB, Configuraciones.template, fid, score);
                if (ret == 0)
                {

                    Pacientes p =listaDeClientes.get(  idHuellas.get(fid[0])   ) ;
                    BusquedaCliente.setText("");
                    ListaDeClientes.setItems(listaDeClientes);
                    ListaDeClientes.getSelectionModel().clearSelection();
                    ListaDeClientes.getSelectionModel().select( p );

                    nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
                    Tabs.getTabs().get(nVentaSelect).setText(p.toString());
                    listasVentaCliente.set(nVentaSelect, p.getIdPaciente());


                    Map<String,Object> paramsAlert = new LinkedHashMap<>();
                    paramsAlert.put("titulo", "Bienvenido");
                    paramsAlert.put("texto", "Cliente: "+p.toString());
                    paramsAlert.put("tiempo", "3");
                    paramsAlert.put("vista", "/vista/alert_box.fxml");
                    try {
                        Funciones.displayFP(paramsAlert, getClass().getResource("/vista/alert_box.fxml"), new AlertBox() );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Map<String,Object> paramsAlert = new LinkedHashMap<>();
                    paramsAlert.put("titulo", "Error");
                    paramsAlert.put("texto", "Huella no válida");
                    paramsAlert.put("tiempo", "1");
                    paramsAlert.put("vista", "/vista/alert_box.fxml");
                    try {
                        Funciones.displayFP(paramsAlert, getClass().getResource("/vista/alert_box.fxml"), new AlertBox() );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("Identify fail, errcode=" + ret + "\n");
                }
            }




        }));

        addTimer(t2);
        t2.setCycleCount(Animation.INDEFINITE);

        t2.play();


        try {
            cargarVendedores();
            cargarDatos("");
            cargarClientes("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int idLista=0;

        for(Pacientes p : listaDeClientes) {
            agregarHuella(p.getHuella0(), idLista);
            agregarHuella(p.getHuella1(), idLista);
            agregarHuella(p.getHuella2(), idLista);
            agregarHuella(p.getHuella3(), idLista);
            agregarHuella(p.getHuella4(), idLista);
            idLista++;
        }


        agregarTab(null);
        ListaDeProductos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                agregarProducto(ListaDeProductos.getSelectionModel().getSelectedItem());
            }
        });

        ListaDeClientes.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
                listasVentaCliente.set(nVentaSelect, ListaDeClientes.getSelectionModel().getSelectedItem().getIdPaciente());
                Tabs.getTabs().get(nVentaSelect).setText(ListaDeClientes.getSelectionModel().getSelectedItem().toString());
            }
        });


        ListaDeVendedores.setOnAction(event -> {
                nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
                listasVentaVendedor.set(nVentaSelect, ListaDeVendedores.getSelectionModel().getSelectedItem().getIdPersonal());
        });






        ventaMostradorActual = Math.random();
        Configuraciones.ventaMostradorActual = ventaMostradorActual;

        Pane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            KeyCode kc = ke.getCode();

            if (kc == KeyCode.F12) {
                ke.consume();


                if(Configuraciones.ventaMostradorActual!=ventaMostradorActual)
                    return;

                try {
                    aceptarVenta(null);
                } catch (IOException  e) {
                    e.printStackTrace();
                }
            }

        });



    }

    private void cargarVendedores() {
        ListaDeVendedores.setItems(Datos.personal);
        seleccionarVendedor(Configuraciones.idPersonal);

    }

    private void seleccionarVendedor(int id) {
        ObservableList<Personal> p = ListaDeVendedores.getItems();
        for(Personal persona : p) {
            if(persona.getIdPersonal()==id) {
                ListaDeVendedores.getSelectionModel().select(persona);
                break;
            }
        }
    }

    @FXML
    void aceptarVenta(ActionEvent event) throws IOException {

        if(listasVentasMostrador.get(nVentaSelect).size()<=0 ) {
            Map<String,Object> paramsAlertImpresora = new LinkedHashMap<>();
            paramsAlertImpresora.put("titulo", "Error");
            paramsAlertImpresora.put("tiempo", "5");
            paramsAlertImpresora.put("vista", "/vista/alert_box.fxml");

            paramsAlertImpresora.put("texto", "No existen productos en la venta");
            Funciones.displayFP(paramsAlertImpresora, getClass().getResource("/vista/alert_box.fxml"), new AlertBox());


            return;
        }

        // Verificar que la impresora este seleccionada
        PrinterService printerService = new PrinterService();
        List<String> listPrinters = printerService.getPrinters();
        boolean impresoraValida = false;
        for(String printer : listPrinters) {
            if( printer.contains(Configuraciones.impresoraTicket)) {
                impresoraValida = true;

            }
        }

        Map<String,Object> paramsAlertImpresora = new LinkedHashMap<>();
        paramsAlertImpresora.put("titulo", "Error");
        paramsAlertImpresora.put("tiempo", "5");
        paramsAlertImpresora.put("vista", "/vista/alert_box.fxml");
        if(!impresoraValida) {
            paramsAlertImpresora.put("texto", "No existe la impresora: "+Configuraciones.impresoraTicket+ " en el sistema");

            Funciones.displayFP(paramsAlertImpresora, getClass().getResource("/vista/alert_box.fxml"), new AlertBox());

            return;
        }

        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
        Map<String,Object> paramsAlert = new LinkedHashMap<>();
        paramsAlert.put("titulo", "Pago de venta");

        paramsAlert.put("vista", "/vista/forma_pago.fxml");
        Configuraciones.ventaAceptada=false;

        Configuraciones.formaPagoMonto = Configuraciones.ventaMostradorTotal;
        Funciones.display(paramsAlert, getClass().getResource("/vista/forma_pago.fxml"), new FormaPago() ,818, 446);

        if(!Configuraciones.ventaAceptada)
            return;


        String cliente="Mostrador";
        int idPaciente = -1;

        if( ListaDeClientes.getSelectionModel().getSelectedIndex()!=-1) {


            Pacientes p = ListaDeClientes.getSelectionModel().getSelectedItem();
            cliente = p.getApellidos()+", "+p.getNombre();
            idPaciente = p.getIdPaciente();
        }


       // System.out.println("idPaciente="+idPaciente);
        ArrayList<PDFvalores> valoresPDF = new ArrayList<>();
        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Venta Productos: Agregar");
        paramsJSON.put("cantidadProductos", CantidadProductos.getText());
        paramsJSON.put("subtotal", Configuraciones.ventaMostradorSubTotal);
        paramsJSON.put("iva", "iva");
        paramsJSON.put("total", Configuraciones.ventaMostradorTotal);
        //paramsJSON.put("idPersonal", Configuraciones.idPersonal);
        paramsJSON.put("idPersonal", listasVentaVendedor.get(nVentaSelect));
        paramsJSON.put("idPersonalComision", listasVentaVendedor.get(nVentaSelect));
        paramsJSON.put("idPaciente", idPaciente);
        paramsJSON.put("tipoPago", Funciones.getFormaPago());
        paramsJSON.put("descuento", Configuraciones.descuento*100);
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);

        String strCantidades="";
        String strProductos="";
        String strCostosU="";
        String strCostoT="";
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {

            valoresPDF.add(new PDFvalores("total", Configuraciones.ventaMostradorTotal+""));
            valoresPDF.add(new PDFvalores("subtotal", Configuraciones.ventaMostradorSubTotal+""));

            int ultimoInsertado = rootArray.get(1).getAsJsonObject().get(Funciones.ultimoInsertado).getAsInt();


            int nVentaConsultado = rootArray.get(2).getAsJsonObject().get("nVenta").getAsInt();



            /**
             * Agregar cobros a la bd
             */


            for(Cobro c : Configuraciones.formaPagoCobros) {
                Map<String,Object> paramsJSON_FPC = new LinkedHashMap<>();
                paramsJSON_FPC.put("Actividad", "Cobros de venta: Agregar");
                paramsJSON_FPC.put("idVentaProductos", ultimoInsertado);
                paramsJSON_FPC.put("formaPago", c.getFormaPago());
                paramsJSON_FPC.put("monto", c.getMonto());
                paramsJSON_FPC.put("descripcion", c.getDescripcion());
                Funciones.consultarBD(paramsJSON_FPC);
            }




            String timeStamp = new SimpleDateFormat("dd/MM/YY HH:mm").format(Calendar.getInstance().getTime());


            String ticketSTR=Configuraciones.ticketTituloClinicaThermal+
                    "Fecha: "+ timeStamp +"\n"+
                    "Cliente: "+cliente+"\n"+
                    "Venta: "+ nVentaConsultado+ "\n\n"+
                    Funciones.nuevaLinea("Cant", "Producto", " C. U.", "Total");
            for(modelo.VentaMostrador ventaMostrador: listasVentasMostrador.get(nVentaSelect)) {
                Map<String,Object> paramsJSON2 = new LinkedHashMap<>();
                paramsJSON2.put("Actividad", "Venta Productos: Agregar detalles");
                paramsJSON2.put("cantidad", ventaMostrador.getCantidad());
                paramsJSON2.put("costo", ventaMostrador.getCosto());
                paramsJSON2.put("total", ventaMostrador.getTotal());
                paramsJSON2.put("idProducto", ventaMostrador.getIdProducto());
                paramsJSON2.put("idVentasProductos", ultimoInsertado);
                JsonArray rootArray2 = Funciones.consultarBD(paramsJSON2);


                strCantidades+=ventaMostrador.getCantidad()+"\n";
                strProductos+=ventaMostrador.getProducto()+"\n";
                strCostosU+=ventaMostrador.getCosto()+"\n";
                strCostoT+=ventaMostrador.getTotal()+"\n";

                ticketSTR = ticketSTR + "\n"+ Funciones.nuevaLinea(" "+ventaMostrador.getCantidad(), ventaMostrador.getProducto(), " "+ventaMostrador.getCosto()+"", Funciones.valorAmoneda(ventaMostrador.getTotal()));



            }
            valoresPDF.add(new PDFvalores("cantidad", strCantidades));
            valoresPDF.add(new PDFvalores("producto", strProductos));
            valoresPDF.add(new PDFvalores("costounitario", strCostosU));
            valoresPDF.add(new PDFvalores("costo", strCostoT));



            if(Configuraciones.descuento>0) {
                ticketSTR = ticketSTR + "\n\n"+ Funciones.nuevaLinea(" "+CantidadProductos.getText(), "producto(s)", "Total", Funciones.valorAmoneda(Configuraciones.ventaMostradorTotal));
                ticketSTR = ticketSTR + "\n"+ Funciones.nuevaLinea(" ", " ", "Desc", Funciones.valorAmoneda(Configuraciones.ventaMostradorTotal*Configuraciones.descuento));
                ticketSTR = ticketSTR + "\n"+ Funciones.nuevaLinea(" ", " ", "Total", Funciones.valorAmoneda(Configuraciones.ventaMostradorTotal - Configuraciones.ventaMostradorTotal*Configuraciones.descuento));
            } else  {
                ticketSTR = ticketSTR + "\n\n"+ Funciones.nuevaLinea(" "+CantidadProductos.getText(), "producto(s)", "Total", Funciones.valorAmoneda(Configuraciones.ventaMostradorTotal));
            }




            ticketSTR = ticketSTR + "\n\nMovimientos:\n"+Funciones.formaPago();

            ticketSTR = ticketSTR + "\n\nNo se hacen cambios ni devoluciones\n" +
                                    "¡Gracias por su compra!\n\n\n\n\n\n\n\n\n";


            valoresPDF.add(new PDFvalores("cliente", cliente));
            valoresPDF.add(new PDFvalores("clinica", Configuraciones.ticketTituloClinica));
            listasVentasMostrador.get(nVentaSelect).clear();
            limpiar(null);
            calcularTotal();


            BusquedaCliente.setText("");
            seleccionarPersonal(null);
            //Funciones.llenarPDF("formatos/venta2.pdf",  valoresPDF, false, "Venta.pdf");


            try {
                //printerService.printImage(Configuraciones.impresoraTicket, "formatos/mgm_t.png", DocFlavor.INPUT_STREAM.PNG);
                printerService.printString(Configuraciones.impresoraTicket, ticketSTR);

                byte[] cutP = new byte[]{0x1d, 'V', 1};
                printerService.printBytes(Configuraciones.impresoraTicket, cutP);


                byte[] openCashDrawer = new byte[]{27, 112, 48, 55, 121};
                printerService.printBytes(Configuraciones.impresoraTicket, openCashDrawer);
            } catch (Exception e) {
                paramsAlertImpresora.put("texto", "Error en la impresora :(, "+Configuraciones.impresoraTicket);
                Funciones.displayFP(paramsAlertImpresora, getClass().getResource("/vista/alert_box.fxml"), new AlertBox() );
                return;
            }





            //Funciones.llenarPDF("formatos/venta2.pdf", valoresPDF, true, null);

            Datos.cargarProductosConCosto();
            cargarDatos("");

        }

        pagoValido = false;



    }


    @FXML
    void agregar_quitar(KeyEvent event) {
        if(event.getText().contains("+")) {
            if(!ListaDeProductos.getSelectionModel().isEmpty())
                agregarProducto(ListaDeProductos.getSelectionModel().getSelectedItem());

        }
    }

    @FXML
    void seleccionarCliente(ActionEvent event) throws IOException {
        if( ListaDeClientes.getSelectionModel().isEmpty()) {
            String nombrePaciente = BusquedaCliente.getText();

            Map<String, Object> paramsAlert = new LinkedHashMap<>();
            paramsAlert.put("titulo", "Crear nuevo usuaro");

            paramsAlert.put("texto", "¿Crear el paciente " + nombrePaciente + "?");
            paramsAlert.put("vista", "/vista/alert_box_confirmar.fxml");
            Configuraciones.alertBoxConfirmar = false;
            Funciones.display(paramsAlert, getClass().getResource("/vista/alert_box_confirmar.fxml"), new AlertBoxConfirmar(), 500, 250);
            if( Configuraciones.alertBoxConfirmar) {
                Map<String, Object> paramsJSON = new LinkedHashMap<>();
                paramsJSON.put("Actividad", "Pacientes: Agregar");
                paramsJSON.put("nombre", nombrePaciente);
                paramsJSON.put("apellidos", "");
                paramsJSON.put("email", "");
                paramsJSON.put("telefono", "");
                paramsJSON.put("movil", "");
                paramsJSON.put("clave", "");
                paramsJSON.put("huella0", "");
                paramsJSON.put("huella1", "");
                paramsJSON.put("huella2", "");
                paramsJSON.put("huella3", "");
                paramsJSON.put("huella4", "");
                paramsJSON.put("idClinica", Configuraciones.idClinica);
                JsonArray rootArray = Funciones.consultarBD(paramsJSON);
                Datos.cargarPacientes();
                listaDeClientes = Datos.buscarPacientes(nombrePaciente);
                seleccionarPersonal(null);



                nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
                listasVentaCliente.set(nVentaSelect, ListaDeClientes.getSelectionModel().getSelectedItem().getIdPaciente());
                Tabs.getTabs().get(nVentaSelect).setText(ListaDeClientes.getSelectionModel().getSelectedItem().toString());

            }

        } else {
            nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
            listasVentaCliente.set(nVentaSelect, ListaDeClientes.getSelectionModel().getSelectedItem().getIdPaciente());
            Tabs.getTabs().get(nVentaSelect).setText(ListaDeClientes.getSelectionModel().getSelectedItem().toString());
        }
    }

    @FXML
    void eliminar(ActionEvent event) {
        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
        //System.out.println("tab="+nVentaSelect+", fila="+TablasVentas.get(nVentaSelect).getSelectionModel().getSelectedIndex());
        if(!TablasVentas.get(nVentaSelect).getSelectionModel().isEmpty()) {
            listasVentasMostrador.get(nVentaSelect).remove(TablasVentas.get(nVentaSelect).getSelectionModel().getSelectedIndex());
            TablasVentas.get(nVentaSelect).getSelectionModel().clearSelection();
            calcularTotal();
        }

    }

    @FXML
    void limpiar(ActionEvent event) {
        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
        listasVentasMostrador.get(nVentaSelect).clear();
        ListaDeClientes.getSelectionModel().clearSelection();
        listasVentaCliente.set(nVentaSelect,-1);
        listasVentaVendedor.set(nVentaSelect, Configuraciones.idPersonal);
        Tabs.getTabs().get(nVentaSelect).setText("Mostrador");
        actualizarCliente();
        calcularTotal();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {




    }

    private void cargarDatos(String patron) throws IOException {

        ListaDeProductos.setItems(Datos.buscarProductosConCosto(patron));
    }

    private void cargarClientes(String patron) throws IOException {

        listaDeClientes = FXCollections.observableArrayList();
        listaDeClientes = Datos.buscarPacientes(patron);

    }


    private void calcularTotal() {
        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();

        double subtotal=0;
        double total;
        int cantidadProductos=0;

        for(modelo.VentaMostrador producto: listasVentasMostrador.get(nVentaSelect)) {
            subtotal+=producto.getTotal();
            cantidadProductos+= producto.getCant();
        }
        total = subtotal;
        CantidadProductos.setText(cantidadProductos+"");


        Configuraciones.ventaMostradorSubTotal = subtotal;
        Configuraciones.ventaMostradorTotal = total;

        Subtotal.setText(Funciones.valorAmoneda(Funciones.fixN(Configuraciones.ventaMostradorSubTotal,2)));
        Total.setText(Funciones.valorAmoneda(Funciones.fixN(Configuraciones.ventaMostradorTotal,2)));

    }

    private void agregarProducto(ProductosConCosto p) {


        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
        int cantidad=0;
        int cantidad2 =Cantidad.getText().isEmpty()?1:Integer.valueOf(Cantidad.getText());
        for(modelo.VentaMostrador producto : listasVentasMostrador.get(nVentaSelect)) {
            if(producto.getIdProducto()==p.getIdProducto()) {
                cantidad = producto.getCant();

                if( p.getExistencia()< (cantidad+cantidad2) && (p.getTratamiento()==0) )
                {
                    Map<String,Object> paramsAlert = new LinkedHashMap<>();
                    paramsAlert.put("titulo", "Almacen");
                    paramsAlert.put("vista", "/vista/alert_box.fxml");



                    paramsAlert.put("texto", "Falta producto: " + p.getNombre()+ ", en almacen se cuenta con: "+ p.getExistencia() +" piezas" );

                    try {
                        Funciones.displayFP(paramsAlert, this.getClass().getResource("/vista/alert_box.fxml"), new AlertBox() );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }






                listasVentasMostrador.get(nVentaSelect).remove(producto);
                break;
            }
        }

        if( p.getExistencia()< (cantidad+cantidad2) && (p.getTratamiento()==0))
        {
            Map<String,Object> paramsAlert = new LinkedHashMap<>();
            paramsAlert.put("titulo", "Almacen");
            paramsAlert.put("vista", "/vista/alert_box.fxml");



            paramsAlert.put("texto", "Falta producto: " + p.getNombre()+ ", en almacen se cuenta con: "+ p.getExistencia() +" piezas" );

            try {
                Funciones.displayFP(paramsAlert, this.getClass().getResource("/vista/alert_box.fxml"), new AlertBox() );
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }




        listasVentasMostrador.get(nVentaSelect).add(new modelo.VentaMostrador(
                p.getIdProducto(),
                cantidad+cantidad2,
                p.getNombre(),
                p.getPrecio()
        ));
        calcularTotal();
        TablasVentas.get(nVentaSelect).getSelectionModel().select(listasVentasMostrador.get(nVentaSelect).size()-1);
    }

}
