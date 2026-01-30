package view.gui;

import controller.Controller;
import javafx.application.Platform;
import model.adt.dictfile.FileTable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import exceptions.MyException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.state.ProgramState;
import model.adt.heap.IHeap;
import model.value.Value;
import model.adt.map.IMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

class Pair<T1, T2> {
    T1 first;
    T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
}

public class ProgramExecutorController {
    private Controller controller;

    @FXML
    private TextField numberOfProgramStatesTextField;

    @FXML
    private TableView<Pair<Integer, Value>> heapTableView;

    @FXML
    private TableColumn<Pair<Integer, Value>, Integer> addressColumn;

    @FXML
    private TableColumn<Pair<Integer, Value>, String> valueColumn;

    @FXML
    private ListView<String> outputListView;

    @FXML
    private ListView<String> fileTableListView;

    @FXML
    private ListView<Integer> programStateIdentifiersListView;

    @FXML
    private TableView<Pair<String, Value>> symbolTableView;

    @FXML
    private TableColumn<Pair<String, Value>, String> variableNameColumn;

    @FXML
    private TableColumn<Pair<String, Value>, String> variableValueColumn;

    @FXML
    private ListView<String> executionStackListView;

    @FXML
    private Button runOneStepButton;

    @FXML
    private TextArea outputTextArea;


    public void setController(Controller controller) {
        this.controller = controller;
        populate();
        refreshAllViews();
    }

    @FXML
    public void initialize() {
        programStateIdentifiersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        addressColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().first).asObject());
        valueColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().second.toString()));
        variableNameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().first));
        variableValueColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().second.toString()));
    }

    private ProgramState getCurrentProgramState() {
        List<ProgramState> states = controller.getProgramStates();
        if (states == null || states.isEmpty())
            return null;
        else {
            int currentId = programStateIdentifiersListView.getSelectionModel().getSelectedIndex();
            if (currentId == -1)
                return states.get(0);
            else
                return states.get(currentId);
        }
    }

    private void populate() {
        populateHeapTableView();
        populateOutputListView();
        populateFileTableListView();
        populateProgramStateIdentifiersListView();
        populateSymbolTableView();
        populateExecutionStackListView();
    }

    @FXML
    private void changeProgramState(MouseEvent event) {
        populateExecutionStackListView();
        populateSymbolTableView();
    }

    private void populateNumberOfProgramStatesTextField() {
        List<ProgramState> programStates = controller.getProgramStates();
        numberOfProgramStatesTextField.setText(String.valueOf(programStates == null ? 0 : programStates.size()));
    }

    private void populateHeapTableView() {
        ProgramState programState = getCurrentProgramState();
        if (programState == null) {
            heapTableView.setItems(FXCollections.observableArrayList(new ArrayList<>()));
            return;
        }
        IHeap<Value> heap = Objects.requireNonNull(programState).getHeap();
        ArrayList<Pair<Integer, Value>> heapEntries = new ArrayList<>();
        if (heap != null && heap.getContent() != null) {
            for (Map.Entry<Integer, Value> entry : heap.getContent().entrySet()) {
                heapEntries.add(new Pair<>(entry.getKey(), entry.getValue()));
            }
        }
        heapTableView.setItems(FXCollections.observableArrayList(heapEntries));
    }

    private void populateOutputListView() {
        ProgramState programState = getCurrentProgramState();
        List<String> output = new ArrayList<>();
        if (programState != null && programState.getOut() != null) {
            List<Value> outputList = programState.getOut().getAll();
            for (int index = 0; index < outputList.size(); index++) {
                output.add(outputList.get(index).toString());
            }
        }
        outputListView.setItems(FXCollections.observableArrayList(output));
    }

    private void populateFileTableListView() {
        ProgramState programState = getCurrentProgramState();
        List<String> files = new ArrayList<>();
        if (programState != null) {
            FileTable ft = programState.getFileTable();
            if (ft != null) {
                try {
                    java.lang.reflect.Method m = ft.getClass().getMethod("getContent");
                    Object content = m.invoke(ft);
                    if (content instanceof Map) {
                        for (Object key : ((Map) content).keySet()) files.add(String.valueOf(key));
                    }
                } catch (Exception ignored) {
                    try {
                        java.lang.reflect.Method m2 = ft.getClass().getMethod("getOpenFiles");
                        Object content = m2.invoke(ft);
                        if (content instanceof Map) {
                            for (Object key : ((Map) content).keySet()) files.add(String.valueOf(key));
                        }
                    } catch (Exception ignored2) {
                    }
                }
            }
        }
        fileTableListView.setItems(FXCollections.observableList(files));
    }

    private void populateProgramStateIdentifiersListView() {
        List<ProgramState> programStates = controller.getProgramStates();
        List<Integer> idList = new ArrayList<>();
        if (programStates != null) {
            idList = programStates.stream().map(ProgramState::getId).collect(Collectors.toList());
        }
        programStateIdentifiersListView.setItems(FXCollections.observableList(idList));
        populateNumberOfProgramStatesTextField();
    }

    private void populateSymbolTableView() {
        ProgramState programState = getCurrentProgramState();
        ArrayList<Pair<String, Value>> symbolTableEntries = new ArrayList<>();
        if (programState != null && programState.getSymTable() != null) {
            IMap<String, Value> symbolTable = Objects.requireNonNull(programState).getSymTable();
            Map<String, Value> content = symbolTable.getContent();
            if (content != null) {
                for (Map.Entry<String, Value> entry : content.entrySet()) {
                    symbolTableEntries.add(new Pair<>(entry.getKey(), entry.getValue()));
                }
            }
        }
        symbolTableView.setItems(FXCollections.observableArrayList(symbolTableEntries));
    }

    private void populateExecutionStackListView() {
        ProgramState programState = getCurrentProgramState();
        List<String> executionStackToString = new ArrayList<>();
        if (programState != null && programState.getExeStack() != null) {
            Object stack = programState.getExeStack();
            boolean filled = false;
            try {
                java.lang.reflect.Method m = stack.getClass().getMethod("getAll");
                Object res = m.invoke(stack);
                if (res instanceof Iterable) {
                    for (Object statement : (Iterable) res) executionStackToString.add(statement.toString());
                    filled = true;
                }
            } catch (Exception ignored) {
            }
            if (!filled) {
                try {
                    java.lang.reflect.Method m2 = stack.getClass().getMethod("get");
                    Object res2 = m2.invoke(stack);
                    if (res2 instanceof Iterable) {
                        for (Object statement : (Iterable) res2) executionStackToString.add(statement.toString());
                        filled = true;
                    }
                } catch (Exception ignored) {
                }
            }
            if (!filled) {
                executionStackToString.add(stack.toString());
            }
        }
        executionStackListView.setItems(FXCollections.observableList(executionStackToString));
    }

    @FXML
    private void runOneStep(MouseEvent mouseEvent) {
        if (controller != null) {
            try {
                List<ProgramState> programStates = Objects.requireNonNull(controller.getProgramStates());
                if (!programStates.isEmpty()) {
                    controller.oneStepForAllPrg(programStates);
                    populate();
                    programStates = controller.getProgramStates();
                    populateProgramStateIdentifiersListView();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("An error has occured!");
                    alert.setContentText("There is nothing left to execute!");
                    alert.showAndWait();
                }
            } catch (MyException | InterruptedException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Execution error!");
                alert.setHeaderText("An execution error has occured!");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("An error has occured!");
            alert.setContentText("No program selected!");
            alert.showAndWait();
        }
    }

    public void refreshAllViews() {
        if (controller == null) return;
        List<ProgramState> states = controller.getProgramStates();
        if (states == null || states.isEmpty()) {
            updateOutput("");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (ProgramState ps : states) {
            if (ps == null || ps.getOut() == null) continue;
            sb.append(ps.getOut().toString()).append(System.lineSeparator());
        }
        updateOutput(sb.toString().trim());
    }

    private void updateOutput(String text) {
        Platform.runLater(() -> {
            if (outputTextArea != null) outputTextArea.setText(text);
        });
    }
}
