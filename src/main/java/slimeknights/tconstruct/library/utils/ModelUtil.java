package slimeknights.tconstruct.library.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.SpriteIdentifier;

import com.mojang.datafixers.util.Either;

public class ModelUtil {

	/**
	 * Checks if a texture is present in the model.
	 * @param name The name of a texture channel.
	 */
	public static boolean isTexturePresent(JsonUnbakedModel model, String name) {
		return !MissingSprite.getMissingSpriteId().equals(model.getMaterial(p_178300_1_).texture());
	}

	/**
	 * Resolves the final texture name, taking into account texture aliases and replacements.
	 * @param name The name of a texture channel.
	 * @return The location of the texture, or the missing texture if not found.
	 */
	public static SpriteIdentifier resolveTexture(JsonUnbakedModel model, String name) {

	}

	public SpriteIdentifier getMaterial(JsonUnbakedModel model, String name) {
		if (isTextureReference(name)) {
			name = name.substring(1);
		}

		List<String> list = Lists.newArrayList();

		while(true) {
			Either<SpriteIdentifier, String> either = model.r().findTextureEntry(name);
			Optional<RenderMaterial> optional = either.left();
			if (optional.isPresent()) {
				return optional.get();
			}

			name = either.right().get();
			if (list.contains(name)) {
				LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", Joiner.on("->").join(list), name, this.name);
				return new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, MissingTextureSprite.getLocation());
			}

			list.add(name);
		}
	}

	private static boolean isTextureReference(String p_178304_0_) {
		return p_178304_0_.charAt(0) == '#';
	}

}
