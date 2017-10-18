package sescomp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;

import java.io.File;

public class Main extends Application {

  private static final String DATA_DIR = ".";
  private static final String TLE_DIR = "tles";

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("AstroApp.fxml"));
    primaryStage.setTitle("AstroApp");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
  }

  private static void loadPhysicalData(String dirName) {
    File dataDir = new File(dirName);

    try {
      DirectoryCrawler crawler = new DirectoryCrawler(dataDir);
      DataProvidersManager providersManager = DataProvidersManager.getInstance();
      providersManager.addProvider(crawler);
    } catch (OrekitException oe) {
      oe.printStackTrace();
    }
  }

  public static void main(String[] args) {
    loadPhysicalData(DATA_DIR);
    TLEs.loadTleDataFromDirectory(TLE_DIR);

    launch(args);
  }
}
