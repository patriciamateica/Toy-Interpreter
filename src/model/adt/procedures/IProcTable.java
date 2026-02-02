package model.adt.procedures;

import model.statement.Statement;
import java.util.List;
import java.util.Map;

public interface IProcTable {
    void add(String procName, List<String> params, Statement body);
    boolean isDefined(String procName);
    List<String> getParams(String procName);
    Statement getBody(String procName);
    Map<String, ProcedureEntry> getContent();
}
