package model.adt.procedures;

import model.statement.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyProcTable implements IProcTable {
    private final Map<String, ProcedureEntry> procTable;

    public MyProcTable() {
        this.procTable = new HashMap<>();
    }

    @Override
    public void add(String procName, List<String> params, Statement body) {
        if (procTable.containsKey(procName)) {
            throw new RuntimeException("Procedure " + procName + " already defined");
        }
        procTable.put(procName, new ProcedureEntry(params, body));
    }

    @Override
    public boolean isDefined(String procName) {
        return procTable.containsKey(procName);
    }

    @Override
    public List<String> getParams(String procName) {
        if (!procTable.containsKey(procName)) {
            throw new RuntimeException("Procedure " + procName + " not defined");
        }
        return procTable.get(procName).getParams();
    }

    @Override
    public Statement getBody(String procName) {
        if (!procTable.containsKey(procName)) {
            throw new RuntimeException("Procedure " + procName + " not defined");
        }
        return procTable.get(procName).getBody();
    }

    @Override
    public Map<String, ProcedureEntry> getContent() {
        return new HashMap<>(procTable);
    }

    @Override
    public String toString() {
        return procTable.toString();
    }
}
