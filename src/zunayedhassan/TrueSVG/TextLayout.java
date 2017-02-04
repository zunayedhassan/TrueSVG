package zunayedhassan.TrueSVG;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 *
 * @author ZUNAYED_PC
 */
public class TextLayout extends HBox {
    public TextLayout(double x, double y) {
        this.SetPosition(x, y);
        this.setAlignment(Pos.BASELINE_LEFT);
    }
    
    public void SetPosition(double x, double y) {
        this.setTranslateX(x);
        this.setTranslateY(y);
    }
}
