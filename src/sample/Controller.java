package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {

    @FXML
    private GridPane gridPane;

    @FXML
    private Slider slider;

    @FXML
    private Label tempoLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private ToggleButton muteButton;

    @FXML
    private Button resetButton;

    private boolean isStarted;

    private MidiPlayer midiPlayer = new MidiPlayer() {
        @Override
        public void beforeEach(int i, int tempo) {
            new Thread(() -> {
                List<Node> checkBoxList = gridPane.getChildren().filtered(node -> GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == i + 1).
                        stream().collect(Collectors.toList());
                checkBoxList.forEach(checkBox -> Platform.runLater(() -> checkBox.setStyle("-fx-background-color: red;")));
                try {
                    Thread.sleep(7500 / tempo);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkBoxList.forEach(checkBox -> Platform.runLater(() -> checkBox.setStyle("-fx-background-color: transparent;")));
            }).start();
        }
    };

    private void addCheckBoxesToGridPane(int columns) {
        for (int j = 1; j < columns + 1; j++) {
            final int index = j - 1;

            addNewHHCheckBox(j, index);
            addNewKDCheckBox(j, index);
            addNewSDCheckBox(j, index);
            addNewChoiceBox(j, index);
        }
    }

    private void addNewChoiceBox(int columnIndex, int index) {
        ChoiceBox<Integer> newChoiceBox = new ChoiceBox<>();
        ObservableList<Integer> observableList = FXCollections.observableArrayList(generateList());
        observableList.add(-1);
        newChoiceBox.setItems(observableList);
        gridPane.add(newChoiceBox, columnIndex, 3);
        newChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable4, oldValue4, newValue4) -> {
            midiPlayer.setPianoNotes(index, newValue4);
        });
    }

    private void addNewSDCheckBox(int columnIndex, int index) {
        CheckBox newCheckBox2 = new CheckBox();
        gridPane.add(newCheckBox2, columnIndex, 2);
        newCheckBox2.selectedProperty().addListener((observable3, oldValue3, newValue3) -> {
            midiPlayer.setSDNote(index, newValue3);
        });
    }

    private void addNewHHCheckBox(int columnIndex, final int index){
        CheckBox newCheckBox = new CheckBox();
        gridPane.add(newCheckBox, columnIndex, 0);
        newCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            midiPlayer.setHhNote(index, newValue);
        });
    }

    private void addNewKDCheckBox(int columnIndex, final int index){
        CheckBox newCheckBox1 = new CheckBox();
        gridPane.add(newCheckBox1, columnIndex, 1);
        newCheckBox1.selectedProperty().addListener((observable2, oldValue2, newValue2) -> {
            midiPlayer.setKDNote(index, newValue2);
        });
    }

    private List<Integer> generateList() {
        List<Integer> intList = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            intList.add(i);
        }
        return intList;
    }

    private void setDefaultTempo() {
        slider.setValue(midiPlayer.getTempo());
        tempoLabel.setText(midiPlayer.getTempo() + "");
    }

    private void sliderOnAction() {
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            new Thread(() -> {
                Platform.runLater(() -> tempoLabel.setText(newValue.intValue() + ""));
                midiPlayer.setTempo(newValue.intValue());
            }).start();

        });
    }

    private void startButtonOnAction() {
        startButton.setOnAction(event -> {
            if (!isStarted) {
                midiPlayer.start();
                isStarted = true;
            }
        });
    }

    private void stopButtonOnAction() {
        stopButton.setOnAction(event -> new Thread(() -> {
            midiPlayer.stop();
            isStarted = false;
        }).start());
    }

    private void muteButtonOnAction() {
        muteButton.setOnAction(event -> {
            new Thread(() -> {
                midiPlayer.setMute(!midiPlayer.isMute());
            }).start();
        });
    }

    private void resetButtonOnAction(){
        resetButton.setOnAction(event ->
        new Thread(() -> {
            List<CheckBox> checkBoxes = getNodeList(CheckBox.class).stream().map(node -> (CheckBox) node).collect(Collectors.toList());
            checkBoxes.forEach(checkBox -> Platform.runLater(() -> checkBox.setSelected(false)));
            List<ChoiceBox> choiceBoxes = getNodeList(ChoiceBox.class).stream().map(node -> (ChoiceBox) node).collect(Collectors.toList());
            choiceBoxes.forEach(choiceBox -> Platform.runLater(() -> choiceBox.setValue(-1)));}).start());
    }

    private List<Node> getNodeList(Class sClass){
        List<Node> nodeList = gridPane.getChildren().filtered(node -> sClass.isInstance(node));
        return nodeList;
    }

    public void initialize() {
        setDefaultTempo();
        sliderOnAction();
        addCheckBoxesToGridPane(32);
        muteButtonOnAction();
        stopButtonOnAction();
        startButtonOnAction();
        resetButtonOnAction();
    }
}
