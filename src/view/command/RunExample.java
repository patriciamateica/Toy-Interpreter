package view.command;

import controller.Controller;
import exceptions.MyException;

public class RunExample extends Command {
    private final Controller ctr;

    public RunExample(String key, String description, Controller ctr) {
        super(key, description);
        this.ctr = ctr;
    }

    @Override
    public void execute() {
        try {
            ctr.allStep();
        } catch (MyException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}