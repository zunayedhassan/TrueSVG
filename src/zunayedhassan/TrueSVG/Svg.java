package zunayedhassan.TrueSVG;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javax.xml.parsers.ParserConfigurationException;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Shear;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

/**
 *
 * @author Zunayed Hassan
 */
public class Svg extends Pane {
    private static enum _UNIT {
        PIXEL,
        POINT
    }

    private String                  _title           = "";
    private double                  _width           = 0;
    private double                  _height          = 0;
    private Document                _xmlDocument     = null;
    private ArrayList<Filter>       _filters         = new ArrayList<>();
    private ArrayList<Fill>         _fills           = new ArrayList<>();
    private String                  _filePath        = null;
    private ArrayList<StyleHistory> _styleHistory    = new ArrayList<>();
    private String                  _styleTagContent = null;
    
    public Svg(String filePath) {
        this._filePath    = filePath;
        this._xmlDocument = this._getXmlDocument(this._filePath);
        
        if (this._xmlDocument != null) {
            String rootName = this._xmlDocument.getDocumentElement().getNodeName();
            
            // If SVG file
            if (rootName.toLowerCase().equals("svg")) {
                org.w3c.dom.Node svgNode = (org.w3c.dom.Node) (this._xmlDocument.getElementsByTagName(rootName).item(0));
                String viewBox           = ((Element) svgNode).getAttribute("viewBox");
                
                double width             = 0;
                double height            = 0;
                    
                // Example: <svg viewBox="0 0 500 500" xmlns="http://www.w3.org/2000/svg"></svg>
                if (!viewBox.trim().equals("")) {
                    String[] dimensionAsText = viewBox.split(" ");

                    width  = _parseDouble(dimensionAsText[2].trim());
                    height = _parseDouble(dimensionAsText[3].trim());
                }
                else {
                    width  = _parseDouble(((Element) svgNode).getAttribute("width").trim());
                    height = _parseDouble(((Element) svgNode).getAttribute("height").trim());
                }
                
                this._setDimension(width, height);

                ArrayList<Element> svgElements = this._getListOfElementsFromNodeList(this._xmlDocument, this._xmlDocument.getElementsByTagName(rootName).item(0).getChildNodes());
                this._readSvgElement(svgElements, this);
            }
            // If not SVG file, then show the error message
            else {
                this._print("[!] ERROR: Are you sure that, this is an actual SVG file? By the way, te file you gave is " + filePath);
            }
        }
        
        // Read CSS Styles
        if (this._styleTagContent != null) {
            String[] cssStyles = this._styleTagContent.split("}");
            
            for (String cssStyle : cssStyles) {
                String[] cssStyleParts = cssStyle.split("\\{");
                
                String tag       = cssStyleParts[0].trim();
                String styleBody = cssStyleParts[1].trim();
                
                if (tag.contains(".")) {
                    tag = tag.substring(1, tag.length());
                }
                
                // Look through style history
                for (StyleHistory history : this._styleHistory) {
                    if (history.StyleClass.equals(tag)) {
                        // Find all the Shape ID related to this style class
                        for (Shape shape : history.ShapesList) {
                            for (javafx.scene.Node node : history.ShapesList) {
                                if (node.equals(shape)) {
                                    this._readSvgObjectStyle(styleBody, shape, null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private Document _getXmlDocument(String filePath) {
        try {
            File xmlFile                                  = new File(filePath);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder               = documentBuilderFactory.newDocumentBuilder();
            Document document                             = documentBuilder.parse(xmlFile);
            
            document.getDocumentElement().normalize();
            
            return document;
        }
        catch (ParserConfigurationException exception) {
            exception.printStackTrace();
        }
        catch (SAXException exception) {
            exception.printStackTrace();
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    private void _print(String text) {
        System.out.println(text);
    }
    
    private void _setDimension(double width, double height) {
        this.setPrefSize(width, height);
        
        this._width  = width;
        this._height = height;
    }

    private ArrayList<Element> _getListOfElementsFromNodeList(Document xmlDocument, NodeList nodeList) {
        ArrayList<Element> listOfElements = new ArrayList<>();

        if (nodeList != null) {
            for (int index = 0; index < nodeList.getLength(); index++) {
                org.w3c.dom.Node node = (org.w3c.dom.Node) nodeList.item(index);
                
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    listOfElements.add((Element) node);
                }
            }
        }
        
        return listOfElements;
    }
    
    private void _readSvgElement(ArrayList<Element> arrayOfElements, Pane group) {
        for (Element element : arrayOfElements) {
            // Read your SVG tag here
            String svgTag = element.getTagName().trim();
            String id     = this._getAttributeValueAsString(element, "id");
            String style  = this._getAttributeValueAsString(element, "style");
            Shape  shape  = null;
            
            // Style
            /*
                
            <style type="text/css">
                .st0{fill:#5759A7;stroke:#FBA925;stroke-miterlimit:10;}
            </style>    
            
             */
            if (svgTag.equals("style")) {
                this._styleTagContent = element.getTextContent().trim();
            }
            // Title
            else if (svgTag.equals("dc:title")) {
                this._title = element.getTextContent();
            }
            // Rectangle Example: <rect x="5.0" y="5.0" width="100.0" height="100.0" style="fill: rgb(216, 216, 216);"/>
            else if (svgTag.equals("rect")) {
                double x         = this._getAttributeValueAsDouble(element, "x");
                double y         = this._getAttributeValueAsDouble(element, "y");
                double width     = this._getAttributeValueAsDouble(element, "width");
                double height    = this._getAttributeValueAsDouble(element, "height");
                double rx        = this._getAttributeValueAsDouble(element, "rx");
                double ry        = this._getAttributeValueAsDouble(element, "ry");

                shape         = this._getRectangle(x, y, width, height, style, group);
                
                if ((rx == 0) && (ry != 0)) {
                    rx = ry;
                }
                else if ((rx != 0) && (ry == 0)) {
                    ry = rx;
                }
                
                ((Rectangle) shape).setArcWidth (rx * 2);
                ((Rectangle) shape).setArcHeight(ry * 2);
                
                this._addToStyleHistory(element, shape);
                this._setOtherStyles(element, shape);
                this.Add(shape, group);
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            // Circle
            else if (svgTag.equals("circle")) {
                double radius  = this._getAttributeValueAsDouble(element, "r");
                double centerX = this._getAttributeValueAsDouble(element, "cx");
                double centerY = this._getAttributeValueAsDouble(element, "cy");
                
                shape          = this._getCircle(radius, centerX, centerY, style, group);
                
                this._addToStyleHistory(element, shape);
                this._setOtherStyles(element, shape);
                this.Add(shape, group);
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            // Ellipse
            else if (svgTag.equals("ellipse")) {
                double centerX = this._getAttributeValueAsDouble(element, "cx");
                double centerY = this._getAttributeValueAsDouble(element, "cy");
                double radiusX = this._getAttributeValueAsDouble(element, "rx");
                double radiusY = this._getAttributeValueAsDouble(element, "ry");
                
                shape          = this._getEllipse(centerX, centerY, radiusX, radiusY, style, group);
                
                this._addToStyleHistory(element, shape);
                this._setOtherStyles(element, shape);
                this.Add(shape, group);
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            // Polygon
            else if (svgTag.equals("polygon")) {
                String points = this._getAttributeValueAsString(element, "points");
                
                shape         = this._getPolygon(points, style, group);
                
                this._addToStyleHistory(element, shape);
                this._setOtherStyles(element, shape);
                this.Add(shape, group);
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            // Text
            else if (svgTag.equals("text")) {
                double x      = this._getAttributeValueAsDouble(element, "x");
                double y      = this._getAttributeValueAsDouble(element, "y");
                
                TextLayout textPane  = new TextLayout(0, 0);
                String     transform = this._getAttributeValueAsString(element, "transform");
                
                // Translate
                if (transform.contains("translate")) {
                    transform                = transform.substring("translate(".length(), transform.length() - 1);
                    
                    String[] translateValues = this._getArrayFromArrayListOfString(this._getSvgPath(transform));
                    
                    double translateX        = _parseDouble(translateValues[0].trim());
                    double translateY        = _parseDouble(translateValues[1].trim());
                    
                    textPane.setTranslateX(translateX + x);
                    textPane.setTranslateY(translateY + y);
                }
                // Look for transform matrix
                // Example: transformMatrix="matrix(scaleX, skewY, skewX, scaleY, translateX, translateY)";
                else if (transform.contains("matrix")) {
                    transform                      = transform.substring("matrix(".length(), transform.length() - 1);
                    String transformMatrixValues[] = this._getArrayFromArrayListOfString(this._getSvgPath(transform));
                    
                    double scaleX                  = _parseDouble(transformMatrixValues[0]);
                    double scaleY                  = _parseDouble(transformMatrixValues[3]);
                    
                    double skewY                   = _parseDouble(transformMatrixValues[1]);
                    double skewX                   = _parseDouble(transformMatrixValues[2]);
                    
                    double translateX              = _parseDouble(transformMatrixValues[4]);
                    double translateY              = _parseDouble(transformMatrixValues[5]);
                    
                    textPane.setScaleX(scaleX);
                    textPane.setScaleY(scaleY);
                    
                    textPane.setTranslateX(translateX + x);
                    textPane.setTranslateY(translateY + y);
                    
                    textPane.getTransforms().add(new Shear(skewX, skewY));
                }
                
                
                this.Add(textPane, group);
                this._readText(element, textPane);
            }
            // Path
            else if (svgTag.equals("path")) {
                String pathDescription = this._getAttributeValueAsString(element, "d");
                
                if (element.getAttribute("fill").trim().equals("")) {
                    shape          = this._getPath(pathDescription.trim(), style, group);
                }
                else {
                    shape          = this._getPath(pathDescription.trim(), "fill:" + element.getAttribute("fill"), group);
                }
                
                this._addToStyleHistory(element, shape);
                this._setOtherStyles(element, shape);
                this.Add(shape, group);
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            // Line
            else if (svgTag.equals("line")) {
                double x1 = this._getAttributeValueAsDouble(element, "x1");
                double y1 = this._getAttributeValueAsDouble(element, "y1");
                double x2 = this._getAttributeValueAsDouble(element, "x2");
                double y2 = this._getAttributeValueAsDouble(element, "y2");
                
                shape     = this._getLine(x1, y1, x2, y2, style, group);
                
                this._addToStyleHistory(element, shape);
                this._setOtherStyles(element, shape);
                this.Add(shape, group);
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            // Polyline
            else if (svgTag.equals("polyline")) {
                String            pointsAsString            = this._getAttributeValueAsString(element, "points");
                ArrayList<String> pointsAsArrayListOfString = this._getSvgPath(pointsAsString);
                
                ArrayList<Double> points                    = new ArrayList<>();
                
                for (String value : pointsAsArrayListOfString) {
                    points.add(_parseDouble(value));
                }
                
                shape                                       = this._getPolyline(points, style, group);
                
                this._addToStyleHistory(element, shape);
                this._setOtherStyles(element, shape);
                this.Add(shape, group);
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            // Image
            else if (svgTag.equals("image")) {
                String    imageAddress = this._getAttributeValueAsString(element, "xlink:href");
                double    x            = this._getAttributeValueAsDouble(element, "x");
                double    y            = this._getAttributeValueAsDouble(element, "y");
                double    width        = this._getAttributeValueAsDouble(element, "width");
                double    height       = this._getAttributeValueAsDouble(element, "height");

                ImageView image        = this._getImage(imageAddress, x, y, width, height);
                
                String transform = this._getAttributeValueAsString(element, "transform");
                
                // Scale
                if (transform.contains("scale")) {
                    int scaleIndex = transform.indexOf("scale(") + "scale(".length();
                    
                    for (int j = scaleIndex - 1; j < transform.length(); j++) {
                        if (transform.charAt(j) == ')') {
                            String scaleValues = transform.substring(scaleIndex, j);
                            
                            if (!scaleValues.contains(",")) {
                                double scale = _parseDouble(scaleValues);
                                image.setScaleX(scale);
                                image.setScaleY(scale);
                            }
                            else {
                                String[] scaleValuesWithXandY = scaleValues.split(",");
                                
                                double scaleX = _parseDouble(scaleValuesWithXandY[0].trim());
                                double scaleY = _parseDouble(scaleValuesWithXandY[1].trim());
                                
                                image.setScaleX(scaleX);
                                image.setScaleY(scaleY);
                                
                                x = Math.abs(x);
                                y = Math.abs(y);
                                
                                if (scaleX < 0) {
                                    image.setTranslateX(x - width);
                                }
                                
                                if (scaleY < 0) {
                                    image.setTranslateY(y - height);
                                }
                            }
                            
                            break;
                        }
                    }
                    
                    // Remove scale(x, y) portion from the string
                    if (transform.contains("translate")) {
                        String modifiedransform = "";
                    
                        int translateIndex = transform.indexOf("translate(");
                        
                        for (int j = translateIndex - 1; j < transform.length(); j++) {
                            if (transform.charAt(j) == ')') {
                                modifiedransform = transform.substring(translateIndex, j + 1);
                                break;
                            }
                        }
                        
                        transform = modifiedransform;
                    }
                }

                this.Add(image, group);
                
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            // Filter
            else if (svgTag.equals("filter")) {
                Filter filter = new Filter(id);
                this._addToFilterList(filter);
                
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            // Gaussian Blur
            else if (svgTag.equals("feGaussianBlur")) {
                FeGaussianBlur feGaussianBlur = new FeGaussianBlur(id, this._getAttributeValueAsDouble(element, "stdDeviation"));
                this._addToLastFilter(feGaussianBlur);
                
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            // Group
            else if (svgTag.equals("g")) {
                Pane   newGroup  = new Pane();
                String transform = this._getAttributeValueAsString(element, "transform");
                
                // Translate
                if (transform.contains("translate")) {
                    transform = transform.substring("translate(".length(), transform.length() - 1);

                    String[] translateValues = this._getArrayFromArrayListOfString(this._getSvgPath(transform));
                    
                    double translateX = _parseDouble(translateValues[0].trim());
                    double translateY = translateX;
                    
                    if (translateValues.length > 1) {
                        translateY = _parseDouble(translateValues[1].trim());
                    }
                    
                    newGroup.setTranslateX(translateX);
                    newGroup.setTranslateY(translateY);
                }
                // Look for transform matrix
                // Example: transformMatrix="matrix(scaleX, skewY, skewX, scaleY, translateX, translateY)";
                else if (transform.contains("matrix")) {
                    transform                      = transform.substring("matrix(".length(), transform.length() - 1);
                    String transformMatrixValues[] = this._getArrayFromArrayListOfString(this._getSvgPath(transform));

                    double scaleX                  = _parseDouble(transformMatrixValues[0]);
                    double scaleY                  = _parseDouble(transformMatrixValues[3]);

                    double skewY                   = _parseDouble(transformMatrixValues[1]);
                    double skewX                   = _parseDouble(transformMatrixValues[2]);

                    double translateX              = _parseDouble(transformMatrixValues[4]);
                    double translateY              = _parseDouble(transformMatrixValues[5]);

                    newGroup.setScaleX(scaleX);
                    newGroup.setScaleY(scaleY);

                    newGroup.setTranslateX(translateX);
                    newGroup.setTranslateY(translateY);

                    newGroup.getTransforms().add(new Shear(skewX, skewY));
                }
                
                this.Add(newGroup, group);
                
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), newGroup);
            }
            // Linear Gradient
            else if (svgTag.equals("linearGradient")) {
                if (!element.hasAttribute("xlink:href")) {
                    ArrayList<Element> stopElements      = this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes());
                    ArrayList<Stop>    stopsForGradient  = new ArrayList<>();
                    
                    /*
                    Example:
                    
                    <linearGradient
                        id="linearGradient4138">
                        <stop
                            id="stop4140"
                            offset="0"
                            style="stop-color:#d35f00;stop-opacity:1;" />
                        <stop
                            id="stop4142"
                            offset="1"
                            style="stop-color:#d35f00;stop-opacity:0;" />
                    </linearGradient>
                    
                     */
                    for (Element stopElement : stopElements) {
                        String            stopStyle      = stopElement.getAttribute("style");
                        ArrayList<String> stopStyleData  = this._getStyleData(stopStyle);
                        Double            stopOpacity    = 1.0;
                        Color             stopColor      = Color.WHITE;  
                        Double            offset         = 0.0;
                        
                        // Stop Color
                        for (int stopStyleIndex = 0; stopStyleIndex < stopStyleData.size(); stopStyleIndex++) {
                            String stopStylePropertyName = stopStyleData.get(stopStyleIndex);
                            stopColor                    = (Color) this._getColorFromStyleData(stopStyleData.get(++stopStyleIndex));
                            
                            if (stopStylePropertyName.equals("stop-color")) {
                                offset                   = this._getAttributeValueAsDouble(stopElement, "offset");     
                                break;
                            }
                        }

                        // Stop Opacity
                        for (int stopStyleIndex = 0; stopStyleIndex < stopStyleData.size(); stopStyleIndex++) {
                            String stopStylePropertyName = stopStyleData.get(stopStyleIndex);
                            
                            if (stopStylePropertyName.equals("stop-opacity")) {
                                stopOpacity              = _parseDouble(stopStyleData.get(++stopStyleIndex));
                                break;
                            }
                        }
                        
                        stopColor                        = this._getColorWithOpacity(stopColor, stopOpacity);
                        Stop stop                        = new Stop(offset, stopColor);

                        stopsForGradient.add(stop);
                    }

                    Fill fill = new Fill();
                    fill.SetId(id);
                    fill.Stops = stopsForGradient;
                    
                    this._fills.add(fill);
                }
                /*
                 Example:
                
                <linearGradient
                    gradientUnits="userSpaceOnUse"
                    y2="263.79078"
                    x2="669.0714"
                    y1="263.79078"
                    x1="125.21429"
                    id="linearGradient4144"
                    xlink:href="#linearGradient4138" />
                
                 */
                
                else {
                    String gradientIdLink = element.getAttribute("xlink:href");
                    gradientIdLink        = gradientIdLink.substring(1, gradientIdLink.length());
                    Fill fillForGradient  = null;
                    
                    for (Fill currentFill : this._fills) {
                        if (currentFill.IsIdExists(gradientIdLink)) {
                            currentFill.SetId(id);
                            fillForGradient = currentFill;
                            
                            break;
                        }
                    }
                    
                    if (fillForGradient != null) {
                        double         x1             = this._getAttributeValueAsDouble(element, "x1");
                        double         x2             = this._getAttributeValueAsDouble(element, "x2");
                        double         y1             = this._getAttributeValueAsDouble(element, "y1");
                        double         y2             = this._getAttributeValueAsDouble(element, "y2");
                        LinearGradient linearGradient = new LinearGradient(x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, fillForGradient.Stops);
                        
                        fillForGradient.PaintData     = linearGradient;
                    }
                }
            }
            // Radial Gradient
            else if (svgTag.equals("radialGradient")) {
                /*
                Example:
                
                <linearGradient
                    inkscape:collect="always"
                    id="linearGradient4750">
                   <stop
                      style="stop-color:#ecc8b2;stop-opacity:1;"
                      offset="0"
                      id="stop4752" />
                   <stop
                      style="stop-color:#ecc8b2;stop-opacity:0;"
                      offset="1"
                      id="stop4754" />
                 </linearGradient>
                 <radialGradient
                    inkscape:collect="always"
                    xlink:href="#linearGradient4750"
                    id="radialGradient4756"
                    cx="408.57131"
                    cy="349.50478"
                    fx="408.57131"
                    fy="349.50478"
                    r="303.35728"
                    gradientTransform="translate(0,-2.2445829e-4)"
                    gradientUnits="userSpaceOnUse" />
                
                 */
                String gradientIdLink                = element.getAttribute("xlink:href");
                
                double r                             = this._getAttributeValueAsDouble(element, "r");
                double fy                            = this._getAttributeValueAsDouble(element, "fy");
                double fx                            = this._getAttributeValueAsDouble(element, "fx");
                double cy                            = this._getAttributeValueAsDouble(element, "cy");
                double cx                            = this._getAttributeValueAsDouble(element, "cx");
                
                if (!gradientIdLink.trim().equals("")) {
                    gradientIdLink                       = gradientIdLink.substring(1, gradientIdLink.length());
                    Fill fillForGradient                 = null;

                    for (Fill currentFill : this._fills) {
                        if (currentFill.IsIdExists(gradientIdLink)) {
                            currentFill.SetId(id);
                            fillForGradient = currentFill;

                            break;
                        }
                    }

                    if (fillForGradient != null) {
                        double          scaleX             = 1;
                        double          scaleY             = 1;
                        double          skewX              = 0;
                        double          skewY              = 0;
                        double          translateX         = 0;
                        double          translateY         = 0;
                        
                        String          gradientTransform  = this._getAttributeValueAsString(element, "gradientTransform");

                        if (!gradientTransform.equals("")) {
                            // Example: gradientTransform="matrix(1.5119581,0.02858043,-0.01200702,0.63519386,-122.45207,118.38238)"
                            // Where:   gradientTransform="matrix(scaleX, skewY, skewX, scaleY, translateX, translateY)";
                            gradientTransform = gradientTransform.substring("matrix(".length(), gradientTransform.length() - 1);
                            
                            if (gradientTransform.contains("matrix")) {
                                String[] transformMatrixValues = gradientTransform.split(",");

                                scaleX                         = _parseDouble(transformMatrixValues[0].trim());
                                skewY                          = _parseDouble(transformMatrixValues[1].trim());
                                skewX                          = _parseDouble(transformMatrixValues[2].trim());
                                scaleY                         = _parseDouble(transformMatrixValues[3].trim());
                                translateX                     = _parseDouble(transformMatrixValues[4].trim());
                                translateY                     = _parseDouble(transformMatrixValues[5].trim());
                            }
                        }

                        RadialGradient  radialGradient  = new RadialGradient(
                                Math.atan2(fy, fx),
                                Math.sqrt(Math.pow(fx - cx, 2) + Math.pow(fy - cy, 2)),
                                cx + translateX,
                                cy + translateY,
                                r,
                                false,
                                CycleMethod.NO_CYCLE,
                                fillForGradient.Stops
                        );

                        fillForGradient.PaintData       = radialGradient;
                    }
                }
                /*
                
                Example:
                
                <radialGradient id="SVGID_424_" cx="758.5794" cy="1770.6367" r="18.9312" gradientUnits="userSpaceOnUse">
                    <stop  offset="0" style="stop-color:#FFF2DB"/>
                    <stop  offset="0.309" style="stop-color:#D7B189"/>
                    <stop  offset="0.7508" style="stop-color:#92562D"/>
                    <stop  offset="0.7809" style="stop-color:#8D5027"/>
                    <stop  offset="1" style="stop-color:#541513"/>
                </radialGradient>
                
                 */
                else {
                    NodeList radialGradientStops     = element.getChildNodes();
                    ArrayList <Element> stopElements = new ArrayList<>();
                    
                    for (int stopNodeIndex = 0; stopNodeIndex < radialGradientStops.getLength(); stopNodeIndex++) {
                        org.w3c.dom.Node stopNode    = radialGradientStops.item(stopNodeIndex);
                        
                        if (stopNode.getNodeType() == element.ELEMENT_NODE) {
                            stopElements.add((Element) stopNode);
                        }
                    }
                    
                    Fill fill  = new Fill();
                    fill.SetId(id);
                    
                    for (Element stopElement : stopElements) {
                        double offset                = this._getAttributeValueAsDouble(stopElement, "offset");
                        Color  stopColor             = Color.TRANSPARENT;
                        
                        String stopColorAsString     = this._getAttributeValueAsString(stopElement, "style");
                        
                        if (stopColorAsString.contains("#")) {
                            String hexColor          = stopColorAsString.substring("stop-color:".length());
                            stopColor                = Color.web(hexColor);
                        }
                        else {
                            stopColorAsString        = stopColorAsString.substring("stop-color:rgb(".length(), stopColorAsString.length() - 1);
                            
                            String[] stopColorValues = stopColorAsString.split(",");
                            
                            double   red             = _parseDouble(stopColorValues[0].trim());
                            double   green           = _parseDouble(stopColorValues[1].trim());
                            double   blue            = _parseDouble(stopColorValues[2].trim());
                            
                            stopColor                = Color.rgb((int) red, (int) green, (int) blue);
                        }
                        
                        Stop stop                    = new Stop(offset, stopColor);
                        
                        fill.Stops.add(stop);
                    }
                    
                    RadialGradient radialGradient    = new RadialGradient(
                            0,
                            0.1,
                            cx,
                            cy,
                            r,
                            false,
                            CycleMethod.NO_CYCLE,
                            fill.Stops
                    );
                    
                    fill.PaintData                   = radialGradient;
                    this._fills.add(fill);
                }
            }
            // defs
            else {
                this._readSvgElement(this._getListOfElementsFromNodeList(this._xmlDocument, element.getChildNodes()), group);
            }
            
            if (shape != null) {
                String transform = this._getAttributeValueAsString(element, "transform");
                
                // Scale
                if (transform.contains("scale")) {
                    int scaleIndex = transform.indexOf("scale(") + "scale(".length();
                    
                    for (int j = scaleIndex - 1; j < transform.length(); j++) {
                        if (transform.charAt(j) == ')') {
                            String scaleValues = transform.substring(scaleIndex, j);
                            
                            if (!scaleValues.contains(",")) {
                                double scale = _parseDouble(scaleValues);
                                shape.setScaleX(scale);
                                shape.setScaleY(scale);
                            }
                            else {
                                String[] scaleValuesWithXandY = scaleValues.split(",");
                                
                                double scaleX = _parseDouble(scaleValuesWithXandY[0].trim());
                                double scaleY = _parseDouble(scaleValuesWithXandY[1].trim());
                                
                                shape.setScaleX(scaleX);
                                shape.setScaleY(scaleY);
                            }
                            
                            break;
                        }
                    }
                    
                    // Remove scale(x, y) portion from the string
                    if (transform.contains("translate")) {
                        String modifiedransform = "";
                    
                        int translateIndex = transform.indexOf("translate(");
                        
                        for (int j = translateIndex - 1; j < transform.length(); j++) {
                            if (transform.charAt(j) == ')') {
                                modifiedransform = transform.substring(translateIndex, j + 1);
                                break;
                            }
                        }
                        
                        transform = modifiedransform;
                    }
                }
                
                // Translate
                if (transform.contains("translate") && !transform.contains("scale")) {
                    transform                = transform.trim().substring("translate(".length(), transform.length() - 1);

                    String[] translateValues = this._getArrayFromArrayListOfString(this._getSvgPath(transform));
                    
                    double translateX        = _parseDouble(translateValues[0].trim());
                    double translateY        = _parseDouble(translateValues[1].trim());
                    
                    shape.setTranslateX(translateX);
                    shape.setTranslateY(translateY);
                }
                // Look for transform matrix
                // Example: transformMatrix="matrix(scaleX, skewY, skewX, scaleY, translateX, translateY)";
                else if (transform.contains("matrix")) {
                    transform                      = transform.substring("matrix(".length(), transform.length() - 1);
                    String transformMatrixValues[] = this._getArrayFromArrayListOfString(this._getSvgPath(transform));
                    
                    double scaleX                  = _parseDouble(transformMatrixValues[0]);
                    double scaleY                  = _parseDouble(transformMatrixValues[3]);
                    
                    double skewY                   = _parseDouble(transformMatrixValues[1]);
                    double skewX                   = _parseDouble(transformMatrixValues[2]);
                    
                    double translateX              = _parseDouble(transformMatrixValues[4]);
                    double translateY              = _parseDouble(transformMatrixValues[5]);
                    
                    shape.setScaleX(scaleX);
                    shape.setScaleY(scaleY);
                    
                    shape.setTranslateX(translateX);
                    shape.setTranslateY(translateY);
                    
                    shape.getTransforms().add(new Shear(skewX, skewY));
                }
            }
        }
    }
    
    private ArrayList<String> _getStyleData(String style) {
        // Example: "fill:none;stroke:#000000;stroke-opacity:1"
        ArrayList<String> styleData = new ArrayList<>();
        
        String[] properties = style.split("\\:");
        
        for (String property : properties) {
            // Example: #000000; stroke-opacity
            String[] attributeAndValues = property.split(";");
            
            for (String data : attributeAndValues) {
                data = data.trim();
                
                if (!data.equals("")) {
                    styleData.add(data);
                }
            }
        }
        
        return styleData;
    }
    
    private void _readSvgObjectStyle(String style, Shape shape, Pane parent) {
        ArrayList<String> styleData = this._getStyleData(style);
        
        shape.setStroke(Color.TRANSPARENT);
        
        if (style.trim().equals("") || (style == null)) {
            shape.setStroke(Color.BLACK);
            shape.setFill(Color.BLACK);
        }
        
        for (int index = 0; index < styleData.size(); index++) {
            String propertyName  = styleData.get(index);
            String propertyValue = styleData.get(++index);
            _UNIT  currentUnit   = _UNIT.PIXEL;
            
            // Pixel (px)
            if (propertyValue.contains("px")) {
                propertyValue = propertyValue.substring(0, propertyValue.length() - "px".length());
                currentUnit   = _UNIT.PIXEL;
            }
            // Point (pt)
            else if (propertyValue.contains("pt")) {
                propertyValue = propertyValue.substring(0, propertyValue.length() - "pt".length());
                currentUnit   = _UNIT.POINT;
            }
            
            propertyValue = propertyValue.trim();
            
            // Fill Color            
            if (propertyName.equals("fill")) {
                // Example: none or #FF00FF or rgb(255, 128, 100)
                Paint paint = this._getColorFromStyleData(propertyValue);
                
                if (paint instanceof Color) {
                    Color fillColor = (Color) paint;
                    fillColor       = this._getColorWithOpacity(fillColor, fillColor.getOpacity());

                    shape.setFill(fillColor);
                }
                // Linear Gradient
                else if (paint instanceof LinearGradient) { 
                    shape.setFill((LinearGradient) paint);
                }
                // Radial Gradient
                else if (paint instanceof RadialGradient) { 
                    shape.setFill((RadialGradient) paint);
                }
            }
            // Fill Opacity
            else if (propertyName.equals("fill-opacity")) {
                // Example: fill-opacity:0.28994087
                if (shape.getFill() instanceof Color) {
                    double fillOpacity = _parseDouble(propertyValue);
                    Color  fillColor   = this._getColorWithOpacity((Color) shape.getFill(), fillOpacity);

                    shape.setFill(fillColor);
                }
            }
            // Stroke Color
            else if (propertyName.equals("stroke")) {
                // Example: stroke:#000000
                Paint paint = this._getColorFromStyleData(propertyValue);
                
                if (paint instanceof Color) {
                    Color strokeColor = (Color) paint;
                    strokeColor       = this._getColorWithOpacity(strokeColor, strokeColor.getOpacity());
                    
                    if (!propertyValue.equals("none")) {
                        shape.setStroke(strokeColor);
                    }
                }
            }
            // Stroke Opacity
            else if (propertyName.equals("stroke-opacity")) {
                // Example: stroke-opacity:1
                if (shape.getStroke() instanceof Color) {
                    double strokeOpacity = _parseDouble(propertyValue);
                    Color strokeColor    = this._getColorWithOpacity((Color) shape.getStroke(), strokeOpacity);
                    shape.setStroke(strokeColor);
                }
            }
            // Stroke Width
            else if (propertyName.equals("stroke-width")) {
                // Example: stroke-width:1.0
                double strokeWidth = this._getConvertedValue(currentUnit, _parseDouble(propertyValue));
                shape.setStrokeWidth(strokeWidth);
            }
            // Stroke Miter Limit
            else if (propertyName.equals("stroke-miterlimit")) {
                // Example: stroke-miterlimit:4
                double strokeMiterLimit = this._getConvertedValue(currentUnit, _parseDouble(propertyValue));;
                
                shape.setStrokeMiterLimit(strokeMiterLimit);
            }
            // Stroke Line Join
            else if (propertyName.equals("stroke-linejoin")) {
                // Example: stroke-linejoin:miter

                // Miter
                if (propertyValue.equals("miter")) {
                    shape.setStrokeLineJoin(StrokeLineJoin.MITER);
                }
                // Round
                else if (propertyValue.equals("round")) {
                    shape.setStrokeLineJoin(StrokeLineJoin.ROUND);
                }
                // Bevel
                else if (propertyValue.equals("bevel")) {
                    shape.setStrokeLineJoin(StrokeLineJoin.BEVEL);
                }
            }
            // Stroke Dash Array
            else if (propertyName.equals("stroke-dasharray")) {
                // Example: stroke-dasharray:50,50
                if (!propertyValue.equals("none")) {
                    ArrayList<Double> dashArray = new ArrayList<>();
                    
                    for (String dashArrayValue : propertyValue.split(",")) {
                        dashArray.add(_parseDouble(dashArrayValue.trim()));
                    }
                    
                    shape.getStrokeDashArray().addAll(dashArray);
                }
            }
            // Stroke Dash Offset
            else if (propertyName.equals("stroke-dashoffset")) {
                // Example: stroke-dashoffset:0
                double strokeDashOffset = this._getConvertedValue(currentUnit, _parseDouble(propertyValue));;
                
                shape.setStrokeDashOffset(strokeDashOffset);
            }
            // Stroke Line Cap
            else if (propertyName.equals("stroke-linecap")) {
                // Example: stroke-linecap:butt

                // Butt
                if (propertyValue.equals("butt")) {
                    shape.setStrokeLineCap(StrokeLineCap.BUTT);
                }
                // Round
                else if (propertyValue.equals("round")) {
                    shape.setStrokeLineCap(StrokeLineCap.ROUND);
                }
                // Square
                else if (propertyValue.equals("square")) {
                    shape.setStrokeLineCap(StrokeLineCap.SQUARE);
                }
            }
            // Opacity
            else if (propertyName.equals("opacity")) {
                // Example: opacity:0.5
                double opacity = _parseDouble(propertyValue);
                shape.setOpacity(opacity);
            }
            // Filter
            else if (propertyName.equals("filter")) {
                // Example: filter:url(#filter4498)
                String filterId = propertyValue.substring("url(#".length(), propertyValue.length() - 1);
                Filter filter   = this._getFilterById(filterId);

                if (filter.GetFilterPrimitiveElement() != null) {
                    // Gaussian Blur
                    if (filter.GetFilterPrimitiveElement().FilterPrimitiveElementType == FilterPrimitiveElement.FILTER_PRIMITIVE_ELEMENT_TYPE.FE_GAUSSIAN_BLUR) {
                        FeGaussianBlur feGaussianBlur = (FeGaussianBlur) filter.GetFilterPrimitiveElement();
                        this._setGaussianBlur(feGaussianBlur, shape);
                    }
                }
            }
            // Font Size
            else if (propertyName.equals("font-size")) {
                // Example: font-size:40px
                double fontSize = this._getConvertedValue(currentUnit, _parseDouble(propertyValue));
                
                shape.setStyle(shape.getStyle() + "-fx-font-size: " + fontSize +";");
                shape.setTranslateY(shape.getTranslateY());
            }
            // Font Weight
            else if (propertyName.equals("font-weight")) {
                // Example: font-weight:normal
                shape.setStyle(shape.getStyle() + "-fx-font-weight: " + propertyValue + ";");
            }
            // Font Style
            else if (propertyName.equals("font-style")) {
                // Example: font-style:italic
                shape.setStyle(shape.getStyle() + "-fx-font-style: " + propertyValue + ";");
            }
            // Font Family
            else if (propertyName.equals("font-family")) {
                // Example: font-family:sans-serif
                shape.setStyle(shape.getStyle() + "-fx-font-family: " + propertyValue + ";");
            }
        }
    }
    
    
    
    private double _getAttributeValueAsDouble(Element element, String attribute) {
        return this._getAttributeValueAsDouble(element, attribute, 0.0);
    }
    
    private double _getAttributeValueAsDouble(Element element, String attribute, double defaultValue) {
        return element.getAttribute(attribute).toString().trim().equals("") ? defaultValue : _parseDouble(element.getAttribute(attribute).toString().trim());
    }
    
    private String _getAttributeValueAsString(Element element, String attribute) {
        return element.getAttribute(attribute).toString();
    }
    
    private Paint _getColorFromStyleData(String colorData) {
        Color color = Color.TRANSPARENT;
        
        if (!colorData.equals("none")) {
            if (colorData.contains("rgb")) {
                colorData = colorData.substring("rgb(".length(), colorData.length() - 1);
                String[] colorComponentsAsString = colorData.split(",");
                
                int red   = Integer.parseInt(colorComponentsAsString[0].trim());
                int green = Integer.parseInt(colorComponentsAsString[1].trim());
                int blue  = Integer.parseInt(colorComponentsAsString[2].trim());
                
                color     = Color.rgb(red, green, blue);
            }
            else if (colorData.contains("url(")) {
                String gradientId = colorData.substring("url(#".length(), colorData.length() - 1);
                Paint  gradient   = null;

                for (Fill currentFill : this._fills) {
                    if (currentFill.IsIdExists(gradientId)) {
                        gradient = currentFill.PaintData;
                        
                        break;
                    }
                }
                
                return gradient;
            }
            else if (colorData.contains("#")) {
                color = Color.web(colorData);
            }
        }
        
        return color;
    }
    
    private Color _getColorWithOpacity(Color color, double opacity) {
        Color outputColor = null;
        
        if (color.getOpacity() != 0) {
            if (color != null) {
                int red     = (int) this._getTruncatedRgbValue(color.getRed()   * 255.0);
                int green   = (int) this._getTruncatedRgbValue(color.getGreen() * 255.0);
                int blue    = (int) this._getTruncatedRgbValue(color.getBlue()  * 255.0);

                outputColor = Color.rgb(red, green, blue, opacity);
            }
            
            return outputColor;
        }
        else {
            return color;
        }
    }
    
    private double _getTruncatedRgbValue(double value) {
        return this._getTruncatedValue(value, 0, 255);
    }
    
    private double _getTruncatedValue(double value, double min, double max) {
        if (value < 0) {
            value = 0;
        }
        else if (value > 255) {
            value = 255;
        }
        
        return value;
    }
    
    private void _addToFilterList(Filter filter) {
        this._filters.add(filter);
    }
    
    private void _addToLastFilter(FilterPrimitiveElement filterPrimitiveElement) {
        if (this._filters.size() > 0) {
            // Gaussian Blur
            if (filterPrimitiveElement instanceof FeGaussianBlur) {
                this._filters.get(this._filters.size() - 1).SetFilterPrimitiveElement(filterPrimitiveElement);
            }
        }
    }
    
    private Filter _getFilterById(String id) {
        for (Filter filter : this._filters) {
            if (filter.GetId().equals(id)) {
                return filter;
            }
        }
        
        return null;
    }
    
    private void _setGaussianBlur(FeGaussianBlur feGaussianBlur, Shape shape) {
        // Relation between sigma and radius on the Gaussian blur: http://stackoverflow.com/questions/21984405/relation-between-sigma-and-radius-on-the-gaussian-blur
        double radius = feGaussianBlur.StandardDeviation * Math.sqrt(2 * Math.log(255)) - 1;
        
        GaussianBlur gaussianBlur = new GaussianBlur(radius);
        shape.setEffect(gaussianBlur);
    }
    
    private Rectangle _getRectangle(double x, double y, double width, double height, String style, Pane parent) {
        Rectangle rectangle = new Rectangle(x, y, width, height);
        this._readSvgObjectStyle(style, rectangle, parent);
        
        return rectangle;
    }
    
    private Circle _getCircle(double radius, double centerX, double centerY, String style, Pane parent) {
        Circle circle = new Circle(centerX, centerY, radius);
        this._readSvgObjectStyle(style, circle, parent);
        
        return circle;
    }
    
    private Ellipse _getEllipse(double centerX, double centerY, double radiusX, double radiusY, String style, Pane parent) {
        Ellipse ellipse = new Ellipse(centerX, centerY, radiusX, radiusY);
        this._readSvgObjectStyle(style, ellipse, parent);
        
        return ellipse;
    }
    
    private Path _getPath(String pathDescription, String style, Pane parent) {
        pathDescription = this._getConvertedPathCommandsFromScientificNotaitonToDecimal(pathDescription);
        
        Path              path           = new Path();
        ArrayList<String> pathCommands   = this._getSvgPath(pathDescription);
        String            lastCommand    = null;
        
        double            lastRelativeX  = 0;
        double            lastRelativeY  = 0;
        
        for (int i = 0; i < pathCommands.size(); i++) {
            String command = pathCommands.get(i);
            
            // Current command is not a number, then it must be one of these { "M", "m", "Z", "z", "L", "l", "H", "h", "V", "v", "S", "s", "C", "c", "Q", "q", "T", "t", "A", "a" }
            if (!Character.isDigit(command.charAt(command.length() - 1))) {
                // Move to
                if (command.toLowerCase().equals("m")) {
                    double x      =  _parseDouble(pathCommands.get(i + 1));
                    double y      =  _parseDouble(pathCommands.get(i + 2));
                    
                    lastRelativeX = x;
                    lastRelativeY = y;
                    
                    MoveTo moveTo =  new MoveTo(x, y);
                    
                    path.getElements().add(moveTo);
                    
                    i             += 2;
                    
                    lastCommand   = command;
                }
                // Line to
                else if (command.toLowerCase().equals("l")) { 
                    for ( int j = i + 1;
                         (j < pathCommands.size()) && this._isDigit(pathCommands.get(j));
                          j += 2) {
                        
                        double x = _parseDouble(pathCommands.get(j + 0));
                        double y = x;
                        
                        if (!Character.isAlphabetic(pathCommands.get(j + 1).charAt(pathCommands.get(j + 1).length() - 1))) {
                            y = _parseDouble(pathCommands.get(j + 1));
                        }

                        if (command.equals("l")) {
                            x    += lastRelativeX;
                            y    += lastRelativeY;
                        }
                        
                        lastRelativeX =  x;
                        lastRelativeY =  y;

                        LineTo lineTo = new LineTo(x, y);

                        path.getElements().add(lineTo);
                    }
                    
                    lastCommand = command;
                }
                // Cubic Bézier curve
                else if (command.toLowerCase().equals("c")) {
                    for ( int j = i + 1;
                         (j < pathCommands.size()) && this._isDigit(pathCommands.get(j));
                          j += 6) {

                        double controlX1   =  _parseDouble(pathCommands.get(j + 0));                        
                        double controlY1   =  _parseDouble(pathCommands.get(j + 1));

                        double controlX2   =  _parseDouble(pathCommands.get(j + 2));
                        double controlY2   =  _parseDouble(pathCommands.get(j + 3));
                        
                        double endX        =  _parseDouble(pathCommands.get(j + 4));
                        double endY        =  _parseDouble(pathCommands.get(j + 5));
                        
                        if (command.equals("c")) {
                            controlX1     += lastRelativeX;
                            controlY1     += lastRelativeY;
                            
                            controlX2     += lastRelativeX;
                            controlY2     += lastRelativeY;
                            
                            endX          += lastRelativeX;
                            endY          += lastRelativeY;
                        }
                        
                        lastRelativeX     =  endX;
                        lastRelativeY     =  endY;
                        
                        CubicCurveTo cubicCurveTo = new CubicCurveTo(controlX1, controlY1, controlX2, controlY2, endX, endY);
                        
                        path.getElements().add(cubicCurveTo);
                    }
                    
                    lastCommand = command;
                }
                // Quadratic Bézier Curve
                else if (command.toLowerCase().equals("q")) {
                    for ( int j = i + 1;
                         (j < pathCommands.size()) && this._isDigit(pathCommands.get(j));
                          j += 4) {
                        
                        double controlX   =  this._parseDouble(pathCommands.get(j + 0));
                        double controlY   =  this._parseDouble(pathCommands.get(j + 1));

                        double endX       =  this._parseDouble(pathCommands.get(j + 2));
                        double endY       =  this._parseDouble(pathCommands.get(j + 3));
                        
                        if (command.equals("q")) {
                            controlX      += lastRelativeX;
                            controlY      += lastRelativeY;

                            endX          += lastRelativeX;
                            endY          += lastRelativeY;
                        }
                        
                        lastRelativeX     =  endX;
                        lastRelativeY     =  endY;
                        
                        QuadCurveTo quadCurveTo = new QuadCurveTo(controlX, controlY, endX, endY);
                        
                        path.getElements().add(quadCurveTo);
                    }
                    
                    lastCommand = command;
                }
                // Curve to
                else if (command.toLowerCase().equals("s")) {
                    for ( int j = i + 1;
                         (j < pathCommands.size()) && this._isDigit(pathCommands.get(j));
                          j += 4) {
                        
                        double controlX   =  this._parseDouble(pathCommands.get(j + 0));
                        double controlY   =  this._parseDouble(pathCommands.get(j + 1));

                        double endX       =  this._parseDouble(pathCommands.get(j + 2));
                        double endY       =  this._parseDouble(pathCommands.get(j + 3));
                        
                        if (command.equals("s")) {
                            controlX      += lastRelativeX;
                            controlY      += lastRelativeY;

                            endX          += lastRelativeX;
                            endY          += lastRelativeY;
                        }
                        
                        lastRelativeX     =  endX;
                        lastRelativeY     =  endY;
                        
                        QuadCurveTo quadCurveTo = new QuadCurveTo(controlX, controlY, endX, endY);
                        
                        path.getElements().add(quadCurveTo);
                    }
                    
                    lastCommand = command;
                }
                // Arc to
                else if (command.toLowerCase().equals("a")) {
                    for ( int j = i + 1;
                         (j < pathCommands.size()) && this._isDigit(pathCommands.get(j));
                          j += 7) {
                        
                        double  rx             = this._parseDouble(pathCommands.get(j + 0));
                        double  ry             = this._parseDouble(pathCommands.get(j + 1));
                        
                        double  xAxisRotation  = this._parseDouble(pathCommands.get(j + 2));
                        
                        boolean largeArcFlag   = (this._parseDouble(pathCommands.get(j + 3)) == 0) ? false : true;
                        
                        boolean sweepFlag      = (this._parseDouble(pathCommands.get(j + 4)) == 0) ? false : true;
                        
                        double  x              = this._parseDouble(pathCommands.get(j + 5));
                        double  y              = this._parseDouble(pathCommands.get(j + 6));
                        
                        if (command.equals("a")) {
                            x                  += lastRelativeX;
                            y                  += lastRelativeY;
                        }
                        
                        lastRelativeX     =  x;
                        lastRelativeY     =  y;
                        
                        ArcTo arcTo       = new ArcTo(rx, ry, xAxisRotation, x, y, largeArcFlag, sweepFlag);
                        
                        path.getElements().add(arcTo);
                    }
                    
                    lastCommand = command;
                }
                // Vertical
                else if (command.toLowerCase().equals("v")) {
                    double x = lastRelativeX;
                    double y = _parseDouble(pathCommands.get(i + 1));
                    
                    if (command.equals("v")) {
                        y += lastRelativeY;
                    }
                    
                    lastRelativeY = y;
                    
                    LineTo lineTo = new LineTo(x, y);
                    path.getElements().add(lineTo);
                }
                // Horizontal
                else if (command.toLowerCase().equals("h")) {
                    double y = lastRelativeY;
                    double x = _parseDouble(pathCommands.get(i + 1));
                    
                    if (command.equals("h")) {
                        x += lastRelativeX;
                    }
                    
                    lastRelativeX = x;
                    
                    LineTo lineTo = new LineTo(x, y);
                    path.getElements().add(lineTo);
                }
                // Close Path
                else if (command.toLowerCase().equals("z")) {
                    ClosePath closePath = new ClosePath();
                    path.getElements().add(closePath);
                }
                
                lastCommand   =  command;
            }
            // If Digit
            else {
                if (lastCommand.toLowerCase().equals("m")) {
                    double x      =  this._parseDouble(pathCommands.get(i + 0));
                    double y      =  this._parseDouble(pathCommands.get(i + 1));
                    
                    if (lastCommand.equals("m")) {
                        x         += lastRelativeX;
                        y         += lastRelativeY;
                    }
                    
                    lastRelativeX = x;
                    lastRelativeY = y;
                    
                    LineTo lineTo =  new LineTo(x, y);

                    path.getElements().add(lineTo);

                    i             += 1;
                }
            }
        }
        
        this._readSvgObjectStyle(style, path, parent);
        
        return path;
    }
    
    private Polygon _getPolygon(String pointsAttribute, String style, Pane parent) {
        ArrayList<String> pointsAsString = this._getSvgPath(pointsAttribute);
        ArrayList<Double> points         = new ArrayList<>();
        
        for (String currentNumberAsString : pointsAsString) {
            points.add(_parseDouble(currentNumberAsString));
        }
        
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(points);
        
        this._readSvgObjectStyle(style, polygon, parent);
        
        return polygon;
    }
    
    private Line _getLine(double x1, double y1, double x2, double y2, String style, Pane parent) {
        Line line = new Line(x1, y1, x2, y2);
        this._readSvgObjectStyle(style, line, parent);
        
        return line;
    }
    
    private Polyline _getPolyline(ArrayList<Double> points, String style, Pane parent) {
        Polyline polyline = new Polyline();
        polyline.getPoints().addAll(points);
        this._readSvgObjectStyle(style, polyline, parent);
        
        return polyline;
    }
    
    private ImageView _getImage(String imageAddress, double x, double y, double width, double height) {
        Image image = null;
        
        if ((!imageAddress.contains("http://") || !imageAddress.contains("https://")) && !imageAddress.contains(";base64,")) {
            File svgFileDirectory  = new File(this._filePath).getParentFile();
            imageAddress           = svgFileDirectory.getAbsolutePath() + "/" + imageAddress;
            
            image                  = new Image((new File(imageAddress)).toURI().toString());
        }
        else if (imageAddress.contains(";base64,")) {
            imageAddress           = imageAddress.replace("\n", "");
            image                  = this._getImageFromBase64(imageAddress);
        }

        ImageView imageView        = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setTranslateX(x);
        imageView.setTranslateY(y);
        
        return imageView;
    }
    
    private Image _getImageFromBase64(String sourceData) {
        sourceData = sourceData.split("data:image/png;base64,")[1];
        
        BufferedImage bufferedImage = null;
        byte[] imageByte;
        
        try {
            imageByte                                 = DatatypeConverter.parseBase64Binary(sourceData);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageByte);
            bufferedImage                             = ImageIO.read(byteArrayInputStream);
            byteArrayInputStream.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        
        WritableImage writableImage = null;
        
        if (bufferedImage != null) {
            writableImage                             = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
            SwingFXUtils.toFXImage(bufferedImage, writableImage);
        }
        
        return writableImage;
    }
    
    private boolean _isDigit(String text) {
        return Character.isDigit(text.charAt(text.length() - 1));
    }
    
    private String[] _getArrayFromArrayListOfString(ArrayList<String> arrayList) {
        String[] array = new String[arrayList.size()];
        
        for (int i = 0; i < array.length; i++) {
            array[i] = arrayList.get(i);
        }
        
        return array;
    }
    
    private ArrayList<String> _getSvgPath(String pathDescription) {
        final String SVG_PATH_COMMANDS = "MmZzLlHhVvSsCcQqTtAa";
        
        ArrayList<String> pathCommands = new ArrayList<>();
        String            command      = "";
        
        for (int i = 0; i < pathDescription.length(); i++) {
            Character currentCharacter = pathDescription.charAt(i);
            
            if (SVG_PATH_COMMANDS.contains(currentCharacter.toString())) {
                if (!command.trim().equals("")) {
                    pathCommands.add(new String(command));
                    command = "";
                }
                
                pathCommands.add(currentCharacter.toString());
            }
            else if (currentCharacter.toString().equals(",")) {
                if (!command.trim().equals("")) {
                    pathCommands.add(new String(command));
                    command = "";
                }
            }
            else if (Character.isDigit(currentCharacter) || currentCharacter.toString().equals(".")) {
                command += currentCharacter;
            }
            else if (currentCharacter.toString().equals("-") || currentCharacter.toString().equals("+")) {
                if (!command.trim().equals("")) {
                    pathCommands.add(new String(command));
                    command = "";
                }
                
                command += currentCharacter;
            }
            else if (currentCharacter.toString().equals(" ")) {
                if (!command.trim().equals("")) {
                    pathCommands.add(new String(command));
                    command = "";
                }
            }
        }
        
        if (!command.trim().equals("")) {
            pathCommands.add(new String(command));
        }
        
        return pathCommands;
    }
    
    private double _getConvertedValue(_UNIT unit, double value) {
        Double output = value;
        
        switch (unit) {
            case PIXEL:
                output *= 1;
                break;
            
            case POINT:
                output *= 72;
                break;
        }
        
        return output;
    }
    
    private void _setOtherStyles(Element element, Shape shape) {
        String fillData = this._getAttributeValueAsString(element, "fill");
                
        if (fillData.length() > 0) {
            Paint fill = this._getColorFromStyleData(fillData);
            shape.setFill(fill);
        }

        String strokeData = this._getAttributeValueAsString(element, "stroke");

        if (strokeData.length() > 0) {
            Paint stroke = this._getColorFromStyleData(strokeData);
            shape.setStroke(stroke);
        }

        double strokeWidth = this._getAttributeValueAsDouble(element, "stroke-width", Integer.MIN_VALUE);
        
        if (strokeWidth != Integer.MIN_VALUE) {
            shape.setStrokeWidth(strokeWidth);
        }
    }
    
    private void _readText(org.w3c.dom.Element element, TextLayout parent) {
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node node = element.getChildNodes().item(i);
            
            // <text id="XMLID_1_" transform="matrix(1 0 0 1 25.6196 31.7744)" class="st0 st1">Hello World</text>
            if (node.getNodeType() == Node.TEXT_NODE) {
                String text = node.getTextContent();
                Text textPane = new Text(text);
                parent.Add(textPane);
            }
            //  <text x="0" y="0"><tspan x="14.363094" y="21.077381">Hello World</tspan></text>
            else if (node.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element tspan = (org.w3c.dom.Element) node;
                
                /*
                
                <text transform="matrix(1 0 0 1 10 13)">
                    <tspan x="0"    y="0"    class="st0 st1"    >Hello  </tspan>
                    <tspan x="28.6" y="0"    class="st2 st0 st1">Cruel  </tspan>
                    <tspan x="55"   y="0"    class="st0 st1"    >       </tspan>
                    <tspan x="0"    y="43.2" class="st3 st0 st1">world  </tspan>
                    <tspan x="28.9" y="43.2" class="st0 st1"    >       </tspan>
                    <tspan x="31.5" y="43.2" class="st4 st0 st1">again  </tspan>
                </text>
                
                */
                if (tspan.getChildNodes().getLength() == 1) {
                    double x    = this._getAttributeValueAsDouble(tspan, "x");
                    double y    = this._getAttributeValueAsDouble(tspan, "y");
                    String text = tspan.getTextContent();
                    
                    Text textPane = new Text(text);
                    textPane.setTranslateX(x);
                    textPane.setTranslateY(y);
                    parent.Add(textPane);
                    
                    // Read CSS Styles
                    this._addToStyleHistory(element, textPane);
                    this._addToStyleHistory(tspan, textPane);
                    
                    String textElementStyle = this._getAttributeValueAsString(element, "style");

                    if (textElementStyle != null) {
                        this._readSvgObjectStyle(textElementStyle, textPane, parent);
                    }
                    
                    String tspanStyle = tspan.getAttribute("style");
                    
                    if ((tspanStyle != null) && !tspanStyle.trim().equals("")) {
                        this._readSvgObjectStyle(tspanStyle, textPane, parent);
                    }
                }
                /*
                
                <text
                    id="text4507"
                    y="8.6041641"
                    x="-0.79375064"
                    style="font-style:normal;font-weight:normal;font-size:10.58333302px;line-height:19.76437569px;font-family:sans-serif;letter-spacing:0px;word-spacing:0px;fill:#000000;fill-opacity:1;stroke:none;stroke-width:0.26458332px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"
                    xml:space="preserve">
                        <tspan
                            style="stroke-width:0.26458332px"
                            y="8.6041641"
                            x="-0.79375064"
                            id="tspan4505">Hello 
                                <tspan
                                    id="tspan4509"
                                    style="fill:#de8787">World
                                </tspan>
                                <tspan
                                    id="tspan4511"
                                    style="fill:#ff9955">
                                </tspan>
                        </tspan>
                
                        <tspan
                            id="tspan4521"
                            style="stroke-width:0.26458332px"
                            y="28.36854"
                            x="-0.79375064">
                                <tspan
                                    id="tspan4523"
                                    style="fill:#ff9955">
                                </tspan>
                        </tspan>
                
                        <tspan
                            id="tspan4526"
                            style="stroke-width:0.26458332px"
                            y="48.132915"
                            x="-0.79375064">
                            <tspan
                                id="tspan4528"
                                style="fill:#ff9955">Cruel
                            </tspan>
                            <tspan
                                id="tspan4513"
                                style="fill:#55ff99">Again
                            </tspan>
                        </tspan>
                </text>
                
                */
                else if (tspan.getChildNodes().getLength() > 1)  {
                    this._readTSpan(tspan, parent, element);
                }
            }
        }
    }
    
    private void _readTSpan(org.w3c.dom.Node tspan, TextLayout parent, org.w3c.dom.Element textElement) {
        double prevX = 0;
        double prevY = 0;
        
        for (int i = 0; i < tspan.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node tspanContentNode = tspan.getChildNodes().item(i);
            String text = tspanContentNode.getTextContent();
            
            if (tspanContentNode.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element tspanChildElement = (org.w3c.dom.Element) tspanContentNode;
            
                double x = Double.parseDouble(tspan.getAttributes().getNamedItem("x").getNodeValue());
                double y = Double.parseDouble(tspan.getAttributes().getNamedItem("y").getNodeValue());

                Text textPane = new Text(text);

                if ((prevX != x) && (prevY != y)) {
                    HBox line = new HBox(textPane);
                    line.setTranslateX(x);
                    line.setTranslateY(y);
                    parent.Add(line);
                }
                else if (parent.getChildren().size() > 0) {
                    HBox lastItem = (HBox) parent.getChildren().get(parent.getChildren().size() - 1);
                    lastItem.getChildren().add(textPane);
                }

                prevX = x;
                prevY = y;

                // Read CSS Styles
                String textElementStyle = this._getAttributeValueAsString(textElement, "style");

                if (textElementStyle != null) {
                    this._readSvgObjectStyle(textElementStyle, textPane, parent);
                }

                if (tspan.getAttributes() != null) {
                    if (tspan.getAttributes().getNamedItem("style").getNodeValue() != null) {
                        String parentStyle = tspan.getAttributes().getNamedItem("style").getNodeValue();
                        this._readSvgObjectStyle(parentStyle, textPane, parent);
                    }
                }

                String localStyle = this._getAttributeValueAsString(tspanChildElement, "style");

                if (localStyle != null) {
                    this._readSvgObjectStyle(localStyle, textPane, parent);
                }
            }
        }
    }
    
    public void Add(Pane child, Pane parent) {
        parent.getChildren().add(child);
    }
    
    public void Add(Shape child, Pane parent) {
        parent.getChildren().add(child);
    }
    
    public void Add(ImageView child, Pane parent) {
        parent.getChildren().add(child);
    }
    
    public String GetTitle() {
        return this._title;
    }
    
    public double GetWidth() {
        return this._width;
    }
    
    public double GetHeight() {
        return this._height;
    }
    
    private void _addToStyleHistory(String styleClass, Shape shape) {  
        if ((styleClass != null) && !styleClass.trim().equals("")) {
            if (styleClass.split(" ").length == 1) {
                for (StyleHistory currentStyle : this._styleHistory) {
                    if (currentStyle.equals(styleClass)) {
                        return;
                    }
                }
                
                StyleHistory history = new StyleHistory(styleClass);
                history.AddShape(shape);
                this._styleHistory.add(history);
            }
            else if (styleClass.split(" ").length > 1) {
                String[] classes = styleClass.split(" ");
                
                for (String currentStyleClass : classes) {
                    if (!currentStyleClass.equals(styleClass)) {
                        StyleHistory history = new StyleHistory(currentStyleClass);
                        history.AddShape(shape);
                        this._styleHistory.add(history);
                    }
                }
            }
        }
    }
    
    private void _addToStyleHistory(Element element, Shape shape) {
        this._addToStyleHistory(this._getAttributeValueAsString(element, "class"), shape);
        this._addToStyleHistory(this._getAttributeValueAsString(element, "id"),    shape);
    }
    
    private double _parseDouble(String number) {
        return Double.parseDouble(number);
    }
    
    private String _getConvertedPathCommandsFromScientificNotaitonToDecimal(String pathCommand) {
        String temp = "";
        
        for (int i = 0; i < pathCommand.length(); i++) {
            char character = pathCommand.charAt(i);
            
            if (character != 'e') {
                temp += Character.toString(character);
            }
            else {
                String reverseValue = "";
                
                for (int j = temp.length() - 1; j >= 0; j--) {
                    if (Character.isDigit(temp.charAt(j)) || (temp.charAt(j) == '+') || (temp.charAt(j) == '-') || (temp.charAt(j) == '.')) {
                        reverseValue += temp.charAt(j);
                    }
                    else {
                        break;
                    }
                }
                
                String value = "";
                
                for (int j = reverseValue.length() - 1; j >= 0; j--) {
                    value += reverseValue.charAt(j);
                }
                
                temp = temp.substring(0, temp.length() - value.length());
                
                String power = "";
                
                for (int j = ++i; j < pathCommand.length(); j++) {  
                    if (Character.isDigit(pathCommand.charAt(j)) || (pathCommand.charAt(j) == '+') || (pathCommand.charAt(j) == '-') || (temp.charAt(j) == '.')) {
                        power += pathCommand.charAt(j);
                    }
                    else {
                        break;
                    }
                }
                
                String finalValue = String.format("%f", Double.parseDouble(value) * Math.pow(10, Double.parseDouble(power)));
                i++;
                
                temp += finalValue;
            }
        }
        
        pathCommand = temp;
        
        return pathCommand;
    }
}
