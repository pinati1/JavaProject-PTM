package test;


import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GenericConfig implements Config {

    private String confFileName;
    private List<Agent> agents = new ArrayList<>();

    public void setConfFile(String fileName) {
        this.confFileName = fileName;
    }

    @Override
    public void create() {
        try (Scanner scanner = new Scanner(new File(confFileName))) {
            while (scanner.hasNextLine()) {
                String className = scanner.nextLine();
                String[] subs = scanner.nextLine().split(",");
                String[] pubs = scanner.nextLine().split(",");

                for (int i = 0; i < subs.length; i++) subs[i] = subs[i].trim();
                for (int i = 0; i < pubs.length; i++) pubs[i] = pubs[i].trim();

                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getConstructor(String[].class, String[].class);
                Agent realAgent = (Agent) constructor.newInstance((Object) subs, (Object) pubs);

                ParallelAgent wrappedAgent = new ParallelAgent(realAgent, 10);
                agents.add(wrappedAgent);
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    @Override
    public String getName(){
        return "GenericConfig";
    }
    @Override
    public int getVersion(){
        return 1;
    }

    @Override
    public void close() {
        for (Agent a : agents) {
            a.close();
        }
    }

}
