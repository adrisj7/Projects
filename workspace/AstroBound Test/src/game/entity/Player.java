package game.entity;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import engine.core.Main;
import engine.core.Window;
import engine.rendering.Shader;
import engine.rendering.Sprite;
import engine.util.CollisionBox;

public class Player extends PhysicsEntity {

    private Sprite sprite;

    public Player() {
        super(0.0f,1.0f, new CollisionBox(-0.4f,0, 0.8f,2));

//        sprite = new Sprite("res/Sprites/Player/Player.png");
        sprite = new Sprite("res/Sprites/Tilesets/test.png");
        sprite.setCenter(new Vector2f(0.5f, 0f));

    }

    @Override
    public void update(float delta) {
        super.update(delta);

        float moveSpeed = 3.0f * delta;
        float moveX = 0;

        // Camera movement (debug only)
        if (Window.getInput().keyCheck(GLFW.GLFW_KEY_RIGHT)) {
            moveX += moveSpeed;
        }
        if (Window.getInput().keyCheck(GLFW.GLFW_KEY_LEFT)) {
            moveX -= moveSpeed;
        }
        if (Window.getInput().keyCheckPressed(GLFW.GLFW_KEY_UP)) {
            velocity.add(0, 15 * delta);
        }
        
//        if (Window.getInput().keyCheck(GLFW.GLFW_KEY_UP)) {
//            move.add(0, moveSpeed);
//        }
//        if (Window.getInput().keyCheck(GLFW.GLFW_KEY_DOWN)) {
//            move.add(0, -moveSpeed);
//        }

        // Move!
        velocity.add(moveX, 0);

        // Fake friction
        velocity.x *= 0.8;

        // Camera movement
        Vector2f camPos = Main.getInstance().getCamera().getPosition();
        Vector2f deltaPos = new Vector2f();
        deltaPos = transform.position.sub(camPos, deltaPos);
        deltaPos.mul(0.2f);
        Main.getInstance().getCamera().getPosition().add(deltaPos);
    }

    @Override
    public void render(Shader shader) {
        sprite.render(transform);
    }

}
