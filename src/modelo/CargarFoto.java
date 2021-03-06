package modelo;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

public class CargarFoto extends Task<Image> {

    private String url;


    public CargarFoto(String url) {
        this.url = url;
    }

    @Override
    protected Image call() {
        //System.out.println(url);
        return new Image(url.replace("#", "_"));

    }

    public String getUrl() {
        return url;
    }
}
