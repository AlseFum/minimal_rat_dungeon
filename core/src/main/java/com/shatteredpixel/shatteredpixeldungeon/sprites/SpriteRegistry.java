package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

import java.util.HashMap;
import java.util.Map;

/**
 * Named pixel regions from sprite textures.
 *
 * <pre>
 * SpriteRegistry.register("rat.idle", Assets.Sprites.RAT, 0, 0, 16, 15);
 * SpriteRegistry.apply(sprite, "rat.idle");
 * </pre>
 */
public final class SpriteRegistry {

	private static final Map<String, Region> regions = new HashMap<>();

	private SpriteRegistry() {
	}

	/** Registers a unique named rectangular region in a texture asset. */
	public static void register(String id, String texturePath, int left, int top, int width, int height) {
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("Sprite region id must not be empty");
		}
		if (texturePath == null || texturePath.isEmpty()) {
			throw new IllegalArgumentException("Sprite texture path must not be empty");
		}
		if (width <= 0 || height <= 0 || left < 0 || top < 0) {
			throw new IllegalArgumentException("Sprite region must have positive dimensions and a non-negative origin");
		}
		if (regions.containsKey(id)) {
			throw new IllegalArgumentException("Sprite region is already registered: " + id);
		}

		SmartTexture texture = TextureCache.get(texturePath);
		if (width > texture.width - left || height > texture.height - top) {
			throw new IllegalArgumentException("Sprite region is outside texture bounds: " + id);
		}

		regions.put(id, new Region(texturePath, left, top, width, height));
	}
	public static void r(String id, String texturePath, int left, int top, int width, int height){
		register(id, texturePath, left, top, width, height);
	}

	/** Returns whether a region was registered under the supplied id. */
	public static boolean contains(String id) {
		return regions.containsKey(id);
	}

	/** Returns the registered texture for a region. */
	public static SmartTexture texture(String id) {
		return TextureCache.get(region(id).texturePath);
	}

	/** Returns a new UV frame for a region. */
	public static RectF frame(String id) {
		Region region = region(id);
		return texture(id).uvRect(region.left, region.top,
				region.left + region.width, region.top + region.height);
	}

	/** Returns the pixel width of a registered region. */
	public static int width(String id) {
		return region(id).width;
	}

	/** Returns the pixel height of a registered region. */
	public static int height(String id) {
		return region(id).height;
	}

	/** Creates an image already configured to display the registered region. */
	public static Image image(String id) {
		Image image = new Image(texture(id));
		image.frame(frame(id));
		return image;
	}

	/** Applies a registered texture region to an existing image or sprite. */
	public static void apply(Image image, String id) {
		image.texture(texture(id));
		image.frame(frame(id));
	}

	private static Region region(String id) {
		Region region = regions.get(id);
		if (region == null) {
			throw new IllegalArgumentException("Unknown sprite region: " + id);
		}
		return region;
	}

	private static final class Region {
		final String texturePath;
		final int left;
		final int top;
		final int width;
		final int height;

		Region(String texturePath, int left, int top, int width, int height) {
			this.texturePath = texturePath;
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
		}
	}
}
