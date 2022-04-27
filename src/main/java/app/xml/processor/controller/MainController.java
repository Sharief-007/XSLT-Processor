package app.xml.processor.controller;

import app.xml.processor.events.Operations;
import app.xml.processor.fxml.FileLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MainController {

    @FXML
    private TabPane TABPANE;

    @FXML
    private ToggleGroup viewToggleGroup;

    private final FileLoader loader;
    private final Operations operations;

    public MainController(FileLoader loader, Operations operations) {
        this.loader = loader;
        this.operations = operations;
    }

    @FXML
    public void initialize(){
        SplitPane pane = loader.loadWorkArea();
        if (Objects.nonNull(pane)){
            Tab newTab = new Tab("Workarea");
            newTab.setContent(pane);
            TABPANE.getTabs().add(newTab);
        }
    }

    @FXML
    void closeAllTabs(ActionEvent event) {
        TABPANE.getTabs().removeAll(TABPANE.getTabs());
    }

    @FXML
    void closeCurrentTab(ActionEvent event) {
        if (TABPANE.getTabs().size()!=0){
            int index = TABPANE.getSelectionModel().getSelectedIndex();
            TABPANE.getTabs().remove(index);
        }
    }

    @FXML
    void createNewTab(ActionEvent event) {
        final String verticalView = "Horizantal View";
        SplitPane pane = loader.loadWorkArea();
        RadioMenuItem selected = (RadioMenuItem) viewToggleGroup.getSelectedToggle();
        if (Objects.nonNull(pane)){
            if (selected.getText().equals(verticalView)){
                pane.setOrientation(Orientation.VERTICAL);
            }
            try {
                String tabName = operations.showInputDialog();
                if (!tabName.trim().isEmpty()){
                    Tab newTab = new Tab(tabName);
                    newTab.setContent(pane);
                    TABPANE.getTabs().add(newTab);
                    TABPANE.getSelectionModel().select(newTab);
                }
            }catch (Exception ignored){ }
        }
    }

    @FXML
    void quit(ActionEvent event) {
        Stage stage = (Stage)TABPANE.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage,WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    void setHorizantalView(ActionEvent event) {
        TABPANE.setSide(Side.LEFT);
        TABPANE.getTabs().forEach(tab->{
            SplitPane pane = (SplitPane) tab.getContent();
            pane.setOrientation(Orientation.VERTICAL);
        });
    }

    @FXML
    void setVerticalView(ActionEvent event) {
        TABPANE.setSide(Side.BOTTOM);
        TABPANE.getTabs().forEach(tab->{
            SplitPane pane = (SplitPane) tab.getContent();
            pane.setOrientation(Orientation.HORIZONTAL);
        });
    }
}
