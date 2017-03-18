package zunayedhassan.CommonUI;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author Zunayed Hassan
 */
public class ToolbarHorizontalGap extends HBox {
    public ToolbarHorizontalGap() {
        HBox.setHgrow(this, Priority.ALWAYS);
    }
}
