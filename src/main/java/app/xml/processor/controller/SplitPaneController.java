package app.xml.processor.controller;

import app.xml.processor.enums.TextBoxType;
import app.xml.processor.events.Operations;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
// import javafx.util.Duration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

import static app.xml.processor.enums.TextBoxType.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SplitPaneController {

    private final Operations operations;

    private Path sourceFilePath, mappingFilePath, targetFilepath;
    private final Tooltip saveTip, openFileTip, formatTip, runTip;

    @FXML
    private Label SOURCEFILEPATH;

    @FXML
    private TextArea SOURCEXML;

    @FXML
    private Label MAPPINGFILEPATH;

    @FXML
    private TextArea MAPPINGXST;

    @FXML
    private Label TARGETFILEPATH;

    @FXML
    private TextArea TARGETXML;

    @FXML
    private WebView WEBVIEW;

    @FXML
    private Button OPENSOURCEBTN;

    @FXML
    private Button SAVESOURCEBTN;

    @FXML
    private Button PRETTYBTN;

    @FXML
    private Button OPENMAPBTN;

    @FXML
    private Button SAVEMAPBTN;

    @FXML
    private Button RUN;

    @FXML
    private Button SAVETARGETBTN;

    public SplitPaneController(Operations operations) {
        this.operations = operations;
        this.saveTip = new Tooltip("Save file to disk");
        // this.saveTip.setShowDelay(Duration.seconds(1));
        this.openFileTip = new Tooltip("Open file from disk");
        // this.openFileTip.setShowDelay(Duration.seconds(1));
        this.runTip = new Tooltip("Run");
        // this.runTip.setShowDelay(Duration.seconds(1));
        this.formatTip = new Tooltip("Pretty print XML");
        // this.formatTip.setShowDelay(Duration.seconds(1));
    }

    @FXML
    void initialize(){
        SAVEMAPBTN.setTooltip(saveTip);
        SAVESOURCEBTN.setTooltip(saveTip);
        SAVETARGETBTN.setTooltip(saveTip);
        OPENMAPBTN.setTooltip(openFileTip);
        OPENSOURCEBTN.setTooltip(openFileTip);
        PRETTYBTN.setTooltip(formatTip);
        RUN.setTooltip(runTip);
    }

    @FXML
    void openMapping() {
        Stage stage = (Stage) MAPPINGXST.getScene().getWindow();
        try {
            File file = operations.openFileChoser(stage);
            if (Objects.nonNull(file)) {
                changeFilePath(file.toPath(),MAPPING);
                StringBuilder buffer = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                reader.lines().forEach(buffer::append);
                MAPPINGXST.setText(buffer.toString());
                reader.close();
            }
        }catch (IOException e){
            operations.showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }

    @FXML
    void openSource(ActionEvent event) {
        Stage stage = (Stage) SOURCEXML.getScene().getWindow();
        try {
            File file = operations.openFileChoser(stage);
            if (Objects.nonNull(file)) {
                changeFilePath(file.toPath(),SOURCE);
                StringBuilder buffer = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                reader.lines().forEach(buffer::append);
                SOURCEXML.setText(buffer.toString());
                reader.close();
            }
        }catch (IOException e){
            operations.showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }

    @FXML
    void prettyPrintSource(ActionEvent event) {
        try {
            String pretty = operations.getPrettyXML(SOURCEXML.getText());
            Platform.runLater(()-> SOURCEXML.setText(pretty));
        } catch (Exception e) {
            operations.showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }

    @FXML
    void runTransformation(ActionEvent event) {
        String xml = SOURCEXML.getText();
        String xsl = MAPPINGXST.getText();
        if (xml.trim().isEmpty()) {
            operations.showAlert(Alert.AlertType.INFORMATION,"Source Xml is Empty");
        }else if (xsl.trim().isEmpty()){
            operations.showAlert(Alert.AlertType.INFORMATION, "Mapping xsl is empty");
        }else {
            try {
                InputStream xmlInput = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
                InputStream xslInput = new ByteArrayInputStream(xsl.getBytes(StandardCharsets.UTF_8));
                StringWriter writer = operations.transform(xmlInput,xslInput);
                Platform.runLater(()->{
                    String output = writer.toString();
                    TARGETXML.setText(output);
                    WEBVIEW.getEngine().loadContent(output);
                });
            }catch (Exception e){
                operations.showDetailedError(e.getMessage(),e);
            }
        }
    }

    @FXML
    void saveMapping(ActionEvent event) {
        InputStream stream = new ByteArrayInputStream(MAPPINGXST.getText().getBytes(StandardCharsets.UTF_8));
        try {
            if (Objects.nonNull(mappingFilePath)) {
                operations.save(stream, mappingFilePath);
            }else {
                Stage stage = (Stage) MAPPINGXST.getScene().getWindow();
                File file = operations.saveFileChooser(stage);
                if (Objects.nonNull(file)){
                    operations.save(stream,file.toPath());
                    changeFilePath(file.toPath(),MAPPING);
                }
            }
        }catch (IOException e){
            operations.showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    void saveSource(ActionEvent event) {
        InputStream stream = new ByteArrayInputStream(SOURCEXML.getText().getBytes(StandardCharsets.UTF_8));
        try {
            if (Objects.nonNull(sourceFilePath)) {
                operations.save(stream, sourceFilePath);
            }else {
                Stage stage = (Stage) SOURCEXML.getScene().getWindow();
                File file = operations.saveFileChooser(stage);
                if (Objects.nonNull(file)){
                    operations.save(stream,file.toPath());
                    changeFilePath(file.toPath(),SOURCE);
                }
            }
        }catch (IOException e){
            operations.showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    void saveTarget(ActionEvent event) {
        InputStream stream = new ByteArrayInputStream(TARGETXML.getText().getBytes(StandardCharsets.UTF_8));
        try {
            if (Objects.nonNull(targetFilepath)){
                operations.save(stream, targetFilepath);
            }else {
                Stage stage = (Stage) TARGETXML.getScene().getWindow();
                File file = operations.saveFileChooser(stage);
                if (Objects.nonNull(file)){
                    operations.save(stream,file.toPath());
                    changeFilePath(file.toPath(), TARGET);
                }
            }
        }catch (IOException e){
            operations.showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    public void changeFilePath(Path path, TextBoxType type){
        switch (type){
            case SOURCE: {
                 sourceFilePath = path;
                 Platform.runLater(()->SOURCEFILEPATH.setText(path.getFileName().toString()));
                 break;
            }
            case MAPPING: {
                mappingFilePath = path;
                Platform.runLater(()->MAPPINGFILEPATH.setText(path.getFileName().toString()));
                break;
            }
            case TARGET: {
                targetFilepath = path;
                Platform.runLater(()->TARGETFILEPATH.setText(path.getFileName().toString()));
                break;
            }
        }
    }
}
