package slimeknights.tconstruct.library.tools.part.block;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tools.TinkerToolParts;

import javax.annotation.Nonnull;
import java.util.Objects;

import static slimeknights.tconstruct.library.tools.part.IMaterialItem.MATERIAL_TAG;

/** Block entity logic for {@link MaterialBlock} */
public class MaterialBlockEntity extends MantleBlockEntity {
  @Nonnull
  @Getter
  private MaterialVariantId material = IMaterial.UNKNOWN_ID;

  /** Constructor for addons to register a new block entity for their material blocks */
  public MaterialBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  /** Constructor for our material blocks. */
  public MaterialBlockEntity(BlockPos pos, BlockState state) {
    this(TinkerToolParts.materialBlock.get(), pos, state);
  }

  @Override
  public ModelData getModelData() {
    return ModelData.builder().with(ModelProperties.MATERIAL, material).build();
  }

  /** Called to update the material on the block. */
  public void setMaterial(MaterialVariantId material) {
    MaterialVariantId oldMaterial = this.material;
    // TODO: resolve redirects?
    this.material = material;
    if (!oldMaterial.equals(material)) {
      setChangedFast();
      RetexturedHelper.onTextureUpdated(this);
    }
  }

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  protected void saveSynced(CompoundTag tags) {
    super.saveSynced(tags);
    if (material != IMaterial.UNKNOWN_ID) {
      tags.putString(MATERIAL_TAG, material.toString());
    }
  }

  @Override
  public void load(CompoundTag tags) {
    super.load(tags);
    if (tags.contains(MATERIAL_TAG, Tag.TAG_STRING)) {
      material = Objects.requireNonNullElse(MaterialVariantId.tryParse(tags.getString(MATERIAL_TAG)), IMaterial.UNKNOWN_ID);
      RetexturedHelper.onTextureUpdated(this);
    }
  }
}
