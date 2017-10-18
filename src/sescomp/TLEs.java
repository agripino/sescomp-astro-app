package sescomp;

import org.orekit.errors.OrekitException;
import org.orekit.propagation.BoundedPropagator;
import org.orekit.propagation.analytical.Ephemeris;
import org.orekit.propagation.analytical.tle.TLE;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TLEs {
  private static HashMap<String, TLE> tleData;

  static {
    tleData = new HashMap<>();
  }

  private static void loadTleDataFromFile(String filename) {
    try {
      List<String> lines = Files.readAllLines(Paths.get(filename));

      Iterator<String> iterator = lines.iterator();
      while (iterator.hasNext()) {
        String name = iterator.next().trim();  // there is whitespace after each name
        String line1 = iterator.next();
        String line2 = iterator.next();

        if (TLE.isFormatOK(line1, line2)) {
          TLE tle = new TLE(line1, line2);
          tleData.put(name, tle);
        } else {
          // or just ignore
          //continue;
          throw new IOException("Invalid line format in TLE file");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (OrekitException oe) {
      System.out.printf("Error reading TLE file");
      oe.printStackTrace();
    }
  }

  static void loadTleDataFromDirectory(String dirname) {
    try {
      Files.list(Paths.get(dirname)).forEach((path) -> {
        String filename = path.toString();
        TLEs.loadTleDataFromFile(filename);
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static Set<String> getSpacecraftNames() {
    return tleData.keySet();
  }

  public static void clearData() {
    tleData.clear();
  }

  public static TLE getTleFromName(String name) {
    return tleData.get(name);
  }
}
