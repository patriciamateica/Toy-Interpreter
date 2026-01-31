import exceptions.TypeCheckFailedException;
import model.adt.dictfile.FileTable;
import model.adt.dictfile.MapFileTable;
import model.adt.heap.IHeap;
import model.adt.heap.MyHeap;
import model.adt.list.ListOut;
import model.adt.list.IList;
import model.adt.lock.ILockTable;
import model.adt.lock.MyLockTable;
import model.adt.map.MapSymbolTable;
import model.adt.map.IMap;
import model.adt.stack.IStack;
import model.adt.stack.ExecutionStack;
import model.state.*;

import model.statement.CompoundStatement;
import model.statement.VariableDeclarationStatement;
import model.statement.AssignmentStatement;
import model.statement.PrintStatement;
import model.statement.IfStatement;
import model.statement.OpenRFileStatement;
import model.statement.ReadFileStatement;
import model.statement.CloseRFileStatement;

import model.expression.ValueExpression;
import model.expression.VariableExpression;
import model.expression.ArithmeticExpression;

import model.type.Integer;
import model.type.Boolean;
import model.type.StringType;
import model.value.IntegerValue;
import model.value.BooleanValue;
import model.value.StringValue;

import repository.SingleProgramRepository;
import controller.Controller;
import view.command.TextMenu;
import view.command.ExitCommand;
import view.command.RunExample;

import model.type.RefType;
import model.statement.newStatement;
import model.expression.rhExpression;
import model.statement.WhileStatement;
import model.statement.whStatement;
import model.statement.ForkStatement;
import model.adt.lock.ILockTable;
import model.adt.lock.MyLockTable;

public class Interpreter {
    public static void main(String[] args) {
        // Example 1: int v; v = 2; Print(v)
        var ex1 = new CompoundStatement(
                new VariableDeclarationStatement(new Integer(), "v"),
                new CompoundStatement(
                        new AssignmentStatement(new ValueExpression(new IntegerValue(2)), "v"),
                        new PrintStatement(new VariableExpression("v"))
                )
        );
        ProgramState prg1 = createProgramState(ex1);
        SingleProgramRepository repo1 = new SingleProgramRepository();
        repo1.addProgram(prg1);
        Controller ctr1 = new Controller(repo1);

        // Example 2: int a; a = 2 + 3 * 5; int b; b = a + 1; Print(b)
        var arith1 = new ArithmeticExpression(1,
                new ValueExpression(new IntegerValue(2)),
                new ArithmeticExpression(3,
                        new ValueExpression(new IntegerValue(3)),
                        new ValueExpression(new IntegerValue(5))
                )
        );
        var arith2 = new ArithmeticExpression(1,
                new VariableExpression("a"),
                new ValueExpression(new IntegerValue(1))
        );
        var ex2 = new CompoundStatement(
                new VariableDeclarationStatement(new Integer(), "a"),
                new CompoundStatement(
                        new AssignmentStatement(arith1, "a"),
                        new CompoundStatement(
                                new VariableDeclarationStatement(new Integer(), "b"),
                                new CompoundStatement(
                                        new AssignmentStatement(arith2, "b"),
                                        new PrintStatement(new VariableExpression("b"))
                                )
                        )
                )
        );
        ProgramState prg2 = createProgramState(ex2);
        SingleProgramRepository repo2 = new SingleProgramRepository();
        repo2.addProgram(prg2);
        Controller ctr2 = new Controller(repo2);

        // Example 3: bool a; a = true; int v; If a Then v=2 Else v=3; Print(v)
        var ex3 = new CompoundStatement(
                new VariableDeclarationStatement(new Boolean(), "a"),
                new CompoundStatement(
                        new AssignmentStatement(new ValueExpression(new BooleanValue(true)), "a"),
                        new CompoundStatement(
                                new VariableDeclarationStatement(new Integer(), "v"),
                                new CompoundStatement(
                                        new IfStatement(new VariableExpression("a"),
                                                new AssignmentStatement(new ValueExpression(new IntegerValue(2)), "v"),
                                                new AssignmentStatement(new ValueExpression(new IntegerValue(3)), "v")
                                        ),
                                        new PrintStatement(new VariableExpression("v"))
                                )
                        )
                )
        );
        ProgramState prg3 = createProgramState(ex3);
        SingleProgramRepository repo3 = new SingleProgramRepository();
        repo3.addProgram(prg3);
        Controller ctr3 = new Controller(repo3);

        // Example 4: string varf; varf = "test.in"; openRFile(varf); int varc;
        //              readFile(varf,varc); print(varc); readFile(varf,varc); print(varc); closeRFile(varf)
        var ex4 = new CompoundStatement(
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
        ProgramState prg4 = createProgramState(ex4);
        SingleProgramRepository repo4 = new SingleProgramRepository();
        repo4.addProgram(prg4);
        Controller ctr4 = new Controller(repo4);

        var ex5 = new CompoundStatement(
                new VariableDeclarationStatement(new Integer(), "a"),
                new CompoundStatement(
                        new AssignmentStatement(new ValueExpression(new IntegerValue(5)), "a"),
                        new CompoundStatement(
                                new VariableDeclarationStatement(new Integer(), "b"),
                                new CompoundStatement(
                                        new AssignmentStatement(new ValueExpression(new IntegerValue(3)), "b"),
                                        new CompoundStatement(
                                                new VariableDeclarationStatement(new Boolean(), "res"),
                                                new CompoundStatement(
                                                        new AssignmentStatement(
                                                                new model.expression.RelationalExpression(
                                                                        "<",
                                                                        new VariableExpression("a"),
                                                                        new VariableExpression("b")
                                                                ),
                                                                "res"
                                                        ),
                                                        new PrintStatement(new VariableExpression("res"))
                                                )
                                        )
                                )
                        )
                )
        );

        ProgramState prgRel = createProgramState(ex5);
        SingleProgramRepository repoRel = new SingleProgramRepository();
        repoRel.addProgram(prgRel);
        Controller ctr5 = new Controller(repoRel);

        var ex6 = new CompoundStatement(
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
        ProgramState prg6 = createProgramState(ex6);
        SingleProgramRepository repo6 = new SingleProgramRepository();
        repo6.addProgram(prg6);
        Controller ctr6 = new Controller(repo6);

        // Ref int v; new(v,20); Ref Ref int a; new(a,v); print(rH(v)); print(rH(rH(a))+5)
        var ex7 = new CompoundStatement(
                new VariableDeclarationStatement(new RefType(new Integer()), "v"),
                new CompoundStatement(
                        new newStatement("v", new ValueExpression(new IntegerValue(20))),
                        new CompoundStatement(
                                new VariableDeclarationStatement(new RefType(new RefType(new Integer())), "a"),
                                new CompoundStatement(
                                        new newStatement("a", new VariableExpression("v")),
                                        new CompoundStatement(
                                                new PrintStatement(new rhExpression(new VariableExpression("v"))),
                                                new PrintStatement(new ArithmeticExpression(1,
                                                        new rhExpression(new rhExpression(new VariableExpression("a"))),
                                                        new ValueExpression(new IntegerValue(5))
                                                ))
                                        )
                                )
                        )
                )
        );
        ProgramState prg7 = createProgramState(ex7);
        SingleProgramRepository repo7 = new SingleProgramRepository();
        repo7.addProgram(prg7);
        Controller ctr7 = new Controller(repo7);

        // Ref int v; new(v,20); print(rH(v)); wH(v,30); print(rH(v)+5)
        var ex8 = new CompoundStatement(
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
        ProgramState prg8 = createProgramState(ex8);
        SingleProgramRepository repo8 = new SingleProgramRepository();
        repo8.addProgram(prg8);
        Controller ctr8 = new Controller(repo8);

        // Ref int v; new(v,20); Ref Ref int a; new(a,v); new(v,30); print(rH(rH(a)))
        var ex9 = new CompoundStatement(
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
        ProgramState prg9 = createProgramState(ex9);
        SingleProgramRepository repo9 = new SingleProgramRepository();
        repo9.addProgram(prg9);
        Controller ctr9 = new Controller(repo9);


        // int v; v=4; (while (v>0) print(v); v=v-1); print(v)
        var ex10 = new CompoundStatement(
                new VariableDeclarationStatement(new Integer(), "v"),
                new CompoundStatement(
                        new AssignmentStatement(new ValueExpression(new IntegerValue(4)), "v"),
                        new CompoundStatement(
                                new WhileStatement(
                                        new model.expression.RelationalExpression(">", new VariableExpression("v"), new ValueExpression(new IntegerValue(0))),
                                        new CompoundStatement(
                                                new PrintStatement(new VariableExpression("v")),
                                                new AssignmentStatement(
                                                        new ArithmeticExpression(2, new VariableExpression("v"), new ValueExpression(new IntegerValue(1))),
                                                        "v"
                                                )
                                        )
                                ),
                                new PrintStatement(new VariableExpression("v"))
                        )
                )
        );
        ProgramState prg10 = createProgramState(ex10);
        SingleProgramRepository repo10 = new SingleProgramRepository();
        repo10.addProgram(prg10);
        Controller ctr10 = new Controller(repo10);

        // Example 11: fork
        var ex11 = new CompoundStatement(
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
        ProgramState prg11 = createProgramState(ex11);
        SingleProgramRepository repo11 = new SingleProgramRepository();
        repo11.addProgram(prg11);
        Controller ctr11 = new Controller(repo11);


        TextMenu menu = new TextMenu();
        menu.addCommand("0", new ExitCommand("0", "exit"));
        menu.addCommand("1", new RunExample("1", ex1.toString(), ctr1));
        menu.addCommand("2", new RunExample("2", ex2.toString(), ctr2));
        menu.addCommand("3", new RunExample("3", ex3.toString(), ctr3));
        menu.addCommand("4", new RunExample("4", ex4.toString(), ctr4));
        menu.addCommand("5", new RunExample("5", ex5.toString(), ctr5));
        menu.addCommand("6", new RunExample("6", ex6.toString(), ctr6));
        menu.addCommand("7", new RunExample("7", ex7.toString(), ctr7));
        menu.addCommand("8", new RunExample("8", ex8.toString(), ctr8));
        menu.addCommand("9", new RunExample("9", ex9.toString(), ctr9));
        menu.addCommand("10", new RunExample("10", ex10.toString(), ctr10));
        menu.addCommand("11", new RunExample("11", ex11.toString(), ctr11));

        menu.show();
    }

    private static ProgramState createProgramState(model.statement.Statement program) {
        try {
            program.typecheck(new MapSymbolTable());
        } catch (exceptions.MyException e) {
            throw new TypeCheckFailedException("Typecheck failed: " + e.getMessage());
        }

        IStack stack = new ExecutionStack();
        IMap symTable = new MapSymbolTable();
        IList out = new ListOut();
        FileTable fileTable = new MapFileTable();
        IHeap heap = new MyHeap();
        ILockTable lockTable = new MyLockTable();

        stack.push(program);
        return new ProgramState(stack, symTable, out, fileTable, heap, lockTable);
    }


}