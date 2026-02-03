package repository;
import exceptions.MyException;
import model.state.ProgramState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SingleProgramRepository implements IRepository {
    private final List<ProgramState> programs;
    private int stepCounter = 0;

    public SingleProgramRepository() {
        this.programs = new ArrayList<>();
    }

    public void addProgram(ProgramState ps) {
        programs.add(ps);
    }

    //
    @Override
    public List<ProgramState> getPrgList() {
        return programs;
    }

    //
    @Override
    public void setPrgList(List<ProgramState> prgList) {
        programs.clear();
        if (prgList != null) {
            programs.addAll(prgList);
        }
    }

    @Override
    public void logProgramState(ProgramState programState) throws MyException {
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(
                new FileWriter("logExecRepo.txt", true)))) {

            printWriter.println("ProgramState id: " + programState.getId());
            printWriter.println("Step " + stepCounter++ + ":");
            printWriter.println(programState.toString());
            printWriter.println("---");

        } catch (IOException e) {
            throw new MyException("Error writing to log file: " + e.getMessage());
        }
    }

    @Override
    public void clearLogFile() throws MyException {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter("logFile.txt", false))) {
            stepCounter = 0;
            printWriter.print("");
        } catch (IOException e) {
            throw new MyException("Error clearing log file: " + e.getMessage());
        }
    }

}