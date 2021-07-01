package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TankModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Model for tank modifiers, also displays the fluid
 */
public class FluidModifierModel extends NormalModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    RenderMaterial smallTexture = smallGetter.apply("");
    RenderMaterial largeTexture = largeGetter.apply("");
    RenderMaterial smallFull = smallGetter.apply("_full");
    RenderMaterial largeFull = largeGetter.apply("_full");
    if (smallTexture != null || largeTexture != null) {
      return new FluidModifierModel(smallTexture, largeTexture, smallFull, largeFull);
    }
    return null;
  };

  /** Textures to show */
  protected final RenderMaterial[] fluidTextures;

  protected FluidModifierModel(@Nullable RenderMaterial smallTexture, @Nullable RenderMaterial largeTexture, RenderMaterial[] fluidTextures) {
    super(smallTexture, largeTexture);
    this.fluidTextures = fluidTextures;
  }

  public FluidModifierModel(@Nullable RenderMaterial smallTexture, @Nullable RenderMaterial largeTexture,
														@Nullable RenderMaterial smallFull, @Nullable RenderMaterial largeFull) {
    this(smallTexture, largeTexture, new RenderMaterial[] { smallFull, largeFull });
  }

  @Nullable
  @Override
  public Object getCacheKey(IModifierToolStack tool, ModifierEntry entry) {
    if (entry.getModifier() instanceof TankModifier) {
      TankModifier tank = (TankModifier) entry.getModifier();
      FluidStack fluid = tank.getFluid(tool);
      if (!fluid.isEmpty()) {
        // cache by modifier and fluid
        return new FluidModifierCacheKey(tank, fluid.getFluid());
      }
    }
    return entry.getModifier();
  }

  @Nullable
  protected RenderMaterial getTemplate(TankModifier tank, IModifierToolStack tool, FluidStack fluid, boolean isLarge) {
    return fluidTextures[(isLarge ? 1 : 0)];
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry entry, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge) {
    // first, determine stored fluid
    ImmutableList<BakedQuad> quads = super.getQuads(tool, entry, spriteGetter, transforms, isLarge);
    // modifier must be tank
    if (entry.getModifier() instanceof TankModifier) {
      TankModifier tank = (TankModifier) entry.getModifier();
      FluidStack fluid = tank.getFluid(tool);
      // must have fluid
      if (!fluid.isEmpty()) {
        // must have texture for the proper state
        RenderMaterial template = getTemplate(tank, tool, fluid, isLarge);
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
  @Data
  private static class FluidModifierCacheKey {
    private final Modifier modifier;
    private final Fluid fluid;
  }
}
