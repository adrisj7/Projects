package engine.util;

import org.joml.Vector2f;

public class CollisionBox {

    public Vector2f offset;
    public Vector2f size;

    public CollisionBox(float x, float y, float width, float height) {
        offset = new Vector2f(x,y);
        size = new Vector2f(width, height);
    }

    /**
     * @param position: The position of our collision box
     * @param x
     * @param y
     * @return does (x,y) fit inside of this box?
     */
    public boolean isColliding(Vector2f position, float x, float y) {
        return  (x > offset.x + position.x && x < offset.x + position.x + size.x)
             && (y > offset.y + position.y && y < offset.y + position.y + size.y);
    }

    /**
     * @param box
     * @return does "box" collide with this box?
     */
    public boolean isColling(Vector2f position, CollisionBox box) {
        return isColliding(position, box.offset.x,box.offset.y)
            || isColliding(position, box.offset.x + box.size.x, box.offset.y)
            || isColliding(position, box.offset.x, box.offset.y + box.size.y)
            || isColliding(position, box.offset.x + box.size.x, box.offset.y + box.size.y);
    }
}
