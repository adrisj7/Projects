package engine.core;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import engine.rendering.Camera;
import engine.rendering.Shader;
import game.entity.Player;
import world.World;

public class Main {

    private static Main myInstance;
    private int counter;

    private Camera camera;

    private int framerateTarget;
    private int fpsRecorded; // Recorded number of frames per second
    private float fpsRecordedReal; // Real fps, recorded every frame

    private Shader shader;

    // TODO: Delete testing code
    World world;
    Player player;

    public Main() {
        Main.myInstance = this;
    }

    /**
     * Starts the entire game
     */
    public void start() {
        this.init();
        this.loop();
        this.destroy();
    }

    /**
     * Central LWJGL Init called from run().
     */
    private void init() {

        System.out.println("Initializing LWJGL Version " + Version.getVersion() + "!");

        // Set up error printing. We want it to print through System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        Window.initWindow("LWJGL Test", 640, 480);

        // Create GL Capabilities, enable Texture2D's, and enable alpha
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Set the clear color
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        framerateTarget = 60;

        camera = new Camera(Window.getWidth() / 2, Window.getHeight() / 2);
        shader = new Shader("shaders/default");

        world = new World();
        world.generate();

        player = new Player();
    }

    /**
     * Central LWJGL Loop
     */
    private void loop() {

        // Never reset
        counter = 0;

        // Keep track of our FPS
        int frameCount = 0;
        long frameCountTime = 0;

        long frameCap = 1000000000L / framerateTarget;
        long timePrev = System.nanoTime();
        long unprocessed = 0;

        while (!Window.shouldClose()) {
            long timeNow = System.nanoTime();
            long delta = timeNow - timePrev;
            timePrev = timeNow;
            unprocessed    += delta;

            frameCountTime += delta;

            boolean shouldRender = false;
            while (unprocessed >= frameCap) {
                fpsRecordedReal = 1000000000L / (delta);
                double deltaSeconds = (double)frameCap / 1000000000.0;

                this.tick(deltaSeconds);

                shouldRender = true;
                unprocessed -= frameCap;
                counter++;
            }

            if (shouldRender) {
                frameCount++;
                this.render();
            }

            // Every second, poll FPS
            if (frameCountTime >= 1000000000L) {
                this.fpsRecorded = frameCount;
                frameCountTime = 0;
                frameCount = 0;
            }

        }
    }

    /**
     * Central tick method
     * @param delta: How many seconds have passed since the last tick
     */
    private void tick(double delta) {
        Window.pollEvents();

        Window.setTitle("LWJGL Test. FPS: " + getFPS());

        // Escape closes the window
        if (Window.getInput().keyCheckPressed(GLFW.GLFW_KEY_ESCAPE)) {
            quit();
        }

        // R restarts
        if (Window.getInput().keyCheckPressed(GLFW.GLFW_KEY_R)) {
            restart();
        }

        // Alt+Enter Fullscreen
        if ((Window.getInput().keyCheck(GLFW.GLFW_KEY_LEFT_ALT) || Window.getInput().keyCheck(GLFW.GLFW_KEY_RIGHT_ALT)) && Window.getInput().keyCheckPressed(GLFW.GLFW_KEY_ENTER)) {
            Window.toggleFullScreen();
        }


        // TODO: Delete/Handle better
        player.update((float)delta);
    }

    /**
     * Central render method
     */
    private void render() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the frame buffer

        // TODO: Delete/Handle better
        world.render(shader);
        player.render(shader);

        Window.swapBuffers();
    }

    /**
     * Central quit function. <br>
     * <br>
     * Call this to quit the game
     */
    public void quit() {
        Window.destroy();
    }

    /**
     * Restarts the entire game.
     */
    public void restart() {
        quit();
        init();
    }

    /**
     * Central LWJGL Destroy function. This destroys the window and exits the game
     * <br>
     * <br>
     * Do not call this to quit the game: This will destroy glfw but not the
     * program.
     */
    private void destroy() {
        // Terminate everything and free error callback
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    /**
     * Sets our framerate target (ex. give 60 and yield 60fps)
     * @param framerateTarget
     */
    public void setFramerateTarget(int framerateTarget) {
        this.framerateTarget = framerateTarget;
    }

    /**
     * A counter that increments every frame, and starts at 0
     * 
     * @return
     */
    public int getCounter() {
        return counter;
    }

    /**
     * @return How many frames per second recorded in the last second
     */
    public int getFPS() {
        return fpsRecorded;
    }

    /**
     * @return On a frame by frame basis the calculated FPS
     */
    public float getFPSReal() {
        return fpsRecordedReal;
    }

    public Camera getCamera() {
        return camera;
    }

    public Shader getMainShader() {
        return shader;
    }
    
    public World getWorld() {
        return world;
    }

    /**
     * Static accessor for main. static?
     * 
     * @return
     */
    public static Main getInstance() {
        return myInstance;
    }

    /**
     * The OG main
     */
    public static void main(String[] args) {
        new Main().start();
    }
}
