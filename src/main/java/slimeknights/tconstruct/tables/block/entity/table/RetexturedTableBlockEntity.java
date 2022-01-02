package slimeknights.tconstruct.tables.block.entity.table;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.util.Lazy;
import slimeknights.mantle.block.entity.IRetexturedBlockEntity;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.shared.block.entity.TableBlockEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class RetexturedTableBlockEntity extends TableBlockEntity implements IRetexturedBlockEntity {
  private static final String TAG_TEXTURE = "texture";

  private final Lazy<IModelData> data = Lazy.of(this::getRetexturedModelData);
  @Nonnull @Getter
  private Block texture = Blocks.AIR;
  public RetexturedTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Component name, int size) {
    super(type, pos, state, name, size);
  }
  @Override
  public AABB getRenderBoundingBox() {
    return new AABB(worldPosition, worldPosition.offset(1, 2, 1));
  }


  /* Textures */

  @Nonnull
  @Override
  public IModelData getModelData() {
    return this.data.get();
  }

  @Override
  public String getTextureName() {
    if (texture == Blocks.AIR) {
      return "";
    }
    return Objects.requireNonNull(texture.getRegistryName()).toString();
  }

  private void textureUpdated() {
    // update the texture in BE data
    if (level != null && level.isClientSide) {
      Block normalizedTexture = texture == Blocks.AIR ? null : texture;
      IModelData data = getModelData();
      if (data.getData(RetexturedHelper.BLOCK_PROPERTY) != normalizedTexture) {
        data.setData(RetexturedHelper.BLOCK_PROPERTY, normalizedTexture);
        requestModelDataUpdate();
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, 0);
      }
    }
  }

  @Override
  public void updateTexture(String name) {
    Block oldTexture = texture;
    texture = RetexturedHelper.getBlock(name);
    if (oldTexture != texture) {
      setChangedFast();
      textureUpdated();
    }
  }

  @Override
  public void saveSynced(CompoundTag tags) {
    super.saveSynced(tags);
    if (texture != Blocks.AIR) {
      tags.putString(TAG_TEXTURE, Objects.requireNonNull(texture.getRegistryName()).toString());
    }
  }

  @Override
  public void load(CompoundTag tags) {
    super.load(tags);
    if (tags.contains(TAG_TEXTURE, Tag.TAG_STRING)) {
      texture = RetexturedHelper.getBlock(tags.getString(TAG_TEXTURE));
      textureUpdated();
      // legacy fallback for anyone who ported from 1.16 (though I doubt that would work). Remove sometime later in 1.18
    } else if (tags.contains("ForgeData", Tag.TAG_COMPOUND)) {
      CompoundTag forgeData = tags.getCompound("ForgeData");
      if (forgeData.contains(TAG_TEXTURE, Tag.TAG_STRING)) {
        texture = RetexturedHelper.getBlock(forgeData.getString(TAG_TEXTURE));
        textureUpdated();
        forgeData.remove(TAG_TEXTURE);
      }
    }
  }
}
