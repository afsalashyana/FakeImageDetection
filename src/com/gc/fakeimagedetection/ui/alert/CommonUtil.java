package com.gc.fakeimagedetection.ui.alert;

import com.gc.fakeimagedetection.ui.Main;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CommonUtil {

    private static final String ICON = "/resources/icon/icon.png";

    public static void attachIcon(Stage stage) {
        stage.getIcons().add(new Image(Main.class.getResourceAsStream(ICON)));
    }
}
