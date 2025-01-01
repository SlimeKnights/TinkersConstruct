package slimeknights.tconstruct.library.client.modifiers;

import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Model for tank modifiers, also displays the fluid
 */
public class TankModifierModel extends FluidModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = new Unbaked(ToolTankHelper.TANK_HELPER);

  public TankModifierModel(ToolTankHelper helper,
                           @Nullable Material smallTexture, @Nullable Material largeTexture,
                           @Nullable Material smallPartial, @Nullable Material largePartial,
                           @Nullable Material smallFull, @Nullable Material largeFull) {
    super(helper, smallTexture, largeTexture, new Material[] {
      smallPartial, largePartial,
      // if the full texture is missing, sub in partial. Reduces number of textures to stitch
      // practically, I see a usecase for full with no partial, but don't see a usecase for partial with no full
      smallFull != null ? smallFull : smallPartial,
      largeFull != null ? largeFull : largePartial
    });
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry entry) {
    FluidStack fluid = helper.getFluid(tool);
    if (!fluid.isEmpty()) {
      // cache by modifier, fluid, and not being full
      return new TankModifierCacheKey(entry.getModifier(), fluid.getFluid(), fluid.getAmount() < helper.getCapacity(tool));
    }
    return entry != ModifierEntry.EMPTY ? entry.getId() : null;
  }

  @Override
  @Nullable
  protected Material getTemplate(IToolStackView tool, ModifierEntry entry, FluidStack fluid, boolean isLarge) {
    boolean isFull = fluid.getAmount() == helper.getCapacity(tool);
    return fluidTextures[(isFull ? 2 : 0) | (isLarge ? 1 : 0)];
  }

  /**
   * Cache key for the model
   */
  private record TankModifierCacheKey(Modifier modifier, Fluid fluid, boolean isPartial) {}

  public record Unbaked(ToolTankHelper helper) implements IUnbakedModifierModel {
    @Nullable
    @Override
    public IBakedModifierModel forTool(Function<String,Material> smallGetter, Function<String,Material> largeGetter) {
      Material smallTexture = smallGetter.apply("");
      Material largeTexture = largeGetter.apply("");
      Material smallPartial = smallGetter.apply("_partial");
      Material largePartial = largeGetter.apply("_partial");
      Material smallFull = smallGetter.apply("_full");
      Material largeFull = largeGetter.apply("_full");
      if (smallTexture != null || largeTexture != null || smallPartial != null || largePartial != null || smallFull != null || largeFull != null) {
        return new TankModifierModel(helper, smallTexture, largeTexture, smallPartial, largePartial, smallFull, largeFull);
      }
      return null;
    }
  }
}
