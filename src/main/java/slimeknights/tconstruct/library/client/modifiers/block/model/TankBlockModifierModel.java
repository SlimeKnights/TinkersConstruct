package slimeknights.tconstruct.library.client.modifiers.block.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.model.block.IncrementalFluidCuboid;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/** Model for a tank showing its fluid on the block, using an incremental fluid cuboid. */
public class TankBlockModifierModel implements BlockModifierModel {
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
   * @param fluid The fluid cuboid
   * @param increments The number of increments to use
   * @param tankHelper The tank helper to use
   */
  public TankBlockModifierModel(FluidCuboid fluid, int increments, ToolTankHelper tankHelper) {
    this.fluid = new IncrementalFluidCuboid(fluid.getFrom(), fluid.getTo(), fluid.getFaces(), increments);
    this.tankHelper = tankHelper;
  }

  /** Cache key for {@link #getCacheKey(IToolStackView, ModifierEntry)} */
  private record CacheKey(Fluid fluid, @Nullable CompoundTag tag, int fillLevel) {}

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    FluidStack fluidStack = tankHelper.getFluid(tool);
    if (fluidStack.isEmpty()) return null;
    int increments = fluid.getIncrements();
    int capacity = tankHelper.getCapacity(tool);
    int amount = Mth.clamp(fluidStack.getAmount() * increments / Math.max(capacity, 1), 1, increments);
    return new CacheKey(fluidStack.getFluid(), fluidStack.getTag(), amount);
  }

  @Override
  public RecordLoadable<? extends BlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {}

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    FluidStack fluidStack = tankHelper.getFluid(tool);
    if (fluidStack.isEmpty()) return;

    int increments = fluid.getIncrements();
    int capacity = tankHelper.getCapacity(tool);
    int amount = Mth.clamp(fluidStack.getAmount() * increments / Math.max(capacity, 1), 1, increments);

    IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(fluidStack.getFluid());
    FluidType type = fluidStack.getFluid().getFluidType();
    int color = attributes.getTintColor(fluidStack);
    int luminosity = type.getLightLevel(fluidStack);

    BlockElement fluidPart = fluid.getPart(amount, type.isLighterThanAir());

    Material stillMaterial = new Material(InventoryMenu.BLOCK_ATLAS, attributes.getStillTexture(fluidStack));
    Material flowingMaterial = new Material(InventoryMenu.BLOCK_ATLAS, attributes.getFlowingTexture(fluidStack));

    List<BakedQuad> quads = new ArrayList<>();
    for (Map.Entry<Direction, BlockElementFace> entry : fluidPart.faces.entrySet()) {
      Direction direction = entry.getKey();
      BlockElementFace face = entry.getValue();
      Material mat = "fluid".equals(face.texture) ? stillMaterial : flowingMaterial;
      TextureAtlasSprite sprite = spriteGetter.apply(mat);
      @SuppressWarnings("null") // Note: ResourceLocation only used when ModelState is uv locked. We can also re-implement this method.
      BakedQuad quad = BAKER.bakeQuad(fluidPart.from, fluidPart.to, face, sprite, direction, DEFAULT_MODEL_STATE, fluidPart.rotation, fluidPart.shade, (ResourceLocation) null);
      if (color != -1) {
        quad = ColoredBlockModel.applyColorQuadTransformer(color).process(quad);
      }
      if (luminosity > 0) {
        quad = QuadTransformers.settingEmissivity(luminosity).process(quad);
      }
      quads.add(quad);
    }
    quadConsumer.accept(QuadTransformers.applying(transforms).process(quads));
  }
}
