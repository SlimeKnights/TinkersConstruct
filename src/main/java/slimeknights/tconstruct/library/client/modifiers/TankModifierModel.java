package slimeknights.tconstruct.library.client.modifiers;

import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.ToolFluidCapability;
import slimeknights.tconstruct.library.tools.capability.ToolFluidCapability.FluidModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Model for tank modifiers, also displays the fluid
 */
public class TankModifierModel extends FluidModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    Material smallTexture = smallGetter.apply("");
    Material largeTexture = largeGetter.apply("");
    Material smallPartial = smallGetter.apply("_partial");
    Material largePartial = largeGetter.apply("_partial");
    Material smallFull = smallGetter.apply("_full");
    Material largeFull = largeGetter.apply("_full");
    if (smallTexture != null || largeTexture != null) {
      return new TankModifierModel(smallTexture, largeTexture, smallPartial, largePartial, smallFull, largeFull);
    }
    return null;
  };

  public TankModifierModel(@Nullable Material smallTexture, @Nullable Material largeTexture,
                           @Nullable Material smallPartial, @Nullable Material largePartial,
                           @Nullable Material smallFull, @Nullable Material largeFull) {
    super(smallTexture, largeTexture, new Material[] { smallPartial, largePartial, smallFull, largeFull });
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry entry) {
    FluidModifierHook tank = entry.getHook(ToolFluidCapability.HOOK);
    FluidStack fluid = tank.getFluidInTank(tool, entry, 0);
    if (!fluid.isEmpty()) {
      // cache by modifier, fluid, and not being full
      return new TankModifierCacheKey(entry.getModifier(), fluid.getFluid(), fluid.getAmount() < tank.getTankCapacity(tool, entry, 0));
    }
    return entry.getModifier();
  }

  @Override
  @Nullable
  protected Material getTemplate(FluidModifierHook tank, IToolStackView tool, ModifierEntry entry, FluidStack fluid, boolean isLarge) {
    boolean isFull = fluid.getAmount() == tank.getTankCapacity(tool, entry, 0);
    return fluidTextures[(isFull ? 2 : 0) | (isLarge ? 1 : 0)];
  }

  /**
   * Cache key for the model
   */
  private record TankModifierCacheKey(Modifier modifier, Fluid fluid, boolean isPartial) {}
}
