package slimeknights.tconstruct.library.tools.part.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;

/** Block for {@link MaterialBlockItem} or {@link ToolPartBlockItem}. Uses {@link MaterialBlockEntity} */
public class MaterialBlock extends Block implements EntityBlock {
  /** Block entity constructor. Used since addons cannot add new blocks to our block entity. */
  private final BlockEntitySupplier<? extends MaterialBlockEntity> blockEntity;
  public MaterialBlock(Properties pProperties, BlockEntitySupplier<? extends MaterialBlockEntity> blockEntity) {
    super(pProperties);
    this.blockEntity = blockEntity;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return blockEntity.create(pos, state);
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    if (stack.hasTag()) {
      MaterialVariantId material = IMaterialItem.getMaterialFromStack(stack);
      if (material != IMaterial.UNKNOWN_ID && level.getBlockEntity(pos) instanceof MaterialBlockEntity be) {
        be.setMaterial(material);
      }
    }
  }

  @Override
  public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
    ItemStack stack = new ItemStack(state.getBlock());
    if (level.getBlockEntity(pos) instanceof MaterialBlockEntity be) {
      stack = IMaterialItem.withMaterial(stack, be.getMaterial());
    }
    return stack;
  }
}
