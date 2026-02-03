package repository;
import exceptions.MyException;
import model.adt.list.IList;
import model.state.ProgramState;
import java.util.List;

public interface IRepository {
    void logProgramState(ProgramState programState) throws MyException;
    void clearLogFile() throws MyException;
    List<ProgramState> getPrgList();
    void setPrgList(List<ProgramState> prgList);
}