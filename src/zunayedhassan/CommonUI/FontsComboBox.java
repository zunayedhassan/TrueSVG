package zunayedhassan.CommonUI;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.util.Callback;

/**
 *
 * @author Zunayed Hassan
 */
public class FontsComboBox extends ComboBox<FontComboBoxElement> {
    public FontsComboBox() {
        for (String fontName : Font.getFamilies()) {
            this.getItems().add(new FontComboBoxElement(fontName));
        }
        
        this.setCellFactory(new Callback<ListView<FontComboBoxElement>, ListCell<FontComboBoxElement>>() {
            @Override
            public ListCell<FontComboBoxElement> call(ListView<FontComboBoxElement> param) {
                return new ListCell<FontComboBoxElement>() {
                    private final FontComboBoxElement fontComboBoxElement;
                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        fontComboBoxElement = new FontComboBoxElement();
                    }
                    
                    @Override
                    protected void updateItem(FontComboBoxElement item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if ((item == null) || empty) {
                            setGraphic(null);
                        }
                        else {
                            fontComboBoxElement.SetFont(item.FontFamilyName);
                            setGraphic(fontComboBoxElement);
                        }
                    }
                };
            }
        });
    }
}
