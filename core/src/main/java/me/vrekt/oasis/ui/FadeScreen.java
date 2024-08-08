package me.vrekt.oasis.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.world.GameWorld;

/**
 * Helper class for fading between two locations
 * <a href="https://gamedev.stackexchange.com/questions/200786/create-a-transition-fade-out-in-libgdx">...</a>
 */
public final class FadeScreen extends ScreenAdapter {

    private final GameWorld primary;
    private final Screen secondaryFader;
    private final ShapeRenderer shapeRenderer;
    private final Camera camera;
    private final Runnable completed;
    private float elapsed;
    private final boolean fadeIn;

    public FadeScreen(GameWorld primary, Screen next, Runnable completed, boolean fadeIn) {
        this.primary = primary;
        this.completed = completed;
        this.secondaryFader = next;
        this.fadeIn = fadeIn;

        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2.0f, camera.viewportHeight / 2.0f, 0.0f);
        camera.update();
    }

    public FadeScreen(GameWorld primary, boolean fadeIn) {
        this(primary, null, null, fadeIn);
    }

    private void renderFade() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        float f = Math.min(1.0f, elapsed / 1.1f);
        float opacity = fadeIn ? 1.0f - Interpolation.smoother.apply(f) : Interpolation.smoother.apply(f);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, opacity);
        shapeRenderer.rect(0, 0, camera.viewportWidth, camera.viewportHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void render(float delta) {
        if (primary != null) {
            elapsed += delta;
            if (elapsed >= 1.1f) {
                if (secondaryFader != null) {
                    GameManager.game().setScreen(secondaryFader);
                    if (completed != null) Gdx.app.postRunnable(completed);
                } else {
                    GameManager.game().worldManager().setActiveWorld(primary);
                    GameManager.game().resetScreen();
                }
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 0);
        if (primary != null)
            primary.render(delta);
        renderFade();
    }
}
