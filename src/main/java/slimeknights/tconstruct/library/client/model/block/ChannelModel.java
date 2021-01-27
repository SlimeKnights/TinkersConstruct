package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Similar to {@link slimeknights.mantle.client.model.fluid.FluidsModel}, but arranges cuboids in the channel.
 * Used since there is no easy way to handle multipart in the fluid cuboid system.
 */
public class ChannelModel implements IModelGeometry<ChannelModel> {
	/** Model loader instance */
	public static final Loader LOADER = new Loader();

	/** Base block model */
	private final SimpleBlockModel model;
	/** Map of all fluid parts of the model */
	private final Map<ChannelModelPart,FluidCuboid> fluids;

	public ChannelModel(SimpleBlockModel model, Map<ChannelModelPart,FluidCuboid> fluids) {
		this.model = model;
		this.fluids = fluids;
	}

	@Override
	public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
		return model.getTextures(owner, modelGetter, missingTextureErrors);
	}

	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation location) {
		IBakedModel baked = this.model.bakeModel(owner, transform, overrides, spriteGetter, location);
		return new BakedModel(baked, this.fluids);
	}

	/**
	 * Baked model wrapper for cistern models
	 */
	public static class BakedModel extends BakedModelWrapper<IBakedModel> {
		private final Map<ChannelModelPart,FluidCuboid> fluids;
		private BakedModel(IBakedModel originalModel, Map<ChannelModelPart,FluidCuboid> fluids) {
			super(originalModel);
			this.fluids = fluids;
		}

		/** Gets the cuboid for flowing down */
		public FluidCuboid getDownFluid() {
			return this.fluids.get(ChannelModelPart.DOWN);
		}

		/**
		 * Gets the cuboid for the center
		 * @param flowing  If true, the center is flowing
		 */
		public FluidCuboid getCenterFluid(boolean flowing) {
			return this.fluids.get(flowing ? ChannelModelPart.CENTER_FLOWING : ChannelModelPart.CENTER_STILL);
		}

		/**
		 * Gets the flowing fluid for the side
		 * @return  Cuboid for center
		 */
		public FluidCuboid getSideFlow(boolean out) {
			return this.fluids.get(out ? ChannelModelPart.SIDE_OUT : ChannelModelPart.SIDE_IN);
		}

		/** Gets the cuboid for still side */
		public FluidCuboid getSideStill() {
			return this.fluids.get(ChannelModelPart.SIDE_STILL);
		}

		/** Gets the cuboid for side edge */
		public FluidCuboid getSideEdge() {
			return this.fluids.get(ChannelModelPart.SIDE_EDGE);
		}
	}

	/** Model loader */
	private static class Loader implements IModelLoader<ChannelModel> {
		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {}

		@Override
		public ChannelModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			SimpleBlockModel model = SimpleBlockModel.deserialize(deserializationContext, modelContents);

			// parse fluid cuboid for each side
			JsonObject fluidJson = JSONUtils.getJsonObject(modelContents, "fluids");
			Map<ChannelModelPart,FluidCuboid> fluids = new EnumMap<>(ChannelModelPart.class);
			fluids.put(ChannelModelPart.DOWN, FluidCuboid.fromJson(JSONUtils.getJsonObject(fluidJson, "down")));
			// center
			JsonObject centerJson = JSONUtils.getJsonObject(fluidJson, "center");
			fluids.put(ChannelModelPart.CENTER_STILL, FluidCuboid.fromJson(JSONUtils.getJsonObject(centerJson, "still")));
			fluids.put(ChannelModelPart.CENTER_FLOWING, FluidCuboid.fromJson(JSONUtils.getJsonObject(centerJson, "flowing")));
			// side
			JsonObject sideJson = JSONUtils.getJsonObject(fluidJson, "side");
			fluids.put(ChannelModelPart.SIDE_STILL, FluidCuboid.fromJson(JSONUtils.getJsonObject(sideJson, "still")));
			fluids.put(ChannelModelPart.SIDE_IN, FluidCuboid.fromJson(JSONUtils.getJsonObject(sideJson, "in")));
			fluids.put(ChannelModelPart.SIDE_OUT, FluidCuboid.fromJson(JSONUtils.getJsonObject(sideJson, "out")));
			fluids.put(ChannelModelPart.SIDE_EDGE, FluidCuboid.fromJson(JSONUtils.getJsonObject(sideJson, "edge")));

			return new ChannelModel(model, fluids);
		}
	}

	/** Enum to hold each of the 7 relevant cuboids */
	private enum ChannelModelPart {
		CENTER_STILL,
		CENTER_FLOWING,
		SIDE_STILL,
		SIDE_IN,
		SIDE_OUT,
		SIDE_EDGE,
		DOWN;
	}
}
