package app.xml.processor;

import app.xml.processor.events.StageReadyEvent;
import app.xml.processor.listeners.StageInitializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {
    ConfigurableApplicationContext ctxt;

    @Autowired
    StageInitializer listener;

    @Override
    public void init() throws Exception {
        this.ctxt = new SpringApplicationBuilder(XsltProcessorApplication.class).run();
    }

    @Override
    public void start(Stage stage) {
        ctxt.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() {
        ctxt.close();
        Platform.exit();
    }
}
