package slimeknights.tconstruct.library.client.modifiers;

import lombok.Data;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TankModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

/**
 * Model for tank modifiers, also displays the fluid
 */
public class TankModifierModel extends FluidModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    RenderMaterial smallTexture = smallGetter.apply("");
    RenderMaterial largeTexture = largeGetter.apply("");
    RenderMaterial smallPartial = smallGetter.apply("_partial");
    RenderMaterial largePartial = largeGetter.apply("_partial");
    RenderMaterial smallFull = smallGetter.apply("_full");
    RenderMaterial largeFull = largeGetter.apply("_full");
    if (smallTexture != null || largeTexture != null) {
      return new TankModifierModel(smallTexture, largeTexture, smallPartial, largePartial, smallFull, largeFull);
    }
    return null;
  };

  public TankModifierModel(@Nullable RenderMaterial smallTexture, @Nullable RenderMaterial largeTexture,
                           @Nullable RenderMaterial smallPartial, @Nullable RenderMaterial largePartial,
                           @Nullable RenderMaterial smallFull, @Nullable RenderMaterial largeFull) {
    super(smallTexture, largeTexture, new RenderMaterial[] { smallPartial, largePartial, smallFull, largeFull });
  }

  @Nullable
  @Override
  public Object getCacheKey(IModifierToolStack tool, ModifierEntry entry) {
    if (entry.getModifier() instanceof TankModifier) {
      TankModifier tank = (TankModifier) entry.getModifier();
      FluidStack fluid = tank.getFluid(tool);
      if (!fluid.isEmpty()) {
        // cache by modifier, fluid, and not being full
        return new TankModifierCacheKey(tank, fluid.getFluid(), fluid.getAmount() < tank.getCapacity(tool));
      }
    }
    return entry.getModifier();
  }

  @Override
  @Nullable
  protected RenderMaterial getTemplate(TankModifier tank, IModifierToolStack tool, FluidStack fluid, boolean isLarge) {
    boolean isFull = fluid.getAmount() == tank.getCapacity(tool);
    return fluidTextures[(isFull ? 2 : 0) | (isLarge ? 1 : 0)];
  }

  /** Cache key for the model */
  @Data
  private static class TankModifierCacheKey {
    private final Modifier modifier;
    private final Fluid fluid;
    private final boolean isPartial;
  }
}
