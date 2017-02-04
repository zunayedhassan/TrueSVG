package zunayedhassan.CommonUI;

import javafx.geometry.Insets;

/**
 *
 * @author Zunayed Hassan
 */
public class RoundedIconButton extends IconButton {
    public RoundedIconButton(String icon, String tooltipText) {
        super(icon, tooltipText);
        
        this._initializeLayout();
    }
    
    // Layout
    private void _initializeLayout() {
        double cornerRadius = Math.max(super.GetContent().getImage().getWidth(), super.GetContent().getImage().getHeight());
        this.setStyle("-fx-background-radius: " + Double.toString(cornerRadius) + "px;");
        this.setPadding(new Insets(cornerRadius / 3.0));
    }
}
