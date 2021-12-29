/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package slimeknights.tconstruct.smeltery.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import lombok.RequiredArgsConstructor;
import lombok.With;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.CompositeModelState;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemMultiLayerBakedModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.item.CopperCanItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Reimplementation of {@link net.minecraftforge.client.model.DynamicBucketModel} as the forge one does not handle fluid NBT
 */
@RequiredArgsConstructor
public final class CopperCanModel implements IModelGeometry<CopperCanModel> {
  public static final Loader LOADER = new Loader();

  // minimal Z offset to prevent depth-fighting
  private static final float NORTH_Z_COVER = 7.496f / 16f;
  private static final float SOUTH_Z_COVER = 8.504f / 16f;
  private static final float NORTH_Z_FLUID = 7.498f / 16f;
  private static final float SOUTH_Z_FLUID = 8.502f / 16f;

  @Nonnull
  @With
  private final FluidStack fluid;
  private final boolean coverIsMask;
  private final boolean applyFluidLuminosity;

  @Override
  public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    // fetch fluid sprite and cover sprite
    FluidAttributes attributes = fluid.getFluid().getAttributes();
    TextureAtlasSprite fluidSprite = !fluid.isEmpty() ? spriteGetter.apply(ForgeHooksClient.getBlockMaterial(attributes.getStillTexture(fluid))) : null;
    Material baseLocation = owner.isTexturePresent("base") ? owner.resolveTexture("base") : null;
    TextureAtlasSprite coverSprite = ((!coverIsMask || baseLocation != null) && owner.isTexturePresent("cover")) ? spriteGetter.apply(owner.resolveTexture("cover")) : null;

    // particle sprite
    TextureAtlasSprite particleSprite;
    if (owner.isTexturePresent("particle")) {
      particleSprite = spriteGetter.apply(owner.resolveTexture("particle"));
    } else if (fluidSprite != null) {
      particleSprite = fluidSprite;
    } else if (!coverIsMask && coverSprite != null) {
      particleSprite = coverSprite;
    } else {
      particleSprite = spriteGetter.apply(ModelLoaderRegistry.blockMaterial(MissingTextureAtlasSprite.getLocation()));
    }

    // setup builder
    ModelState transformsFromModel = owner.getCombinedTransform();
    ImmutableMap<TransformType,Transformation> transformMap = PerspectiveMapWrapper.getTransforms(new CompositeModelState(transformsFromModel, modelTransform));
    ItemMultiLayerBakedModel.Builder builder = ItemMultiLayerBakedModel.builder(owner, particleSprite, new ContainedFluidOverrideHandler(overrides, bakery, owner, this), transformMap);
    Transformation transform = modelTransform.getRotation();

    // start with the base
    if (baseLocation != null) {
      // build base (insidest)
      builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemLayerModel.getQuadsForSprites(ImmutableList.of(baseLocation), transform, spriteGetter));
    }

    // add in the fluid
    if (fluidSprite != null && owner.isTexturePresent("fluid")) {
      TextureAtlasSprite templateSprite = spriteGetter.apply(owner.resolveTexture("fluid"));
      if (templateSprite != null) {
        // build liquid layer (inside)
        int luminosity = applyFluidLuminosity ? attributes.getLuminosity(fluid) : 0;
        int color = attributes.getColor(fluid);
        builder.addQuads(ItemLayerModel.getLayerRenderType(luminosity > 0), ItemTextureQuadConverter.convertTexture(transform, templateSprite, fluidSprite, NORTH_Z_FLUID, Direction.NORTH, color, -1, luminosity));
        builder.addQuads(ItemLayerModel.getLayerRenderType(luminosity > 0), ItemTextureQuadConverter.convertTexture(transform, templateSprite, fluidSprite, SOUTH_Z_FLUID, Direction.SOUTH, color, -1, luminosity));
      }
    }

    if (coverIsMask) {
      if (coverSprite != null) {
        TextureAtlasSprite baseSprite = spriteGetter.apply(baseLocation);
        builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemTextureQuadConverter.convertTexture(transform, coverSprite, baseSprite, NORTH_Z_COVER, Direction.NORTH, 0xFFFFFFFF, 2));
        builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemTextureQuadConverter.convertTexture(transform, coverSprite, baseSprite, SOUTH_Z_COVER, Direction.SOUTH, 0xFFFFFFFF, 2));
      }
    } else if (coverSprite != null) {
      builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, NORTH_Z_COVER, coverSprite, Direction.NORTH, 0xFFFFFFFF, 2));
      builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, SOUTH_Z_COVER, coverSprite, Direction.SOUTH, 0xFFFFFFFF, 2));
    }

    builder.setParticle(particleSprite);

    return builder.build();
  }

  @Override
  public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation,UnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    Set<Material> texs = Sets.newHashSet();
    if (owner.isTexturePresent("particle")) texs.add(owner.resolveTexture("particle"));
    if (owner.isTexturePresent("base"))     texs.add(owner.resolveTexture("base"));
    if (owner.isTexturePresent("fluid"))    texs.add(owner.resolveTexture("fluid"));
    if (owner.isTexturePresent("cover"))    texs.add(owner.resolveTexture("cover"));
    return texs;
  }

  private static class Loader implements IModelLoader<CopperCanModel> {
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {}

    @Override
    public CopperCanModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      boolean coverIsMask = GsonHelper.getAsBoolean(modelContents, "coverIsMask", true);
      boolean applyFluidLuminosity = GsonHelper.getAsBoolean(modelContents, "applyFluidLuminosity", true);
      return new CopperCanModel(FluidStack.EMPTY, coverIsMask, applyFluidLuminosity);
    }
  }

  private static final class ContainedFluidOverrideHandler extends ItemOverrides {
    private static final ResourceLocation BAKE_LOCATION = TConstruct.getResource("copper_can_dynamic");
    private final Map<FluidStack,BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change
    private final ItemOverrides nested;
    private final ModelBakery bakery;
    private final IModelConfiguration owner;
    private final CopperCanModel parent;

    private ContainedFluidOverrideHandler(ItemOverrides nested, ModelBakery bakery, IModelConfiguration owner, CopperCanModel parent) {
      this.nested = nested;
      this.bakery = bakery;
      this.owner = owner;
      this.parent = parent;
    }

    /** Gets the model directly, for creating the cached models */
    private BakedModel getUncahcedModel(FluidStack fluid) {
      return this.parent.withFluid(fluid).bake(owner, bakery, ForgeModelBakery.defaultTextureGetter(), BlockModelRotation.X0_Y0, ItemOverrides.EMPTY, BAKE_LOCATION);
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
      BakedModel overriden = nested.resolve(originalModel, stack, world, entity, seed);
      if (overriden != originalModel) return overriden;
      Fluid fluid = CopperCanItem.getFluid(stack);
      if (fluid != Fluids.EMPTY) {
        FluidStack fluidStack = new FluidStack(fluid, FluidValues.INGOT, CopperCanItem.getFluidTag(stack));
        return cache.computeIfAbsent(fluidStack, this::getUncahcedModel);
      }
      return originalModel;
    }
  }
}
