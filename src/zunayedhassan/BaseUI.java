/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zunayedhassan;

import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Zunayed Hassan
 */
public class BaseUI extends BorderPane {
    public static boolean IS_STYLESHEET_ALREADY_ADDED = false;
    
    public CommonTools Tools = new CommonTools();
    
    public BaseUI() {

    }
    
    public void InitializeStyleSheets(Scene scene, String[] styleSheets) {
        if (!IS_STYLESHEET_ALREADY_ADDED) {
            for (String stylesheet : styleSheets) {
                this.Tools.SetStyleSheet(scene, stylesheet);
            }
            
            IS_STYLESHEET_ALREADY_ADDED = true;
        }
    }
    
    public void Add(Pane parent, Control chldren) {
        parent.getChildren().add(chldren);
    }
}
