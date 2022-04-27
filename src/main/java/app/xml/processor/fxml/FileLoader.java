package app.xml.processor.fxml;

import app.xml.processor.events.Operations;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FileLoader {
    private final Resource mainFxml,splitpaneFxml;
    private final ApplicationContext context;
    private final Operations operations;
    private final String styles;
    public FileLoader(@Value("${app.fxml.main}") Resource mainFxml,
                      @Value("${app.fxml.splitpane}") Resource splitpaneFxml,
                      @Value("${app.css.styles}") String styles,
                      ApplicationContext context, Operations operations) {
        this.styles = styles;
        this.mainFxml = mainFxml;
        this.splitpaneFxml = splitpaneFxml;
        this.context = context;
        this.operations = operations;
    }

    public void loadMainFxml(Stage stage){
        try {
            FXMLLoader loader = new FXMLLoader(mainFxml.getURL());
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Scene scene = new Scene(root);
//            scene.getStylesheets().add(styles);
            stage.setScene(scene);
        } catch (Exception e) {
            operations.showDetailedError(e.getMessage(), e);
            // operations.showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }

    public SplitPane loadWorkArea(){
        SplitPane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(splitpaneFxml.getURL());
            loader.setControllerFactory(context::getBean);
            pane = loader.load();
        } catch (IOException e) {
            operations.showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
        return pane;
    }
}
