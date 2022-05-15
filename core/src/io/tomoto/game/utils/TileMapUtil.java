package io.tomoto.game.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static io.tomoto.game.utils.Constants.PIXELS_PER_METER;

/**
 * Tile map util
 *
 * @author Tomoto
 * @version 1.0
 * @since 1.0 2022/5/15 0:22
 */
public class TileMapUtil {
    /**
     * parse tile map objects to body and add to world.
     *
     * @param world      game world
     * @param mapObjects tile map objects
     */
    public static void parseTileMap(World world, MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof PolylineMapObject) {
                Shape shape = createPolyline((PolylineMapObject) mapObject);
                BodyDef def = new BodyDef();
                def.type = BodyDef.BodyType.StaticBody;
                Body body = world.createBody(def);
                body.createFixture(shape, 1.0f);
                shape.dispose();
                return;
            }
        }
    }

    /**
     * parse polyline vertices to chain shape
     *
     * @param polyline polyline
     * @return chain shape
     */
    private static ChainShape createPolyline(PolylineMapObject polyline) {
        float[] vertices = polyline.getPolyline().getTransformedVertices(); // order like { v1.x, v1.y, v2.x, v2.y, ... }
        Vector2[] realVertices = new Vector2[vertices.length / 2];
        for (int i = 0; i < realVertices.length; i++) {
            realVertices[i] = new Vector2(vertices[i * 2] / PIXELS_PER_METER, vertices[i * 2 + 1] / PIXELS_PER_METER);
        }
        ChainShape shape = new ChainShape();
        shape.createChain(realVertices);
        return shape;
    }
}
