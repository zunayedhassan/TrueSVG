package zunayedhassan.TrueSVG;

import javafx.scene.layout.Pane;

/**
 *
 * @author ZUNAYED_PC
 */
public class TextLayout extends Pane {
    public TextLayout(double x, double y) {
        this.SetPosition(x, y);
    }
    
    public void SetPosition(double x, double y) {
        this.setTranslateX(x);
        this.setTranslateY(y);
    }
}
