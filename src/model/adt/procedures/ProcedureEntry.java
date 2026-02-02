package model.adt.procedures;

import model.statement.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProcedureEntry {
    private final List<String> params;
    private final Statement body;

    public ProcedureEntry(List<String> params, Statement body) {
        this.params = new ArrayList<>(params);
        this.body = body;
    }

    public List<String> getParams() {
        return new ArrayList<>(params);
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "(" + String.join(", ", params) + ") -> " + body.toString();
    }
}
