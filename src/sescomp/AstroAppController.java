package sescomp;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.WWUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AstroAppController {

  @FXML
  private ListView<String> listView = new ListView<>();

  @FXML
  private SwingNode swingNode = new SwingNode();

  private WorldWindowGLJPanel wwd = null;

  public void initialize() {
    ObservableList<String> satelliteNames =
        FXCollections.observableArrayList(TLEs.getSpacecraftNames());
    listView.setItems(satelliteNames.sorted());
    listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable,
                          String oldValue, String newValue) {
        TLE tle = TLEs.getTleFromName(newValue);

        // Propagar e plotar
        try {
          Propagator tlePropagator = TLEPropagator.selectExtrapolator(tle);
          AbsoluteDate initialDate = tlePropagator.getInitialState().getDate();
          double timeStep = 10;
          double duration = 5400; // 1.5 horas
          AbsoluteDate finalDate = initialDate.shiftedBy(duration);
          ArrayList<Position> positions = new ArrayList<>();

          for (AbsoluteDate currentDate = initialDate;
               currentDate.compareTo(finalDate) < 0;
               currentDate = currentDate.shiftedBy(timeStep)) {
            SpacecraftState state = tlePropagator.propagate(currentDate);
            Vector3D pos = state.getOrbit()
                .getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true))
                .getPosition();

            // Aproximação considerando a Terra esférica
            double latitude = pos.getDelta();
            double longitude = pos.getAlpha();
            double altitude = pos.getNorm() - Constants.WGS84_EARTH_EQUATORIAL_RADIUS;

            positions.add(Position.fromRadians(latitude, longitude, altitude));
          }

          // Atributos gráficos para a trajetória
          ShapeAttributes attrs = new BasicShapeAttributes();
          Color randomColor = WWUtil.makeRandomColor(new Color(255, 255, 255));
          attrs.setOutlineMaterial(new Material(randomColor));
          attrs.setOutlineWidth(3d);

          // Criar uma trajetória
          Path trajectory = new Path(positions);
          trajectory.setAttributes(attrs);
          trajectory.setVisible(true);
          trajectory.setAltitudeMode(WorldWind.ABSOLUTE);
          trajectory.setPathType(AVKey.GREAT_CIRCLE);

          RenderableLayer newLayer = new RenderableLayer();
          newLayer.addRenderable(trajectory);

          wwd.getModel().getLayers().add(newLayer);
          System.out.println("Added " + newValue);

        } catch (OrekitException e) {
          e.printStackTrace();
        }
      }
    });

    SwingUtilities.invokeLater(() -> {
      WorldWindowGLJPanel wwd = new WorldWindowGLJPanel();
      wwd.setModel(new BasicModel());
      swingNode.setContent(wwd);
      this.wwd = wwd;
    });
  }
}
