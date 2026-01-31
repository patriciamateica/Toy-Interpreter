package view.gui;

import controller.Controller;
import exceptions.MyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.adt.dictfile.FileTable;
import model.adt.dictfile.MapFileTable;
import model.adt.heap.IHeap;
import model.adt.heap.MyHeap;
import model.adt.list.IList;
import model.adt.list.ListOut;
import model.adt.map.IMap;
import model.adt.map.MapSymbolTable;
import model.adt.stack.ExecutionStack;
import model.adt.stack.IStack;
import model.expression.*;
import model.statement.*;
import model.state.ProgramState;
import model.type.Boolean;
import model.type.Integer;
import model.type.RefType;
import model.type.StringType;
import model.value.BooleanValue;
import model.value.IntegerValue;
import model.value.StringValue;
import repository.IRepository;
import repository.SingleProgramRepository;
import model.value.Value;
import model.adt.lock.ILockTable;
import model.adt.lock.MyLockTable;

import java.util.ArrayList;
import java.util.List;

public class ProgramChooserController {
    private ProgramExecutorController programExecutorController;

    public void setProgramExecutorController(ProgramExecutorController programExecutorController) {
        this.programExecutorController = programExecutorController;
    }

    @FXML
    private ListView<Statement> programsListView;

    @FXML
    private Button displayButton;

    @FXML
    public void initialize() {
        programsListView.setItems(getAllStatements());
        programsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    private void displayProgram(ActionEvent actionEvent) {
        Statement selected = programsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error encountered!");
            alert.setContentText("No statement selected!");
            alert.showAndWait();
            return;
        }

        try {
            selected.typecheck(new MapSymbolTable<>());

            IStack stack = new ExecutionStack();
            IMap<String, Value> symTable = new MapSymbolTable<>();
            IList<Value> out = new ListOut<>();
            FileTable fileTable = new MapFileTable();
            IHeap<Value> heap = new MyHeap<>();

            ILockTable lockTable = new MyLockTable();

            ProgramState programState = new ProgramState(stack, symTable, out, fileTable, selected, heap, lockTable, 0);

            IRepository repo = new SingleProgramRepository();
            List<ProgramState> list = new ArrayList<>();
            list.add(programState);
            repo.setPrgList(list);

            Controller controller = new Controller(repo);

            if (programExecutorController != null) {
                programExecutorController.setController(controller);
            }
        } catch (MyException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error encountered!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }





    @FXML
    private ObservableList<Statement> getAllStatements() {
        List<Statement> all = new ArrayList<>();

        // Example 1: int v; v = 2; Print(v)
        Statement ex1 = new CompoundStatement(
                new VariableDeclarationStatement(new Integer(), "v"),
                new CompoundStatement(
                        new AssignmentStatement(new ValueExpression(new IntegerValue(2)), "v"),
                        new PrintStatement(new VariableExpression("v"))
                )
        );
        all.add(ex1);

        // Example 2: int a; a = 2 + 3 * 5; int b; b = a + 1; Print(b)
        Statement ex2 = new CompoundStatement(
                new VariableDeclarationStatement(new Integer(), "a"),
                new CompoundStatement(
                        new AssignmentStatement(
                                new ArithmeticExpression(1,
                                        new ValueExpression(new IntegerValue(2)),
                                        new ArithmeticExpression(3,
                                                new ValueExpression(new IntegerValue(3)),
                                                new ValueExpression(new IntegerValue(5)))
                                ), "a"),
                        new CompoundStatement(
                                new VariableDeclarationStatement(new Integer(), "b"),
                                new CompoundStatement(
                                        new AssignmentStatement(new ArithmeticExpression(1,
                                                new VariableExpression("a"),
                                                new ValueExpression(new IntegerValue(1))), "b"),
                                        new PrintStatement(new VariableExpression("b"))
                                )
                        )
                )
        );
        all.add(ex2);

        // Example 3: bool a; a = true; int v; If a Then v=2 Else v=3; Print(v)
        Statement ex3 = new CompoundStatement(
                new VariableDeclarationStatement(new Boolean(), "a"),
                new CompoundStatement(
                        new AssignmentStatement(new ValueExpression(new BooleanValue(true)), "a"),
                        new CompoundStatement(
                                new VariableDeclarationStatement(new Integer(), "v"),
                                new CompoundStatement(
                                        new IfStatement(new VariableExpression("a"),
                                                new AssignmentStatement(new ValueExpression(new IntegerValue(2)), "v"),
                                                new AssignmentStatement(new ValueExpression(new IntegerValue(3)), "v")),
                                        new PrintStatement(new VariableExpression("v"))
                                )
                        )
                )
        );
        all.add(ex3);

        // Example 4: Ref int v; new(v,20); Ref Ref int a; new(a,v); print(v); print(a)
        Statement ex4 = new CompoundStatement(
                new VariableDeclarationStatement(new RefType(new Integer()), "v"),
                new CompoundStatement(
                        new newStatement("v", new ValueExpression(new IntegerValue(20))),
                        new CompoundStatement(
                                new VariableDeclarationStatement(new RefType(new RefType(new Integer())), "a"),
                                new CompoundStatement(
                                        new newStatement("a", new VariableExpression("v")),
                                        new CompoundStatement(
                                                new PrintStatement(new VariableExpression("v")),
                                                new PrintStatement(new VariableExpression("a"))
                                        )
                                )
                        )
                )
        );
        all.add(ex4);

        // Example 5: Ref int v; new(v,20); Ref Ref int a; new(a,v); new(v,30); print(rH(rH(a)))
        Statement ex5 = new CompoundStatement(
                new VariableDeclarationStatement(new RefType(new Integer()), "v"),
                new CompoundStatement(
                        new newStatement("v", new ValueExpression(new IntegerValue(20))),
                        new CompoundStatement(
                                new VariableDeclarationStatement(new RefType(new RefType(new Integer())), "a"),
                                new CompoundStatement(
                                        new newStatement("a", new VariableExpression("v")),
                                        new CompoundStatement(
                                                new newStatement("v", new ValueExpression(new IntegerValue(30))),
                                                new PrintStatement(new rhExpression(new rhExpression(new VariableExpression("a"))))
                                        )
                                )
                        )
                )
        );
        all.add(ex5);

        // Example 6: Ref int v; new(v,20); print(rH(v)); wH(v,30); print(rH(v)+5)
        Statement ex6 = new CompoundStatement(
                new VariableDeclarationStatement(new RefType(new Integer()), "v"),
                new CompoundStatement(
                        new newStatement("v", new ValueExpression(new IntegerValue(20))),
                        new CompoundStatement(
                                new PrintStatement(new rhExpression(new VariableExpression("v"))),
                                new CompoundStatement(
                                        new whStatement("v", new ValueExpression(new IntegerValue(30))),
                                        new PrintStatement(new ArithmeticExpression(1,
                                                new rhExpression(new VariableExpression("v")),
                                                new ValueExpression(new IntegerValue(5))
                                        ))
                                )
                        )
                )
        );
        all.add(ex6);

        // Example 7: int v; v=4; while (v>0) { print(v); v = v-1 } print(v)
        Statement ex7 = new CompoundStatement(
                new VariableDeclarationStatement(new Integer(), "v"),
                new CompoundStatement(
                        new AssignmentStatement(new ValueExpression(new IntegerValue(4)), "v"),
                        new CompoundStatement(
                                new WhileStatement(
                                        new model.expression.RelationalExpression(">", new VariableExpression("v"), new ValueExpression(new IntegerValue(0))),
                                        new CompoundStatement(
                                                new PrintStatement(new VariableExpression("v")),
                                                new AssignmentStatement(new ArithmeticExpression(2, new VariableExpression("v"), new ValueExpression(new IntegerValue(1))), "v")
                                        )
                                ),
                                new PrintStatement(new VariableExpression("v"))
                        )
                )
        );
        all.add(ex7);

        // Example 8: int v; Ref int a; v=10; new(a,22); fork( wH(a,30); v=32; print(v); print(rH(a)) ); print(v); print(rH(a))
        Statement ex8 = new CompoundStatement(
                new VariableDeclarationStatement(new Integer(), "v"),
                new CompoundStatement(
                        new VariableDeclarationStatement(new RefType(new Integer()), "a"),
                        new CompoundStatement(
                                new AssignmentStatement(new ValueExpression(new IntegerValue(10)), "v"),
                                new CompoundStatement(
                                        new newStatement("a", new ValueExpression(new IntegerValue(22))),
                                        new CompoundStatement(
                                                new ForkStatement(
                                                        new CompoundStatement(
                                                                new whStatement("a", new ValueExpression(new IntegerValue(30))),
                                                                new CompoundStatement(
                                                                        new AssignmentStatement(new ValueExpression(new IntegerValue(32)), "v"),
                                                                        new CompoundStatement(
                                                                                new PrintStatement(new VariableExpression("v")),
                                                                                new PrintStatement(new rhExpression(new VariableExpression("a")))
                                                                        )
                                                                )
                                                        )
                                                ),
                                                new CompoundStatement(
                                                        new PrintStatement(new VariableExpression("v")),
                                                        new PrintStatement(new rhExpression(new VariableExpression("a")))
                                                )
                                        )
                                )
                        )
                )
        );
        all.add(ex8);

        // Example 9: string varf; varf=".../test.in"; openRFile(varf); int varc; readFile(varf,varc); print(varc); readFile(varf,varc); print(varc); closeRFile(varf)
        Statement ex9 = new CompoundStatement(
                new VariableDeclarationStatement(new StringType(), "varf"),
                new CompoundStatement(
                        new AssignmentStatement(new ValueExpression(new StringValue("test.in")), "varf"),
                        new CompoundStatement(
                                new OpenRFileStatement(new VariableExpression("varf")),
                                new CompoundStatement(
                                        new VariableDeclarationStatement(new Integer(), "varc"),
                                        new CompoundStatement(
                                                new ReadFileStatement(new VariableExpression("varf"), "varc"),
                                                new CompoundStatement(
                                                        new PrintStatement(new VariableExpression("varc")),
                                                        new CompoundStatement(
                                                                new ReadFileStatement(new VariableExpression("varf"), "varc"),
                                                                new CompoundStatement(
                                                                        new PrintStatement(new VariableExpression("varc")),
                                                                        new CloseRFileStatement(new VariableExpression("varf"))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        all.add(ex9);

        // Example 10: int a; int b; a=5; b=7; if a>b then print(a) else print(b)
        Statement ex10 = new CompoundStatement(
                new VariableDeclarationStatement(new Integer(), "a"),
                new CompoundStatement(
                        new VariableDeclarationStatement(new Integer(), "b"),
                        new CompoundStatement(
                                new AssignmentStatement(new ValueExpression(new IntegerValue(5)), "a"),
                                new CompoundStatement(
                                        new AssignmentStatement(new ValueExpression(new IntegerValue(7)), "b"),
                                        new IfStatement(new model.expression.RelationalExpression(">", new VariableExpression("a"), new VariableExpression("b")),
                                                new PrintStatement(new VariableExpression("a")),
                                                new PrintStatement(new VariableExpression("b"))
                                        )
                                )
                        )
                )
        );
        all.add(ex10);

        // Example 11: fork example from Interpreter (complex)
        Statement ex11 = new CompoundStatement(
                new VariableDeclarationStatement(new Integer(), "v"),
                new CompoundStatement(
                        new VariableDeclarationStatement(new RefType(new Integer()), "a"),
                        new CompoundStatement(
                                new AssignmentStatement(new ValueExpression(new IntegerValue(10)), "v"),
                                new CompoundStatement(
                                        new newStatement("a", new ValueExpression(new IntegerValue(22))),
                                        new CompoundStatement(
                                                new ForkStatement(
                                                        new CompoundStatement(
                                                                new whStatement("a", new ValueExpression(new IntegerValue(30))),
                                                                new CompoundStatement(
                                                                        new AssignmentStatement(new ValueExpression(new IntegerValue(32)), "v"),
                                                                        new CompoundStatement(
                                                                                new PrintStatement(new VariableExpression("v")),
                                                                                new PrintStatement(new rhExpression(new VariableExpression("a")))
                                                                        )
                                                                )
                                                        )
                                                ),
                                                new CompoundStatement(
                                                        new PrintStatement(new VariableExpression("v")),
                                                        new PrintStatement(new rhExpression(new VariableExpression("a")))
                                                )
                                        )
                                )
                        )
                )
        );
        all.add(ex11);

// Example: Lock mechanism test
        Statement lockExample = new CompoundStatement(
                new VariableDeclarationStatement(new RefType(new Integer()), "v1"),
                new CompoundStatement(
                        new VariableDeclarationStatement(new RefType(new Integer()), "v2"),
                        new CompoundStatement(
                                new VariableDeclarationStatement(new Integer(), "x"),
                                new CompoundStatement(
                                        new VariableDeclarationStatement(new Integer(), "q"),
                                        new CompoundStatement(
                                                new newStatement("v1", new ValueExpression(new IntegerValue(20))),
                                                new CompoundStatement(
                                                        new newStatement("v2", new ValueExpression(new IntegerValue(30))),
                                                        new CompoundStatement(
                                                                new NewLockStatement("x"),
                                                                new CompoundStatement(
                                                                        new ForkStatement(
                                                                                new CompoundStatement(
                                                                                        new ForkStatement(
                                                                                                new CompoundStatement(
                                                                                                        new LockStatement("x"),
                                                                                                        new CompoundStatement(
                                                                                                                new whStatement("v1", new ArithmeticExpression(2, new rhExpression(new VariableExpression("v1")), new ValueExpression(new IntegerValue(1)))),
                                                                                                                new UnlockStatement("x")
                                                                                                        ))
                                                                                        ),
                                                                                        new CompoundStatement(
                                                                                                new LockStatement("x"),
                                                                                                new CompoundStatement(
                                                                                                        new whStatement("v1", new ArithmeticExpression(3, new rhExpression(new VariableExpression("v1")), new ValueExpression(new IntegerValue(10)))),
                                                                                                        new UnlockStatement("x")
                                                                                                )
                                                                                        )
                                                                                )
                                                                        ),
                                                                        new CompoundStatement(
                                                                                new NewLockStatement("q"),
                                                                                new CompoundStatement(
                                                                                        new ForkStatement(
                                                                                                new CompoundStatement(
                                                                                                        new ForkStatement(
                                                                                                                new CompoundStatement(
                                                                                                                        new LockStatement("q"),
                                                                                                                        new CompoundStatement(
                                                                                                                                new whStatement("v2", new ArithmeticExpression(1, new rhExpression(new VariableExpression("v2")), new ValueExpression(new IntegerValue(5)))),
                                                                                                                                new UnlockStatement("q")
                                                                                                                        )
                                                                                                                )
                                                                                                        ),
                                                                                                        new CompoundStatement(
                                                                                                                new LockStatement("q"),
                                                                                                                new CompoundStatement(
                                                                                                                        new whStatement("v2", new ArithmeticExpression(3, new rhExpression(new VariableExpression("v2")), new ValueExpression(new IntegerValue(10)))),
                                                                                                                        new UnlockStatement("q")
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        ),
                                                                                        new CompoundStatement(
                                                                                                new NopStatement(),
                                                                                                new CompoundStatement(
                                                                                                        new NopStatement(),
                                                                                                        new CompoundStatement(
                                                                                                                new NopStatement(),
                                                                                                                new CompoundStatement(
                                                                                                                        new NopStatement(),
                                                                                                                        new CompoundStatement(
                                                                                                                                new LockStatement("x"),
                                                                                                                                new CompoundStatement(
                                                                                                                                        new PrintStatement(new rhExpression(new VariableExpression("v1"))),
                                                                                                                                        new CompoundStatement(
                                                                                                                                                new UnlockStatement("x"),
                                                                                                                                                new CompoundStatement(
                                                                                                                                                        new LockStatement("q"),
                                                                                                                                                        new CompoundStatement(
                                                                                                                                                                new PrintStatement(new rhExpression(new VariableExpression("v2"))),
                                                                                                                                                                new UnlockStatement("q")
                                                                                                                                                        )
                                                                                                                                                )
                                                                                                                                        )
                                                                                                                                )
                                                                                                                        )
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        ))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        all.add(lockExample);

        return FXCollections.observableArrayList(all);
    }
}
