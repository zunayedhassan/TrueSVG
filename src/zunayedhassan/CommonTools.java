package zunayedhassan;

import java.io.InputStream;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Zunayed Hassan
 */
public class CommonTools extends Control {
    public static Stage PRIMARY_STAGE = null;

    public void SetStyleSheet(Scene scene, String styleClass) {
        scene.getStylesheets().add(this.getClass().getResource(styleClass).toExternalForm());
    }

    public Font GetFontFromFile(String fontName, int size) {
        InputStream fontInputStream = this.getClass().getResourceAsStream(fontName);
        Font font = Font.loadFont(fontInputStream, size);

        return font;
    }

    public void LoadIcon(Stage stage, String icon) {
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream(icon)));
    }

    public static Scene GET_SCENE_AND_INITIALIZE(Stage stage) {
        PRIMARY_STAGE = stage;

        BaseUI root = new RootUI();
        Scene scene = null;

        if (Settings.IS_SCENE_TRANSPARENT) {
            scene = new Scene(root, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT, Color.TRANSPARENT);
        }
        else {
            scene = new Scene(root, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
        }

        CommonTools tools = new CommonTools();

        for (String[] fontInformation : Settings.FONTS) {
            tools.GetFontFromFile(fontInformation[0], Integer.parseInt(fontInformation[1].trim()));
        }

        if (Settings.APPLICATION_ICON != null) {
            tools.LoadIcon(PRIMARY_STAGE, Settings.APPLICATION_ICON);
        }

        root.InitializeStyleSheets(scene, Settings.STYLE_CLASSES);
        stage.setTitle(Settings.WINDOW_TITLE);

        return scene;
    }
}
