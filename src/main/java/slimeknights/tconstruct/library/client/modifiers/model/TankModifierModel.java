package slimeknights.tconstruct.library.client.modifiers.model;

import com.mojang.math.Transformation;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/** Model for a tank showing its fluid on the tool */
@RequiredArgsConstructor
public class TankModifierModel implements ModifierModel {
  public static final RecordLoadable<TankModifierModel> LOADER = RecordLoadable.create(
    ModifierModel.MATERIAL_LOADABLE.nullableField("partial", m -> m.smallPartial),
    ModifierModel.MATERIAL_LOADABLE.nullableField("full", m -> m.smallFull),
    ModifierModel.MATERIAL_LOADABLE.nullableField("partial_large", m -> m.largePartial),
    ModifierModel.MATERIAL_LOADABLE.nullableField("full_large", m -> m.largeFull),
    IntLoadable.FROM_ZERO.defaultField("tolerance", 0, false, m -> m.tolerance),
    TankModifierModel::new);

  @Nullable
  private final Material smallPartial;
  @Nullable
  private final Material smallFull;
  @Nullable
  private final Material largePartial;
  @Nullable
  private final Material largeFull;
  private final int tolerance;

  @Override
  public RecordLoadable<? extends TankModifierModel> getLoader() {
    return LOADER;
  }

  protected ToolTankHelper tankHelper() {
    return ToolTankHelper.TANK_HELPER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    if (smallPartial != null) spriteGetter.apply(smallPartial);
    if (largePartial != null) spriteGetter.apply(largePartial);
    if (smallFull != null) spriteGetter.apply(smallFull);
    if (largeFull != null) spriteGetter.apply(largeFull);
  }

  private record CacheKey(Fluid fluid, @Nullable CompoundTag tag, boolean partial) {}

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    ToolTankHelper helper = tankHelper();
    FluidStack fluid = helper.getFluid(tool);
    if (!fluid.isEmpty()) {
      return new CacheKey(fluid.getFluid(), fluid.getTag(), fluid.getAmount() + tolerance < helper.getCapacity(tool));
    }
    return null;
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    Material partial, full;
    if (isLarge) {
      partial = largePartial;
      full = largeFull;
    } else {
      partial = smallPartial;
      full = smallFull;
    }
    if (partial != null || full != null) {
      ToolTankHelper helper = tankHelper();
      FluidStack fluid = helper.getFluid(tool);
      if (!fluid.isEmpty()) {
        Material mask = fluid.getAmount() + tolerance < helper.getCapacity(tool) ? partial : full;
        if (mask != null) {
          FluidModifierModel.addQuads(fluid, mask, spriteGetter, transforms, quadConsumer);
        }
      }
    }
  }
}
