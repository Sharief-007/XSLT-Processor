package app.xml.processor.events;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class Operations {
    private final TransformerFactory factory;
    public Operations() {
        factory = TransformerFactory.newInstance();
    }

    public void save(InputStream stream, Path filepath) throws IOException {
        Files.copy(stream, filepath, StandardCopyOption.REPLACE_EXISTING);
        stream.close();
    }

    public StringWriter transform(InputStream source, InputStream xsl) throws TransformerException {
        StringWriter writer = new StringWriter();
        Source xml = new StreamSource(source);
        Source xslt = new StreamSource(xsl);
        Transformer transformer = factory.newTransformer(xslt);
        transformer.transform(xml, new StreamResult(writer));
        return writer;
    }

    public void showAlert(Alert.AlertType type, String message){
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }

    public String getPrettyXML(String xmlData) throws Exception {
        StringBuilder builder = new StringBuilder();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 2);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);
        Source xmlInput = new StreamSource(new StringReader(xmlData));
        transformer.transform(xmlInput, xmlOutput);
        BufferedReader reader = new BufferedReader(new StringReader(xmlOutput.getWriter().toString()));
        reader.lines().forEach(line ->{
            if (! line.trim().isEmpty()) {
                builder.append(line);
                builder.append("\n");
            }
        });
        return builder.toString();
    }

    public File saveFileChooser(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"),
                new FileChooser.ExtensionFilter("XSL Files", "*.xsl"),
                new FileChooser.ExtensionFilter("XSLT Files", "*,xslt"),
                new FileChooser.ExtensionFilter("HTML Files","*.html"),
                new FileChooser.ExtensionFilter("TEXT Files","*.txt")
        );
        fileChooser.setTitle("Save File");
        return fileChooser.showSaveDialog(stage);
    }

    public File openFileChoser(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"),
                new FileChooser.ExtensionFilter("XSL Files", "*.xsl"),
                new FileChooser.ExtensionFilter("XSLT Files", "*,xslt")
        );
        fileChooser.setTitle("Open File");
        return fileChooser.showOpenDialog(stage);
    }
    public String showInputDialog(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Please enter Workarea name");
        dialog.setTitle("Input for Workarea");
        dialog.setContentText("Name");
        return dialog.showAndWait().orElseThrow(null);
    }

    public boolean showCloseDialog(){
        AtomicBoolean close = new AtomicBoolean(false);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to exit the Application ");
        Optional<ButtonType> button = alert.showAndWait();
        if(button.isPresent()) {
            if (button.get().getButtonData().isCancelButton()){
                close.set(true);
            }
        } else {
            close.set(true);
        }
        return close.get();
    }

    public void showDetailedError(String message, Exception ex){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(message);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        String exceptionText = stringWriter.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}
