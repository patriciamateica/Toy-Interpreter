package view.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TextMenu {
    private Map<String, Command> commands;
    public TextMenu(){commands = new HashMap<>();}
    public void addCommand(String command, Command cmd) {
        commands.put(cmd.getKey(), cmd);
    }
    private void printMenu(){
        for(Command com: commands.values()){
            String line = String.format("%4s: %s", com.getKey(), com.getDescription());
            System.out.println(line);
        }
    }
    public void show(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            printMenu();
            System.out.printf("Select the program you want to run:");
            String key = scanner.nextLine();
            Command com = commands.get(key);
            if(com == null){
                System.out.println("Invalid choice");
                continue;
            }
        com.execute();
        }
    }
}
