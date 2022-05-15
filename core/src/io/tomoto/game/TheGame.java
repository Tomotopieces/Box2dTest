package io.tomoto.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import io.tomoto.game.utils.TileMapUtil;

import static io.tomoto.game.utils.Constants.CAMERA_SCALE;
import static io.tomoto.game.utils.Constants.PIXELS_PER_METER;

public class TheGame extends ApplicationAdapter {

    /* basic components */
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap map;

    /* box2d components */
    private Box2DDebugRenderer debugBoxRenderer;
    private World world;

    /* game objects */
    private Body player;

    private Texture playerTexture;
    private Texture back;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        spriteBatch = new SpriteBatch();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        world = new World(new Vector2(0, -9.8f), true);
        debugBoxRenderer = new Box2DDebugRenderer();

        map = new TmxMapLoader().load("maps/map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        TileMapUtil.parseTileMap(world, map.getLayers().get("collision").getObjects());

        player = createBox(96, 96, 32, 32, false);

        playerTexture = new Texture("images/player.png");
        back = new Texture("images/back.png");
    }

    private Body createBox(float x, float y, float width, float height, boolean isStatic) {
        // body definition
        BodyDef def = new BodyDef();
        def.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        def.position.set(x / PIXELS_PER_METER, y / PIXELS_PER_METER);
        def.fixedRotation = true;

        // shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PIXELS_PER_METER, height / 2 / PIXELS_PER_METER);

        // create body with definition and shape
        Body box = world.createBody(def);
        box.createFixture(shape, 1);

        shape.dispose();
        return box;
    }

    @Override
    public void render() {
        // update data
        update(Gdx.graphics.getDeltaTime());

        // render
        ScreenUtils.clear(Color.WHITE);
        spriteBatch.begin();
        spriteBatch.draw(back,
                camera.position.x - camera.viewportWidth / 2 , camera.position.y - camera.viewportHeight / 2,
                camera.viewportWidth, camera.viewportHeight);
        spriteBatch.end();

//        debugBoxRenderer.render(world, camera.combined.scl(PIXELS_PER_METER));
        mapRenderer.render();

        spriteBatch.begin();
        spriteBatch.draw(playerTexture, player.getPosition().x * PIXELS_PER_METER - playerTexture.getWidth() / 2,
                player.getPosition().y * PIXELS_PER_METER - playerTexture.getHeight() / 2);
        spriteBatch.end();
    }

    private void update(float delta) {
        world.step(1 / 60f, 6, 2);
        inputUpdate(delta);
        cameraUpdate(delta);
        mapRenderer.setView(camera);
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    private void inputUpdate(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
//            player.setLinearVelocity(new Vector2(0, 3));
            player.applyForceToCenter(0, 300, false);
        }

        int horizontalForce = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontalForce -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            horizontalForce += 1;
        }
        player.setLinearVelocity(horizontalForce * 3, player.getLinearVelocity().y);
    }

    private void cameraUpdate(float delta) {
        Vector3 position = camera.position;
        position.x = camera.position.x + (player.getPosition().x * PIXELS_PER_METER - camera.position.x) * .1f;
        position.y = camera.position.y + (player.getPosition().y * PIXELS_PER_METER - camera.position.y) * .1f;
        camera.position.set(position);

        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / CAMERA_SCALE, height / CAMERA_SCALE);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        world.dispose();
        debugBoxRenderer.dispose();
        mapRenderer.dispose();
        map.dispose();
    }
}
