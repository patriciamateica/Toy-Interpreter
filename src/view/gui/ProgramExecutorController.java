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
import model.adt.lock.ILockTable;
import model.adt.stack.IStack;
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
    private TableView<Pair<Integer, Integer>> lockTableView;

    @FXML
    private TableColumn<Pair<Integer, Integer>, Integer> lockLocationColumn;

    @FXML
    private TableColumn<Pair<Integer, Integer>, Integer> lockValueColumn;

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
        lockLocationColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().first).asObject());
        lockValueColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().second).asObject());
    }

    private ProgramState getCurrentProgramState() {
        List<ProgramState> states = controller.getProgramStates();
        if (states == null || states.isEmpty()) {
            runOneStepButton.setDisable(true);
            return null;
        } else {
            runOneStepButton.setDisable(false);
        }
        int selectedId = programStateIdentifiersListView.getSelectionModel().getSelectedIndex();
        return selectedId == -1 ? states.get(0) : states.get(selectedId);
    }

    private void populate() {
        populateHeapTableView();
        populateOutputListView();
        populateFileTableListView();
        populateProgramStateIdentifiersListView();
        populateSymbolTableView();
        populateExecutionStackListView();
        populateLockTableView();
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
            heapTableView.setItems(FXCollections.observableArrayList());
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

    private void populateLockTableView() {
        ProgramState programState = getCurrentProgramState();
        if (programState == null) {
            lockTableView.setItems(FXCollections.observableArrayList());
            return;
        }
        ILockTable lockTable = programState.getLockTable();
        ArrayList<Pair<Integer, Integer>> lockTableEntries = new ArrayList<>();
        if (lockTable != null && lockTable.getContent() != null) {
            for (Map.Entry<Integer, Integer> entry : lockTable.getContent().entrySet()) {
                lockTableEntries.add(new Pair<>(entry.getKey(), entry.getValue()));
            }
        }
        lockTableView.setItems(FXCollections.observableArrayList(lockTableEntries));
    }

    private void populateOutputListView() {
        ProgramState programState = getCurrentProgramState();
        List<String> output = new ArrayList<>();
        if (programState != null && programState.getOut() != null) {
            for (Object item : programState.getOut().getAll()) {
                output.add(item.toString());
            }
        }
        outputListView.setItems(FXCollections.observableArrayList(output));
    }

    private void populateFileTableListView() {
        ProgramState programState = getCurrentProgramState();
        List<String> files = new ArrayList<>();
        if (programState != null) {
            FileTable fileTable = (FileTable) programState.getFileTable();
            if (fileTable != null) {
                // Assuming FileTable has a method to get all file names, e.g., keySet()
                // This part might need adjustment based on your FileTable implementation
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
            IMap<String, Value> symTable = programState.getSymTable();
            if (symTable.getContent() != null) {
                for (Map.Entry<String, Value> entry : symTable.getContent().entrySet()) {
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
            IStack exeStack = programState.getExeStack();

            List<model.statement.Statement> stackContents = new ArrayList<>();

            while (!exeStack.isEmpty()) {
                stackContents.add(exeStack.pop());
            }

            for (model.statement.Statement stmt : stackContents) {
                executionStackToString.add(stmt.toString());
            }

            for (int i = stackContents.size() - 1; i >= 0; i--) {
                exeStack.push(stackContents.get(i));
            }
        }
        executionStackListView.setItems(FXCollections.observableList(executionStackToString));
    }


    @FXML
    private void runOneStep(MouseEvent mouseEvent) {
        if (controller != null) {
            try {
                List<ProgramState> programStates = controller.getProgramStates();
                if (programStates != null && !programStates.isEmpty()) {
                    controller.oneStepForAllPrg(programStates);
                    populate();
                    refreshAllViews();
                }
            } catch (MyException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } catch (InterruptedException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }


    public void refreshAllViews() {
        if (controller == null) return;
        List<ProgramState> states = controller.getProgramStates();
        if (states == null || states.isEmpty()) {
            // Clear all views
            numberOfProgramStatesTextField.setText("0");
            heapTableView.setItems(FXCollections.observableArrayList());
            outputListView.setItems(FXCollections.observableArrayList());
            fileTableListView.setItems(FXCollections.observableArrayList());
            lockTableView.setItems(FXCollections.observableArrayList());
            programStateIdentifiersListView.setItems(FXCollections.observableArrayList());
            symbolTableView.setItems(FXCollections.observableArrayList());
            executionStackListView.setItems(FXCollections.observableArrayList());
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (ProgramState ps : states) {
            sb.append(ps.toString()).append("\n-----------------\n");
        }
        updateOutput(sb.toString().trim());
    }

    private void updateOutput(String text) {
        Platform.runLater(() -> {
            if (outputTextArea != null) {
                outputTextArea.setText(text);
            }
        });
    }
}
