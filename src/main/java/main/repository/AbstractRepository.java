package main.repository;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

abstract class AbstractRepository<T> {

    private Gson gson = new Gson();
    private BufferedReader reader;
    private BufferedWriter writer;

    private LinkedList<T> items;

    AbstractRepository() throws Exception {
        String path = Paths.get(getJarDirectory(), "database", this.getClass().getSimpleName()).toString();
        File file = new File(path);
        if(!file.exists())
            createFile(file);
        FileWriter fileWriter = new FileWriter(path, true);
        FileReader fileReader = new FileReader(path);
        this.reader = new BufferedReader(fileReader);
        this.writer = new BufferedWriter(fileWriter);
        init();
    }

    private void createFile(File file) throws Exception {
        if(!file.getParentFile().exists() && !file.getParentFile().mkdir())
            throw new Exception("Cannot create directory: "+file.getParentFile());
        if(!file.createNewFile())
            throw new Exception("Cannot create file: "+file.getPath());
    }

    public List<T> getAllItems(){
        return items;
    }

    public boolean save(T data) {
        try {
            if (items.size() != 0)
                this.writer.append("\n");
            this.writer.append(gson.toJson(data));
            this.writer.flush();
            this.items.add(data);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void init() throws IOException {
        this.items = new LinkedList<>();
        String line;
        while((line = this.reader.readLine()) != null)
            this.items.add(this.gson.fromJson(line,((ParameterizedType)getClass()
                    .getGenericSuperclass())
                    .getActualTypeArguments()[0]));
    }

    private String getJarDirectory(){
        return System.getProperty("user.dir");
    }
}
