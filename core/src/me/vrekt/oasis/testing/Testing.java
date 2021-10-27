package me.vrekt.oasis.testing;

import com.badlogic.gdx.math.Vector2;

public class Testing {

    // Vector2 newCoordinates = MovementUtilities.getIncremented(x, y);
    //    cam.position.set(newCoordinates.x, newCoordinates.y, 0);
    //    batch.draw(texture, cam.position.x-15, cam.position.y, width, height);

    // initialize with your beginning position if you'd like.
    private float x, y;
    // initialize with your beginning position if you'd like.
    private final Vector2 coordinates = new Vector2(x, y);

    private final float moveSpeed = 5.0f;

    public void function() {

    }

    public void getNewPosition() {
        coordinates.set(x - moveSpeed, y - moveSpeed);
    }

}
