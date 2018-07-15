package engine.core;

import java.nio.IntBuffer;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * A me-friendly wrapper for GLFW's Window class
 *
 */
public class Window {

    // LWJGL Window Handle
    private static long window;
    private static int width, height;

    private static Input input;

    public static void initWindow(String name, int width, int height) {
        Window.width = width;
        Window.height = height;

        input = new Input();

        // For some reason, this breaks drawing
//        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
//        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
//        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_ES_API);

//        GLFW.glfwDefaultWindowHints();                             // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);   // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE); // the window will be resizable

        // Create the window
        window = GLFW.glfwCreateWindow(width, height, name, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == 0)
            throw new RuntimeException("Failed to create GLFW window");

        // Input Callbacks
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            input.setKey(key, action);
        });
        GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            input.setMouseButton(button, action);
        });
        GLFW.glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            input.setMousePosition((int)xpos, (int)ypos);
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            // Center the window
            GLFW.glfwSetWindowPos(
                    window, 
                    (vidmode.width() - pWidth.get(0)) / 2, 
                    (vidmode.height() - pHeight.get(0)) / 2);
         } // the stack frame is popped automatically

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);

        Window.setVsync(true);

        Window.showWindow();
    }

    /**
     * To be called after each draw loop
     * <br><br>
     * Swaps the window buffer
     */
    public static void swapBuffers() {
        GLFW.glfwSwapBuffers(window); // swap the color buffers 
    }

    /**
     * Polls events from this window
     */
    public static void pollEvents() {
        // Update input and then poll events.
        // NOTE! THESE MUST BE CALLED IN THAT ORDER!
        // First the input is updated, then events are polled!
        // Otherwise our input system won't work.
        Window.getInput().update();
        GLFW.glfwPollEvents();
    }

    public static void destroy() {
        // Clean up
        GLFW.glfwSetWindowShouldClose(window, true);
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);
    }

    /**
     * Lets you turn vsync on or off
     */
    public static void setVsync(boolean on) {
        GLFW.glfwSwapInterval(on ? 1 : 0);
    }

    /**
     * Makes the window visible
     */
    public static void showWindow() {
        GLFW.glfwShowWindow(window);
    }

    /**
     * Makes the window invisible
     */
    public static void hideWindow() {
        GLFW.glfwHideWindow(window);
    }

    /**
     * Makes the window fullscreen
     */
    public static void enableFullScreen() {
        if (isFullScreen()) {
            return;
        }
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0, vidmode.width(), vidmode.height(), 0);
    }

    /**
     * Makes the window not fullscreen
     */
    public static void disableFullScreen() {
        if (!isFullScreen()) {
            return;
        }
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowMonitor(
                window, 
                MemoryUtil.NULL,
                (vidmode.width() - width) / 2, 
                (vidmode.height() - height) / 2, 
                width, 
                height, 
                0
                );
    }

    /**
     * Is the window fullscreen?
     */
    public static boolean isFullScreen() {
        return (GLFW.glfwGetWindowMonitor(window) == GLFW.glfwGetPrimaryMonitor());
    }

    /**
     * Toggle window fullscreen
     */
    public static void toggleFullScreen() {
        if (isFullScreen()) {
            disableFullScreen();
        } else {
            enableFullScreen();
        }
    }

    /**
     * Sets the title of our window
     * @param title
     */
    public static void setTitle(String title) {
        GLFW.glfwSetWindowTitle(window, title);
    }

    /**
     * Sets the window size
     */
    public static void setSize(int width, int height) {
        Window.width = width;
        Window.height = height;
        GLFW.glfwSetWindowSize(window, width, height);
    }

    /**
     * @return The input handler we've made for the window
     */
    public static Input getInput() {
        return input;
    }

//    public static int getKey(int key) {
//        return GLFW.glfwGetKey(window, key);
//    }

    /**
     * Gets the window width
     */
    public static int getWidth() {
        return width;
    }

    /**
     * Gets the window height
     */
    public static int getHeight() {
        return height;
    }

    /**
     * To be used for our main loop
     * @return Should our window close?
     */
    public static boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

}
