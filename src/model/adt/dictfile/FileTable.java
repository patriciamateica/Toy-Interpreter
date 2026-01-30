package model.adt.dictfile;
import exceptions.FileAlreadyOpenException;

import java.io.BufferedReader;

public interface FileTable {
    boolean isOpen(String fileName);

    void addOpenFile(String fileName, BufferedReader bufferReader) throws FileAlreadyOpenException;

    BufferedReader getOpenFile(String fileName);
    void closeFile(String fileName);

}
