package app.xml.processor.listeners;

import app.xml.processor.events.Operations;
import app.xml.processor.events.StageReadyEvent;
import app.xml.processor.fxml.FileLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer  implements ApplicationListener<StageReadyEvent> {
    private final FileLoader loader;
    private final Operations operations;
    private final Resource logo;
    public StageInitializer(FileLoader loader, Operations operations, @Value("${app.stage.icon}") Resource logo) {
        this.loader = loader;
        this.operations = operations;
        this.logo = logo;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = (Stage) event.getSource();
        loader.loadMainFxml(stage);
        setTitleAndIcon(stage,"XSLT Processor");
        stage.setOnCloseRequest(this::handleCloseEvent);
        stage.show();
    }

    public void setTitleAndIcon(Stage stage,String title){
        try {
            stage.setTitle(title);
            stage.getIcons().add(new Image(logo.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCloseEvent(WindowEvent event){
        boolean close = operations.showCloseDialog();
        if (close){
            event.consume();
        }
    }
}
