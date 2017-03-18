package zunayedhassan.CommonUI;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author Zunayed Hassan
 */
public class FontComboBoxElement extends BorderPane {
    public static final double DEFAULT_FONT_SIZE = 12;
    
    public String FontFamilyName = "";
    
    private Label _titleText = new Label();
    private Label _previewText = new Label("abcdeABCDE");
    private Pane _gap = new Pane();
    
    public FontComboBoxElement() {
        this("System");
    }
    
    public FontComboBoxElement(String fontFamilyName) {
        this.SetFont(fontFamilyName);
        
        this._titleText.setFont(Font.font(DEFAULT_FONT_SIZE));
        this._gap.setMinWidth(16);
        
        this.setLeft(this._titleText);
        this.setCenter(this._gap);
        this.setRight(this._previewText);
        
        this._titleText.setMinWidth(100);
        this._titleText.setPrefWidth(this._titleText.getMinWidth());
        this._previewText.setMinWidth(this._titleText.getMinWidth());
        this._previewText.setPrefWidth(this._titleText.getMinWidth());
        
        this._titleText.setTextFill(Color.BLACK);
        this._previewText.setTextFill(Color.BLACK);
    }
    
    public void SetFont(String fontFamily) {
        this.FontFamilyName = fontFamily;
        
        this._titleText.setText(this.FontFamilyName);
        this._previewText.setFont(Font.font(this.FontFamilyName, DEFAULT_FONT_SIZE));
    }
}
