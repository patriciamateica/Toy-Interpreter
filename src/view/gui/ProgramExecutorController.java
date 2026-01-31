package view.gui;

import controller.Controller;
import exceptions.MyException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.adt.stack.IStack;
import model.state.ProgramState;
import model.value.Value;
import model.statement.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgramExecutorController {

    private Controller controller;

    @FXML
    private TextField numberOfProgramStatesTextField;
    @FXML
    private TableView<Map.Entry<Integer, Value>> heapTableView;
    @FXML
    private TableColumn<Map.Entry<Integer, Value>, Integer> addressColumn;
    @FXML
    private TableColumn<Map.Entry<Integer, Value>, String> valueColumn;
    @FXML
    private ListView<String> outputListView;
    @FXML
    private ListView<String> fileTableListView;
    @FXML
    private ListView<Integer> programStateIdentifiersListView;
    @FXML
    private TableView<Map.Entry<String, Value>> symbolTableView;
    @FXML
    private TableColumn<Map.Entry<String, Value>, String> variableNameColumn;
    @FXML
    private TableColumn<Map.Entry<String, Value>, String> variableValueColumn;
    @FXML
    private ListView<String> executionStackListView;
    @FXML
    private Button runOneStepButton;
    @FXML
    private TableView<Map.Entry<Integer, Integer>> latchTableView;
    @FXML
    private TableColumn<Map.Entry<Integer, Integer>, Integer> latchLocationColumn;
    @FXML
    private TableColumn<Map.Entry<Integer, Integer>, Integer> latchValueColumn;


    public void setController(Controller controller) {
        this.controller = controller;
        populate();
    }

    @FXML
    public void initialize() {
        addressColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getKey()).asObject());
        valueColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue().toString()));
        variableNameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getKey()));
        variableValueColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue().toString()));
        latchLocationColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getKey()).asObject());
        latchValueColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getValue()).asObject());
    }

    private ProgramState getCurrentProgramState() {
        if (controller.getProgramStates().isEmpty())
            return null;
        int currentId = programStateIdentifiersListView.getSelectionModel().getSelectedIndex();
        if (currentId == -1)
            return controller.getProgramStates().get(0);
        return controller.getProgramStates().get(currentId);
    }

    private void populate() {
        populateHeap();
        populateOutput();
        populateFileTable();
        populateProgramStateIdentifiers();
        populateSymbolTable(getCurrentProgramState());
        populateExecutionStack(getCurrentProgramState());
        populateLatchTable();
    }

    @FXML
    private void changeProgramState(MouseEvent event) {
        populateExecutionStack(getCurrentProgramState());
        populateSymbolTable(getCurrentProgramState());
    }

    private void populateNumberOfProgramStates() {
        List<ProgramState> programStates = controller.getProgramStates();
        numberOfProgramStatesTextField.setText(String.valueOf(programStates.size()));
    }

    private void populateHeap() {
        if (!controller.getProgramStates().isEmpty()) {
            Map<Integer, Value> heap = controller.getProgramStates().get(0).getHeap().getContent();
            heapTableView.setItems(FXCollections.observableList(new ArrayList<>(heap.entrySet())));
        } else {
            heapTableView.getItems().clear();
        }
    }

    private void populateOutput() {
        if (!controller.getProgramStates().isEmpty()) {
            List<String> output = controller.getProgramStates().get(0).getOut().getAll().stream().map(Object::toString).collect(Collectors.toList());
            outputListView.setItems(FXCollections.observableList(output));
        } else {
            outputListView.getItems().clear();
        }
    }

    private void populateFileTable() {
        if (!controller.getProgramStates().isEmpty()) {
            List<String> files = new ArrayList<>(controller.getProgramStates().get(0).getFileTable().getContent().keySet());
            fileTableListView.setItems(FXCollections.observableArrayList(files));
        } else {
            fileTableListView.getItems().clear();
        }
    }

    private void populateProgramStateIdentifiers() {
        List<ProgramState> programStates = controller.getProgramStates();
        List<Integer> idList = programStates.stream().map(ProgramState::getId).collect(Collectors.toList());
        programStateIdentifiersListView.setItems(FXCollections.observableList(idList));
        populateNumberOfProgramStates();
    }

    private void populateSymbolTable(ProgramState givenProgramState) {
        if (givenProgramState != null) {
            Map<String, Value> symTable = givenProgramState.getSymTable().getContent();
            symbolTableView.setItems(FXCollections.observableList(new ArrayList<>(symTable.entrySet())));
        } else {
            symbolTableView.getItems().clear();
        }
    }

    private void populateExecutionStack(ProgramState givenProgramState) {
        if (givenProgramState != null) {
            IStack exeStack = givenProgramState.getExeStack();
            List<String> exeStackList = new ArrayList<>();
            for (Statement st : exeStack.getStatements()) {
                exeStackList.add(st.toString());
            }
            executionStackListView.setItems(FXCollections.observableList(exeStackList));
        } else {
            executionStackListView.getItems().clear();
        }
    }

    private void populateLatchTable() {
        if (!controller.getProgramStates().isEmpty()) {
            Map<Integer, Integer> latchTableContent = controller.getProgramStates().get(0).getLatchTable().getContent();
            latchTableView.setItems(FXCollections.observableList(new ArrayList<>(latchTableContent.entrySet())));
        } else {
            latchTableView.getItems().clear();
        }
    }

    @FXML
    void runOneStep(MouseEvent event) {
        if (controller != null) {
            try {
                List<ProgramState> programStates = controller.getProgramStates();
                if (programStates.size() > 0) {
                    controller.oneStepForAllPrg(programStates);
                    populate();
                }
            } catch (InterruptedException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error has occurred!");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error has occurred!");
            alert.setContentText("Controller is not initialized!");
            alert.showAndWait();
        }
    }
}
