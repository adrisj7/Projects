package game.entity;

import engine.rendering.Shader;
import engine.rendering.Transform;

public abstract class Entity {
    protected Transform transform;

    public Entity() {
        transform = new Transform();
    }
    
    public abstract void update(float delta);
    public abstract void render(Shader shader);
}
