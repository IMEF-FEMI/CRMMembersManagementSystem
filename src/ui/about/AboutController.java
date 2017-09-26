
package ui.about;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

public class AboutController implements Initializable {

    @FXML
    private MaterialDesignIconView closeIcon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void closeStage(MouseEvent event) {
        closeIcon.getScene().getWindow().hide();
    }
    
}
