package zunayedhassan.CommonUI.ColorChooserFX;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Zunayed Hassan
 */
public class ColorChooser extends BorderPane {
    public final ObjectProperty<Color> ColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    public ColorChooserBase ColorChooserBaseControl = new ColorChooserBase();
    
    private Slider _rSlider = new Slider(0, 255, ColorChooserBaseControl.GetCustomColor().getRed() * 255);
    private Slider _gSlider = new Slider(0, 255, ColorChooserBaseControl.GetCustomColor().getGreen() * 255);
    private Slider _bSlider = new Slider(0, 255, ColorChooserBaseControl.GetCustomColor().getBlue() * 255);
    
    private Slider _hueSlider = new Slider(0, 360, ColorChooserBaseControl.GetCustomColor().getHue() * 360);
    private Slider _satSlider = new Slider(0, 100, ColorChooserBaseControl.GetCustomColor().getSaturation() * 100);
    private Slider _briSlider = new Slider(0, 100, ColorChooserBaseControl.GetCustomColor().getBrightness() * 100);
    
    private Spinner _rSpinner = new Spinner(0, 255, ColorChooserBaseControl.GetCustomColor().getRed() * 255);
    private Spinner _gSpinner = new Spinner(0, 255, ColorChooserBaseControl.GetCustomColor().getGreen() * 255);
    private Spinner _bSpinner = new Spinner(0, 255, ColorChooserBaseControl.GetCustomColor().getBlue() * 255);
    
    private Spinner _hueSpinner = new Spinner(0, 360, ColorChooserBaseControl.GetCustomColor().getHue() * 360);
    private Spinner _satSpinner = new Spinner(0, 100, ColorChooserBaseControl.GetCustomColor().getSaturation() * 100);
    private Spinner _briSpinner = new Spinner(0, 100, ColorChooserBaseControl.GetCustomColor().getBrightness() * 100);
    
    private TextField _hexTextField = new TextField(ColorChooserBaseControl.GetCustomColor().toString().substring(2, ColorChooserBaseControl.GetCustomColor().toString().length() - 2));
    private GridPane _colorDetailsGridPane = new GridPane();
    private ToggleButton _moreColorsToggleButton = new ToggleButton("More Colors...");
    
    private final String[] StandardColors = {
        "#FFFFFF",
        "#000000",
        "#C05046",
        "#9DBB61",
        "#AB9AC0",
        "#4BACC6",
        "#F59D56",
        "#FFC000",

        "#F2F2F2",
        "#7F7F7F",
        "#F2DCDA",
        "#EBF1DF",
        "#EEEAF2",
        "#DBEEF3",
        "#FCEBDD",
        "#FFF2CC",

        "#D8D8D8",
        "#595959",
        "#E5B9B5",
        "#D7E3BF",
        "#DDD6E5",
        "#B7DDE8",
        "#FBD7BB",
        "#FEE599",

        "#BFBFBF",
        "#3F3F3F",
        "#D9958F",
        "#C4D6A0",
        "#CCC2D9",
        "#92CDDC",
        "#F9C499",
        "#FFD965",

        "#A5A5A5",
        "#262626",
        "#923931",
        "#789440",
        "#7E649E",
        "#31859B",
        "#EA700D",
        "#BF9000",

        "#7F7F7F",
        "#0C0C0C",
        "#612621",
        "#50632A",
        "#54426A",
        "#205867",
        "#9C4A09",
        "#7F6000"
    };
    
    private final static int _STANDARD_COLOR_RECT_SIZE = 13;
    
    protected VBox standardColorsVBox = null;

    public ColorChooser() {       
        this.getStyleClass().add("color-chooser-toolbar");
        
        this._colorDetailsGridPane.setHgap(8);
        this._colorDetailsGridPane.setVgap(8);
        this._colorDetailsGridPane.setPadding(new Insets(5));
        
        ArrayList<Label> labels = new ArrayList<>();
        labels.add(new Label("RGB"));
        labels.add(new Label("Red"));
        labels.add(new Label("Green"));
        labels.add(new Label("Blue"));
        labels.add(new Label("HSB"));
        labels.add(new Label("Hue"));
        labels.add(new Label("Saturation"));
        labels.add(new Label("Brightness"));
        labels.add(new Label("Hex Value: #"));
        labels.add(new Label("Standard Colors"));
        
        labels.get(0).setStyle("-fx-font-weight: bold;");
        labels.get(4).setStyle("-fx-font-weight: bold;");
        labels.get(8).setStyle("-fx-font-weight: bold;");
        labels.get(9).setStyle("-fx-font-weight: bold;");
        
        for (Label label : labels) {
            label.setTextFill(Color.BLACK);
        }
        
        this._rSpinner.setPrefWidth(80);
        this._gSpinner.setPrefWidth(80);
        this._bSpinner.setPrefWidth(80);
        this._hueSpinner.setPrefWidth(80);
        this._satSpinner.setPrefWidth(80);
        this._briSpinner.setPrefWidth(80);
        
        this._rSpinner.setEditable(true);
        this._gSpinner.setEditable(true);
        this._bSpinner.setEditable(true);
        this._hueSpinner.setEditable(true);
        this._satSpinner.setEditable(true);
        this._briSpinner.setEditable(true);
        
        this._colorDetailsGridPane.add(labels.get(0),      0, 0);
        this._colorDetailsGridPane.add(labels.get(1),      0, 1);
        this._colorDetailsGridPane.add(this._rSlider,      1, 1);
        this._colorDetailsGridPane.add(this._rSpinner,     2, 1);
        this._colorDetailsGridPane.add(labels.get(2),      0, 2);
        this._colorDetailsGridPane.add(this._gSlider,      1, 2);
        this._colorDetailsGridPane.add(this._gSpinner,     2, 2);
        this._colorDetailsGridPane.add(labels.get(3),      0, 3);
        this._colorDetailsGridPane.add(this._bSlider,      1, 3);
        this._colorDetailsGridPane.add(this._bSpinner,     2, 3);
        this._colorDetailsGridPane.add(labels.get(4),      0, 4);
        this._colorDetailsGridPane.add(labels.get(5),      0, 5);
        this._colorDetailsGridPane.add(this._hueSlider,    1, 5);
        this._colorDetailsGridPane.add(this._hueSpinner,   2, 5);
        this._colorDetailsGridPane.add(labels.get(6),      0, 6);
        this._colorDetailsGridPane.add(this._satSlider,    1, 6);
        this._colorDetailsGridPane.add(this._satSpinner,   2, 6);
        this._colorDetailsGridPane.add(labels.get(7),      0, 7);
        this._colorDetailsGridPane.add(this._briSlider,    1, 7);
        this._colorDetailsGridPane.add(this._briSpinner,   2, 7);
        this._colorDetailsGridPane.add(labels.get(8),      0, 8);
        this._colorDetailsGridPane.add(this._hexTextField, 1, 8);
        
        
        standardColorsVBox = new VBox(5);
        
        GridPane stanardColorsGridPane = new GridPane();
        stanardColorsGridPane.setHgap(1);
        stanardColorsGridPane.setVgap(1);
        stanardColorsGridPane.setPadding(new Insets(5));
        
        this._moreColorsToggleButton.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream("icons/color-management.png"))));
        super.setLeft(standardColorsVBox);
        standardColorsVBox.setPadding(new Insets(5));
        
        standardColorsVBox.getChildren().add(labels.get(9));
        standardColorsVBox.getChildren().add(stanardColorsGridPane);
        
        int j = 0;
        int k = 0;
        
        for (int i = 1; i <= this.StandardColors.length; i++) {
            Rectangle colorRect = new Rectangle(_STANDARD_COLOR_RECT_SIZE, _STANDARD_COLOR_RECT_SIZE);
            colorRect.setFill(Color.web(this.StandardColors[i - 1]));
            colorRect.setStroke(Color.LIGHTGRAY);

            stanardColorsGridPane.add(colorRect, j++, k);
            
            if ((i % 8) == 0) {
                j = 0;
                ++k;
            }
            
            colorRect.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    colorRect.setStroke(Color.BLACK);
                    setCursor(Cursor.HAND);
                }
            });
            
            colorRect.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    colorRect.setStroke(Color.LIGHTGRAY);
                    setCursor(Cursor.DEFAULT);
                }
            });
            
            colorRect.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    ColorChooserBaseControl.SetCustomColor(Color.web(colorRect.getFill().toString().substring(2, colorRect.getFill().toString().length() - 2)));
                }
            });
        }
        
        standardColorsVBox.getChildren().add(this._moreColorsToggleButton);
        this._moreColorsToggleButton.setPrefWidth((_STANDARD_COLOR_RECT_SIZE + 2) * 8 + standardColorsVBox.getPadding().getLeft());
        
        
        
        // Events
        this.ColorChooserBaseControl.CustomColorProperty.addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                ColorProperty.set(newValue);
                _updateColorRGB(newValue);
            }
        });
        
        this._rSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Color color = ColorProperty.get();
                color = color.rgb(newValue.intValue(), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
                ColorChooserBaseControl.SetCustomColor(color);
            }
        });
        
        this._gSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Color color = ColorProperty.get();
                color = color.rgb((int) (color.getRed() * 255), newValue.intValue(), (int) (color.getBlue() * 255));
                ColorChooserBaseControl.SetCustomColor(color);
            }
        });
        
        this._bSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Color color = ColorProperty.get();
                color = color.rgb((int) (color.getRed() * 255), (int) (color.getGreen() * 255), newValue.intValue());
                ColorChooserBaseControl.SetCustomColor(color);
            }
        });
        
        this._rSpinner.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Color color = ColorProperty.get();
                color = color.rgb(((Double) newValue).intValue(), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
                ColorChooserBaseControl.SetCustomColor(color);
            }
        });
        
        this._gSpinner.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Color color = ColorProperty.get();
                color = color.rgb((int) (color.getRed() * 255) , ((Double) newValue).intValue(), (int) (color.getBlue() * 255));
                ColorChooserBaseControl.SetCustomColor(color);
            }
        });
        
        this._bSpinner.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Color color = ColorProperty.get();
                color = color.rgb((int) (color.getRed() * 255) , (int) (color.getGreen() * 255), ((Double) newValue).intValue());
                ColorChooserBaseControl.SetCustomColor(color);
            }
        });
        
        this._hueSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Color color = ColorProperty.get();
                Color newColor = color.hsb(newValue.doubleValue(), color.getSaturation(), color.getBrightness());
                ColorChooserBaseControl.SetCustomColor(newColor);
            }
        });
        
        this._satSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Color color = ColorProperty.get();                
                Color newColor = color.hsb(color.getHue(), newValue.doubleValue() / 100, color.getBrightness());
                ColorChooserBaseControl.SetCustomColor(newColor);
            }
        });
        
        this._briSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Color color = ColorProperty.get();                
                Color newColor = color.hsb(color.getHue(), color.getSaturation(), newValue.doubleValue() / 100);
                ColorChooserBaseControl.SetCustomColor(newColor);
            }
        });
        
        this._hueSpinner.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Color color = ColorProperty.get();
                color = color.hsb(((Double) newValue).doubleValue(), color.getSaturation(), color.getBrightness());
                ColorChooserBaseControl.SetCustomColor(color);
            }
        });
        
        this._satSpinner.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Color color = ColorProperty.get();
                color = color.hsb(color.getHue(), ((Double) newValue).doubleValue() / 100, color.getBrightness());
                ColorChooserBaseControl.SetCustomColor(color);
            }
        });
        
        this._briSpinner.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Color color = ColorProperty.get();
                color = color.hsb(color.getHue(), color.getSaturation(), ((Double) newValue).doubleValue() / 100);
                ColorChooserBaseControl.SetCustomColor(color);
            }
        });
        
        this.ColorChooserBaseControl.CustomColorProperty.addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                _updateColorRGB(newValue);
                _updateColorHSB(newValue);
                _updateColorHex(newValue);
            }
        });
        
        this._hexTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() == 6) {
                    String pattern = "[A-F,a-f,0-9][A-F,a-f,0-9][A-F,a-f,0-9][A-F,a-f,0-9][A-F,a-f,0-9][A-F,a-f,0-9]";
                    Pattern patternObject = Pattern.compile(pattern);

                    Matcher match = patternObject.matcher(newValue);
                    
                    if (match.find()) {
                        ColorChooserBaseControl.SetCustomColor(Color.web(newValue));
                    }
                }
            }
        });
        
        
        this._moreColorsToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    setCenter(ColorChooserBaseControl);
                    setRight(_colorDetailsGridPane);
                }
                else {
                    setCenter(null);
                    setRight(null);
                }
            }
        });
    }
    
    public ColorChooser(boolean isExpanded) {
        this();
        this._moreColorsToggleButton.setSelected(true);
        this.standardColorsVBox.getChildren().remove(this._moreColorsToggleButton);
    }
    
    private void _updateColorRGB(Color color) {
        this._rSlider.setValue(color.getRed() * 255);
        this._gSlider.setValue(color.getGreen() * 255);
        this._bSlider.setValue(color.getBlue() * 255);
        
        this._rSpinner.getValueFactory().setValue(color.getRed() * 255);
        this._gSpinner.getValueFactory().setValue(color.getGreen() * 255);
        this._bSpinner.getValueFactory().setValue(color.getBlue() * 255);
    }
    
    private void _updateColorHSB(Color color) {        
        this._hueSlider.setValue(color.getHue());
        this._satSlider.setValue(color.getSaturation() * 100);
        this._briSlider.setValue(color.getBrightness() * 100);
        
        this._hueSpinner.getValueFactory().setValue(color.getHue());
        this._satSpinner.getValueFactory().setValue(color.getSaturation() * 100);
        this._briSpinner.getValueFactory().setValue(color.getBrightness() * 100);
    }
    
    private void _updateColorHex(Color color) {
        this._hexTextField.setText(color.toString().substring(2, color.toString().length() - 2));
    }
}
