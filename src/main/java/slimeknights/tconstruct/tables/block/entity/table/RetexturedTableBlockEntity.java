package slimeknights.tconstruct.tables.block.entity.table;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.util.Lazy;
import slimeknights.mantle.block.entity.IRetexturedBlockEntity;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.shared.block.entity.TableBlockEntity;

import javax.annotation.Nonnull;

public abstract class RetexturedTableBlockEntity extends TableBlockEntity implements IRetexturedBlockEntity {
  private final Lazy<IModelData> data = Lazy.of(this::getRetexturedModelData);
  public RetexturedTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Component name, int size) {
    super(type, pos, state, name, size);
  }

  @Nonnull
  @Override
  public IModelData getModelData() {
    return this.data.get();
  }

  @Override
  public AABB getRenderBoundingBox() {
    return new AABB(worldPosition, worldPosition.offset(1, 2, 1));
  }

  @Override
  public void load(CompoundTag tags) {
    String oldName = getTextureName();
    super.load(tags);
    String newName = getTextureName();
    // if the texture name changed, mark the position for rerender
    if (!oldName.equals(newName) && level != null && level.isClientSide) {
      data.get().setData(RetexturedHelper.BLOCK_PROPERTY, getTexture());
      requestModelDataUpdate();
      BlockState state = getBlockState();
      level.sendBlockUpdated(worldPosition, state, state, 0);
    }
  }
}
