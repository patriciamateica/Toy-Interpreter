package model.adt.dictfile;

import exceptions.FileAlreadyOpenException;
import exceptions.FileNotOpenException;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class MapFileTable implements FileTable {
    private final Map<String, BufferedReader> fileTable;

    public MapFileTable() {
        this.fileTable = new HashMap<>();
    }

    @Override
    public boolean isOpen(String fileName) {
        return fileTable.containsKey(fileName);
    }

    @Override
    public void addOpenFile(String fileName, BufferedReader bufferReader) throws FileAlreadyOpenException {
        if (isOpen(fileName)) {
            throw new FileAlreadyOpenException("File '" + fileName + "' is already open");
        }
        fileTable.put(fileName, bufferReader);
    }

    @Override
    public BufferedReader getOpenFile(String fileName) {
        return fileTable.get(fileName);
    }

    @Override
    public void closeFile(String fileName) {
        if (!isOpen(fileName)) {
            throw new FileNotOpenException("File '" + fileName + "' is not open");
        }
        try {
            fileTable.get(fileName).close();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        fileTable.remove(fileName);
    }

    @Override
    public Map<String, BufferedReader> getContent() {
        return fileTable;
    }
}
