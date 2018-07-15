package game.entity;

import org.joml.Vector2f;

import engine.core.Main;
import engine.util.CollisionBox;
import world.Physics;

/** PhysicsEntity.java
 * 
 * An entity that handles basic physics.
 * 
 * Including collisions and gravity
 * 
 * @author adris
 *
 */
public abstract class PhysicsEntity extends Entity {

    protected CollisionBox collisionBox;
    protected Vector2f velocity;

    public PhysicsEntity(float x, float y, CollisionBox collisionBox) {
        transform.position.set(x, y);
        velocity = new Vector2f();
        this.collisionBox = collisionBox; 
    }

    @Override
    public void update(float delta) {
        // Gravity
        velocity.add(Physics.getInstance().getGravity(delta));

        // Collisions
        doCollisions();
    }

    /**
     * Do collisions on this physics entity
     */
    private void doCollisions() {
        Vector2f collisionOffset = new Vector2f();
        Vector2f tileCollisionLocation = new Vector2f();

        // X collision
        if (Main.getInstance().getWorld().checkCollision(collisionOffset.set(transform.position).add(velocity.x, 0), collisionBox, tileCollisionLocation)) {
            if (velocity.x > 0) {
                transform.position.x = tileCollisionLocation.x - collisionBox.size.x - collisionBox.offset.x - 0.01f;
            } else if (velocity.x < 0){
                transform.position.x = tileCollisionLocation.x - collisionBox.offset.x + 1f;
            }
            velocity.x = 0;
        }
        // Move by x
        transform.position.x += velocity.x;

        // Y collision
        if (Main.getInstance().getWorld().checkCollision(collisionOffset.set(transform.position).add(0, velocity.y), collisionBox, tileCollisionLocation)) {
            if (velocity.y > 0) {
                transform.position.y = tileCollisionLocation.y - collisionBox.size.y - collisionBox.offset.y - 0.01f;
            } else if (velocity.y < 0){
                transform.position.y = tileCollisionLocation.y - collisionBox.offset.y + 1f;
            }
            velocity.y = 0;
        }        
        // Move by y
        transform.position.y += velocity.y;

    }

}
