package potioncontrol.config.classdump;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class PotionClassReader {
    public static Set<String> classesEarly = null;

    public static final String DUMP_PATH = "config/potioncontrol/data/earlyclasses.dat";

    public static Set<String> getEarlyClasses(){
        if(classesEarly == null){
            classesEarly = new HashSet<>();

            Path enchclasses_path = Paths.get(DUMP_PATH);
            try {
                Files.createDirectories(enchclasses_path.getParent());

                for(String line : Files.readAllLines(enchclasses_path)){
                    if(line.startsWith("!!")) continue;
                    classesEarly.add(line);
                }
            }
            catch(IOException exception) {
                System.out.println("PotionControl failed to read earlyclasses.dat");
            }
        }

        return classesEarly;
    }
}
