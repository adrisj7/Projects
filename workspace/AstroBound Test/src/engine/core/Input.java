package engine.core;

import javax.management.InvalidAttributeValueException;

import org.lwjgl.glfw.GLFW;

/** Input.java
 *      Handles keyboard and mouse input
 */

public class Input {

    private static final int KEY_STATUS_SIZE = 512;
    private static final int MB_STATUS_SIZE = 4;

    private boolean[] keyStatus;
    private boolean[] keyStatusLast;
    
    private boolean[] mbStatus;
    private boolean[] mbStatusLast;

    private int mouseXpos;
    private int mouseYpos;

    public Input() {
        keyStatus =     new boolean[KEY_STATUS_SIZE];
        keyStatusLast = new boolean[KEY_STATUS_SIZE];
        
        mbStatus =      new boolean[MB_STATUS_SIZE];
        mbStatusLast =  new boolean[MB_STATUS_SIZE];
    }

    /**
     * Utility function, use this to set the value of a key
     * @param key
     */
    void setKey(int key, int glfwKeyAction) {
        if (key < 0 || key >= KEY_STATUS_SIZE) {
            (new InvalidAttributeValueException("Invalid key requested: " + key + ". Cannot go below 0, and maximum key index is " + KEY_STATUS_SIZE + ".")).printStackTrace();
            return;
        }
        if (glfwKeyAction == GLFW.GLFW_PRESS) {
            keyStatus[key] = true;            
        } else if (glfwKeyAction == GLFW.GLFW_RELEASE) {
            keyStatus[key] = false;
        }
    }

    /**
     * Utility function, use this to set the value of a mouse button
     * @param button
     */
    void setMouseButton(int button, int glfwKeyAction) {
        if (button < 0 || button >= MB_STATUS_SIZE) {
            (new InvalidAttributeValueException("Invalid key requested: " + button + ". Cannot go below 0, and maximum key index is " + MB_STATUS_SIZE + ".")).printStackTrace();
            return;
        }
        if (glfwKeyAction == GLFW.GLFW_PRESS) {
            mbStatus[button] = true;            
        } else if (glfwKeyAction == GLFW.GLFW_RELEASE) {
            mbStatus[button] = false;
        }
    }

    /**
     * Utility function, use this to set the value of our mouse position
     * @param xpos
     * @param ypos
     */
    void setMousePosition(int xpos, int ypos) {
        this.mouseXpos = xpos;
        this.mouseYpos = ypos;
    }

    /**
     * Update the input handler
     */
    void update() {
        for(int i = 0; i < KEY_STATUS_SIZE; i++) {
            keyStatusLast[i] = keyStatus[i];
        }
        for(int i = 0; i < MB_STATUS_SIZE; i++) {
            mbStatusLast[i] = mbStatus[i];
        }
    }

    /// UTILITY FUNCTIONS that grab key/mouse statuses and do error checking

    private boolean getKeyStatus(int key) {
        if (key < 0 || key >= KEY_STATUS_SIZE) {
            (new InvalidAttributeValueException("Invalid key requested: " + key + ". Cannot go below 0, and maximum key index is " + KEY_STATUS_SIZE + ".")).printStackTrace();
            return false;
        }
        return keyStatus[key];
    }
    private boolean getKeyStatusLast(int key) {
        if (key < 0 || key >= KEY_STATUS_SIZE) {
            (new InvalidAttributeValueException("Invalid key requested: " + key + ". Cannot go below 0, and maximum key index is " + KEY_STATUS_SIZE + ".")).printStackTrace();
            return false;
        }
        return keyStatusLast[key];        
    }

    private boolean getMouseStatus(int button) {
        if (button < 0 || button >= MB_STATUS_SIZE) {
            (new InvalidAttributeValueException("Invalid key requested: " + button + ". Cannot go below 0, and maximum key index is " + MB_STATUS_SIZE + ".")).printStackTrace();
            return false;
        }
        return mbStatus[button];
    }
    private boolean getMouseStatusLast(int button) {
        if (button < 0 || button >= MB_STATUS_SIZE) {
            (new InvalidAttributeValueException("Invalid key requested: " + button + ". Cannot go below 0, and maximum key index is " + MB_STATUS_SIZE + ".")).printStackTrace();
            return false;
        }
        return mbStatusLast[button];        
    }


    /**
     * @return Is "key" currently being held?
     */
    public boolean keyCheck(int key) {
        return getKeyStatus(key);
    }

    /**
     * @return Has "key" just been pressed?
     */
    public boolean keyCheckPressed(int key) {
        return getKeyStatus(key) && !getKeyStatusLast(key);
    }

    /**
     * @return Has "key" just been released?
     */
    public boolean keyCheckReleased(int key) {
        return !getKeyStatus(key) && getKeyStatusLast(key);
    }
    
    /**
     * @return Is mouse "button" currently being held?
     */
    public boolean mouseCheck(int button) {
        return getMouseStatus(button);
    }

    /**
     * @return Has mouse "button" just been pressed?
     */
    public boolean mouseCheckPressed(int button) {
        return getMouseStatus(button) && !getMouseStatusLast(button);
    }

    /**
     * @return Has mouse "button" just been released?
     */
    public boolean mouseCheckReleased(int button) {
        return !getMouseStatus(button) && getMouseStatusLast(button);
    }

    /**
     * @return The mouse's X position on the screen
     */
    public int getMouseX() {
        return mouseXpos;
    }

    /**
     * @return The mouse's Y position on the screen
     */
    public int getMouseY() {
        return mouseYpos;
    }

}
