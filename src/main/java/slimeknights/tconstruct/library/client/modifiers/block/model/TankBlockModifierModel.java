package slimeknights.tconstruct.library.client.modifiers.block.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.model.block.IncrementalFluidCuboid;
import slimeknights.tconstruct.library.client.modifiers.block.ModifierBakingContext;
import slimeknights.tconstruct.library.client.modifiers.block.ModifierBakingContext.TextureResolver;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

/**
 * Model for a tank showing its fluid on the block, using an incremental fluid
 * cuboid.
 */
public class TankBlockModifierModel implements BlockModifierModel, ModifierBakingContext.MaterialSupplier {
  public static final RecordLoadable<TankBlockModifierModel> LOADER = RecordLoadable.create(
      FluidCuboid.LOADABLE.requiredField("fluid", m -> m.fluid),
      IntLoadable.FROM_ONE.defaultField("increments", 1, false, m -> m.fluid.getIncrements()),
      ToolTankHelper.LOADABLE.defaultField("tank_helper", ToolTankHelper.TANK_HELPER, false, m -> m.tankHelper),
      TankBlockModifierModel::new);

  /** The fluid cuboid */
  private final IncrementalFluidCuboid fluid;
  /** The tank helper */
  private final ToolTankHelper tankHelper;

  /**
   * @param fluid      The fluid cuboid
   * @param increments The number of increments to use
   * @param tankHelper The tank helper to use
   */
  public TankBlockModifierModel(FluidCuboid fluid, int increments, ToolTankHelper tankHelper) {
    this.fluid = new IncrementalFluidCuboid(fluid.getFrom(), fluid.getTo(), fluid.getFaces(), increments);
    this.tankHelper = tankHelper;
  }

  /** Cache key for {@link #getCacheKey(IToolStackView, ModifierEntry)} */
  private record CacheKey(Fluid fluid, @Nullable CompoundTag tag, int fillLevel) {
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    FluidStack fluidStack = tankHelper.getFluid(tool);
    if (fluidStack.isEmpty())
      return null;
    int increments = fluid.getIncrements();
    int capacity = tankHelper.getCapacity(tool);
    int amount = Mth.clamp(fluidStack.getAmount() * increments / Math.max(capacity, 1), 1, increments);
    return new CacheKey(fluidStack.getFluid(), fluidStack.getOrCreateTag(), amount);
  }

  @Override
  public RecordLoadable<? extends BlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate() {
  }

  @Override
  public void addParts(IToolStackView tool, ModifierEntry modifier, ModifierBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, IQuadTransformer quadTransformer, Builder builder) {
    FluidStack stack = tankHelper.getFluid(tool);
    // empty tank, nothing to render
    if (stack.isEmpty()) {
      return;
    }
    IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(stack.getFluid());
    FluidType type = stack.getFluid().getFluidType();
    int color = attributes.getTintColor(stack);
    int luminosity = type.getLightLevel(stack);
    int increments = fluid.getIncrements();
    int capacity = tankHelper.getCapacity(tool);
    int amount = Mth.clamp(stack.getAmount() * increments / Math.max(capacity, 1), 1, increments);
    BlockElement fluid = this.fluid.getPart(amount, type.isLighterThanAir());
    IQuadTransformer fluidTransformer = color == -1 ? quadTransformer : quadTransformer.andThen(ColoredBlockModel.applyColorQuadTransformer(color));
    ColoredBlockModel.bakePart(builder, context.with(tool, modifier, this).getResolver(tool, modifier), fluid, luminosity, spriteGetter, context.transform.getRotation(), fluidTransformer, context.transform.isUvLocked(), context.location);
  }

  @Override
  public boolean hasMaterial(IToolStackView tool, ModifierEntry modifier, String name, TextureResolver resolver) {
    return !tankHelper.getFluid(tool).isEmpty() && ("fluid".equals(name) || "flowing_fluid".equals(name));
  }

  @Override
  public Material getMaterial(IToolStackView tool, ModifierEntry modifier, String name, TextureResolver resolver) {
    FluidStack stack = tankHelper.getFluid(tool);
    if (stack.isEmpty()) {
      return TexturedBlockModifierModel.MISSING;
    }
    IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(stack.getFluid());
    Map<String, Material> textures = ImmutableMap.of(
        "fluid", new Material(InventoryMenu.BLOCK_ATLAS, attributes.getStillTexture(stack)),
        "flowing_fluid", new Material(InventoryMenu.BLOCK_ATLAS, attributes.getFlowingTexture(stack)));
    return textures.getOrDefault(name, TexturedBlockModifierModel.MISSING);
  }
}
