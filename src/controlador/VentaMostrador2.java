package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import modelo.*;
import modelo.VentaMostrador;

import java.awt.print.PrinterException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class VentaMostrador2 extends Controlador implements Initializable {
    private int nVenta=1;
    private int nVentaSelect = 1;
    private boolean pagoValido=false;

    @FXML
    private AnchorPane Pane;

    private ArrayList<JFXTreeTableView<modelo.VentaMostrador>> TablasVentas = new ArrayList<>();


    private ArrayList<ObservableList<VentaMostrador>> listasVentasMostrador = new ArrayList<>();



    @FXML
    private JFXTreeTableView<modelo.VentaMostrador> TablaVenta;


    @FXML
    private JFXListView<ProductosConCosto> ListaDeProductos;

    @FXML
    private JFXTextField Busqueda;

    @FXML
    private JFXTextField Cantidad;

    @FXML
    private Label CantidadProductos;

    @FXML
    private Label Subtotal;

    @FXML
    private Label IVA;

    @FXML
    private Label Total;


    @FXML
    private JFXTextField FormaPagoAuxiliar;

    @FXML
    private Label FormaPagoRespuesta;

    @FXML
    private JFXTabPane Tabs;

    @FXML
    void TabCambiando(MouseEvent event) {
        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
        calcularTotal();
    }


    @FXML
    void agregarTab(ActionEvent event) {
        Tab tab = new Tab();
        tab.setText("Venta "+nVenta++);



        TablasVentas.add(cargarTabla(new JFXTreeTableView<>() ));

        tab.setContent(TablasVentas.get(TablasVentas.size()-1));

        Tabs.getTabs().add(tab);
        Tabs.getSelectionModel().select(Tabs.getTabs().size()-1);
        calcularTotal();


    }

    private  JFXTreeTableView<VentaMostrador> cargarTabla(JFXTreeTableView<modelo.VentaMostrador> fxTreeTableView) {

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



        JFXTreeTableColumn<modelo.VentaMostrador, String> columnProducto = new JFXTreeTableColumn<>("Producto");
        columnProducto.setPrefWidth(380);
        columnProducto.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getProducto()));

        JFXTreeTableColumn<modelo.VentaMostrador, Double> columnCosto = new JFXTreeTableColumn<>("Costo");
        columnCosto.setPrefWidth(150);
        columnCosto.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().getCosto()));
        columnCosto.setStyle("-fx-alignment: CENTER;");


        JFXTreeTableColumn<modelo.VentaMostrador, Double> columnSubTotal = new JFXTreeTableColumn<>("SubTotal");
        columnSubTotal.setPrefWidth(150);
        columnSubTotal.setCellValueFactory((TreeTableColumn.CellDataFeatures<modelo.VentaMostrador, Double> param) ->
                new ReadOnlyObjectWrapper<>(param.getValue().getValue().getTotal())

        );
        columnSubTotal.setStyle("-fx-alignment: CENTER;");



        listasVentasMostrador.add(FXCollections.observableArrayList());




        TreeItem<modelo.VentaMostrador> root = new RecursiveTreeItem<>(listasVentasMostrador.get(listasVentasMostrador.size()-1), RecursiveTreeObject::getChildren);



        fxTreeTableView.getColumns().addAll(columnCantidad, columnProducto, columnCosto, columnSubTotal);
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
        nVenta--;
        if(nVenta==1) {
            agregarTab(null);
        }

        calcularTotal();




    }
    @FXML
    void insertar(ActionEvent event) {
        for(ProductosConCosto p : ListaDeProductos.getItems()) {
            if(Busqueda.getText().equalsIgnoreCase(p.getClave()) || Busqueda.getText().equalsIgnoreCase(p.getBarCode()))
            {
                agregarProducto(p);
                Busqueda.setText("");
                return;
            }
        }
    }






    private ObservableList<modelo.VentaMostrador> listaVentaMostrador;

    @Override
    public void init() {
        try {
            cargarDatos();
        } catch (IOException e) {
            e.printStackTrace();
        }



        /*
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



        JFXTreeTableColumn<modelo.VentaMostrador, String> columnProducto = new JFXTreeTableColumn<>("Producto");
        columnProducto.setPrefWidth(380);
        columnProducto.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getProducto()));

        JFXTreeTableColumn<modelo.VentaMostrador, Double> columnCosto = new JFXTreeTableColumn<>("Costo");
        columnCosto.setPrefWidth(150);
        columnCosto.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().getCosto()));
        columnCosto.setStyle("-fx-alignment: CENTER;");


        JFXTreeTableColumn<modelo.VentaMostrador, Double> columnSubTotal = new JFXTreeTableColumn<>("SubTotal");
        columnSubTotal.setPrefWidth(150);
        columnSubTotal.setCellValueFactory((TreeTableColumn.CellDataFeatures<modelo.VentaMostrador, Double> param) ->
                 new ReadOnlyObjectWrapper<>(param.getValue().getValue().getTotal())

        );
        columnSubTotal.setStyle("-fx-alignment: CENTER;");

        columnCantidad.setStyle("-fx-alignment: CENTER;");
        columnCantidad.setOnEditCommit((CellEditEvent<modelo.VentaMostrador, String> t) -> {

                    int index = t.getTreeTablePosition().getRow();
                    double costo = Double.valueOf(listaVentaMostrador.get(index).getCosto());

                    int cantidad = Integer.valueOf(t.getNewValue());
                    int idProducto = listaVentaMostrador.get(index).getIdProducto();
                    String producto = listaVentaMostrador.get(index).getProducto();

                    modelo.VentaMostrador vM = new modelo.VentaMostrador(idProducto, cantidad,producto,costo);
                    listaVentaMostrador.remove(index);
                    listaVentaMostrador.add( vM);
                    t.getTreeTableView().getSelectionModel().select(listaVentaMostrador.size()-1);

                    if(cantidad<=0) {
                        listaVentaMostrador.remove(listaVentaMostrador.size()-1);
                    }
                    calcularTotal();

                }
        );



        listaVentaMostrador = FXCollections.observableArrayList();



        TreeItem<modelo.VentaMostrador> root = new RecursiveTreeItem<>(listaVentaMostrador, RecursiveTreeObject::getChildren);



        TablaVenta.getColumns().addAll(columnCantidad, columnProducto, columnCosto, columnSubTotal);
        TablaVenta.setRoot(root);
        TablaVenta.setEditable(true);
        TablaVenta.setShowRoot(false);



        */


        agregarTab(null);
        ListaDeProductos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                agregarProducto(ListaDeProductos.getSelectionModel().getSelectedItem());
            }
        });





    }



    @FXML
    void aceptar(ActionEvent event) throws IOException, PrinterException {
        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
        Map<String,Object> paramsAlert = new LinkedHashMap<>();
        paramsAlert.put("titulo", "Error");

        paramsAlert.put("vista", "/vista/forma_pago.fxml");
        Configuraciones.ventaAceptada=false;
        Configuraciones.ventaPago = Double.valueOf(Total.getText());
        Funciones.display(paramsAlert, getClass().getResource("/vista/forma_pago.fxml"), new FormaPago() ,818, 311);

        if(!Configuraciones.ventaAceptada)
            return;







        ArrayList<PDFvalores> valoresPDF = new ArrayList<>();
        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Venta Productos: Agregar");
        paramsJSON.put("cantidadProductos", CantidadProductos.getText());
        paramsJSON.put("subtotal", Subtotal.getText());
        paramsJSON.put("iva", "iva");
        paramsJSON.put("total", Total.getText());
        paramsJSON.put("idPersonal", 1);
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);

        String strCantidades="";
        String strProductos="";
        String strCostosU="";
        String strCostoT="";
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {

            valoresPDF.add(new PDFvalores("total", Total.getText()));
            valoresPDF.add(new PDFvalores("subtotal", Subtotal.getText()));

            int ultimoInsertado = rootArray.get(1).getAsJsonObject().get(Funciones.ultimoInsertado).getAsInt();

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

            }
            valoresPDF.add(new PDFvalores("cantidad", strCantidades));
            valoresPDF.add(new PDFvalores("producto", strProductos));
            valoresPDF.add(new PDFvalores("costounitario", strCostosU));
            valoresPDF.add(new PDFvalores("costo", strCostoT));

            valoresPDF.add(new PDFvalores("cliente", "Mostrador"));
            listasVentasMostrador.get(nVentaSelect).clear();
            calcularTotal();


            //Funciones.llenarPDF("formatos/venta2.pdf",  valoresPDF, false, "Venta.pdf");

            Funciones.llenarPDF("formatos/venta2.pdf",  valoresPDF, true, null);

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
    void eliminar(ActionEvent event) {
        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
        System.out.println("tab="+nVentaSelect+", fila="+TablasVentas.get(nVentaSelect).getSelectionModel().getSelectedIndex());
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
        calcularTotal();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void cargarDatos() throws IOException {

        ObservableList<ProductosConCosto> listaDeProductos = FXCollections.observableArrayList();

        Map<String,Object> paramsJSON = new LinkedHashMap<>();
        paramsJSON.put("Actividad", "Productos: Lista con precio");
        paramsJSON.put("idClinica", parametros.get(0).get("idClinica").toString());
        JsonArray rootArray = Funciones.consultarBD(paramsJSON);
        if(rootArray.get(0).getAsJsonObject().get(Funciones.res).getAsInt()>0) {
            int t = rootArray.size();
            for(int i = 1; i< t; i++) {
                listaDeProductos.add(new Gson().fromJson(rootArray.get(i).getAsJsonObject(), ProductosConCosto.class) );
            }
        }

        ListaDeProductos.setItems(listaDeProductos);

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
        Subtotal.setText(Funciones.fixN(subtotal, 2)+"");


        Total.setText(Funciones.fixN(total,2)+"");
    }

    private void agregarProducto(ProductosConCosto p) {
        nVentaSelect = Tabs.getSelectionModel().getSelectedIndex();
        int cantidad=0;
        for(modelo.VentaMostrador producto : listasVentasMostrador.get(nVentaSelect)) {
            if(producto.getIdProducto()==p.getIdProducto()) {
                cantidad = producto.getCant();
                listasVentasMostrador.get(nVentaSelect).remove(producto);
                break;
            }
        }

        int cantidad2 =Cantidad.getText().isEmpty()?1:Integer.valueOf(Cantidad.getText());
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
