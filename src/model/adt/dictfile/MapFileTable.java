package model.adt.dictfile;
import exceptions.FileAlreadyOpenException;
import exceptions.FileNotOpenException;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class MapFileTable implements FileTable{
    private Map<String, BufferedReader> fileTable = new HashMap<>();

    @Override
    public boolean isOpen(String fileName) {

        return fileTable.containsKey(fileName);
    }

    @Override
    public void addOpenFile(String fileName, BufferedReader bufferReader) throws FileAlreadyOpenException {
        try{
            fileTable.put(fileName, bufferReader);
        }
        catch (Exception e){
            throw new FileAlreadyOpenException("File already open");
        }

    }

    @Override
    public BufferedReader getOpenFile(String fileName) {

        return fileTable.get(fileName);
    }

    @Override
    public void closeFile(String fileName) {
        try {
            fileTable.remove(fileName).close();
        } catch (Exception e) {
            throw new FileNotOpenException("File not open");
        }
    }

    @Override
    public String toString() {
        return fileTable.toString();
    }
}
