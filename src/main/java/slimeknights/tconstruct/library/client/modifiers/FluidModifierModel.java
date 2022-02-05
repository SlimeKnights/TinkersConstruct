package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.TankModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Model for tank modifiers, also displays the fluid
 */
public class FluidModifierModel extends NormalModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    Material smallTexture = smallGetter.apply("");
    Material largeTexture = largeGetter.apply("");
    Material smallFull = smallGetter.apply("_full");
    Material largeFull = largeGetter.apply("_full");
    if (smallTexture != null || largeTexture != null) {
      return new FluidModifierModel(smallTexture, largeTexture, smallFull, largeFull);
    }
    return null;
  };

  /** Textures to show */
  protected final Material[] fluidTextures;

  protected FluidModifierModel(@Nullable Material smallTexture, @Nullable Material largeTexture, Material[] fluidTextures) {
    super(smallTexture, largeTexture);
    this.fluidTextures = fluidTextures;
  }

  public FluidModifierModel(@Nullable Material smallTexture, @Nullable Material largeTexture,
														@Nullable Material smallFull, @Nullable Material largeFull) {
    this(smallTexture, largeTexture, new Material[] { smallFull, largeFull });
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry entry) {
    if (entry.getModifier() instanceof TankModifier tank) {
      FluidStack fluid = tank.getFluid(tool);
      if (!fluid.isEmpty()) {
        // cache by modifier and fluid
        return new FluidModifierCacheKey(tank, fluid.getFluid());
      }
    }
    return entry.getModifier();
  }

  @Nullable
  protected Material getTemplate(TankModifier tank, IToolStackView tool, FluidStack fluid, boolean isLarge) {
    return fluidTextures[(isLarge ? 1 : 0)];
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IToolStackView tool, ModifierEntry entry, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, @Nullable ItemLayerPixels pixels) {
    // first, determine stored fluid
    ImmutableList<BakedQuad> quads = super.getQuads(tool, entry, spriteGetter, transforms, isLarge, startTintIndex, pixels);
    // modifier must be tank
    // TODO: is there anything that can be done about the fluid? to prevent weird offsets?
    if (entry.getModifier() instanceof TankModifier tank) {
      FluidStack fluid = tank.getFluid(tool);
      // must have fluid
      if (!fluid.isEmpty()) {
        // must have texture for the proper state
        Material template = getTemplate(tank, tool, fluid, isLarge);
        if (template != null) {
          // finally, build (mostly based on bucket model)
          ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
          builder.addAll(quads);
          FluidAttributes attributes = fluid.getFluid().getAttributes();
          TextureAtlasSprite fluidSprite = spriteGetter.apply(ForgeHooksClient.getBlockMaterial(attributes.getStillTexture(fluid)));
          int color = attributes.getColor(fluid);
          int luminosity = attributes.getLuminosity(fluid);
          TextureAtlasSprite templateSprite = spriteGetter.apply(template);
          builder.addAll(ItemTextureQuadConverter.convertTexture(transforms, templateSprite, fluidSprite, 7.498f / 16f, Direction.NORTH, color, -1, luminosity));
          builder.addAll(ItemTextureQuadConverter.convertTexture(transforms, templateSprite, fluidSprite, 8.502f / 16f, Direction.SOUTH, color, -1, luminosity));
          quads = builder.build();
        }
      }
    }
    return quads;
  }

  /** Cache key for the model */
  private record FluidModifierCacheKey(Modifier modifier, Fluid fluid) {}
}
