package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FancyItemFrameEntity extends ItemFrameEntity implements IEntityAdditionalSpawnData {

  private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(FancyItemFrameEntity.class, DataSerializers.VARINT);
  private static final String TAG_VARIANT = "Variant";

  public FancyItemFrameEntity(EntityType<? extends FancyItemFrameEntity> type, World world) {
    super(type, world);
  }

  public FancyItemFrameEntity(World worldIn, BlockPos blockPos, Direction face, int variant) {
    super(TinkerGadgets.fancy_item_frame, worldIn);
    this.hangingPosition = blockPos;
    this.updateFacingWithBoundingBox(face);
    this.dataManager.set(VARIANT, variant);
  }

  @Override
  protected void registerData() {
    super.registerData();

    this.dataManager.register(VARIANT, 0);
  }

  public FrameType getFrameType() {
    return FrameType.byId(this.getVariantIndex());
  }

  public int getVariantIndex() {
    return this.dataManager.get(VARIANT);
  }

  @Nullable
  @Override
  public ItemEntity entityDropItem(@Nonnull ItemStack stack, float offset) {
    if (stack.getItem() == Items.ITEM_FRAME) {
      stack = new ItemStack(FrameType.getFrameFromType(this.getFrameType()));
    }
    return super.entityDropItem(stack, offset);
  }

  @Nonnull
  @Override
  public ItemStack getPickedResult(RayTraceResult target) {
    ItemStack held = this.getDisplayedItem();
    if (held.isEmpty()) {
      return new ItemStack(FrameType.getFrameFromType(this.getFrameType()));
    } else {
      return held.copy();
    }
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putInt(TAG_VARIANT, this.getVariantIndex());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.dataManager.set(VARIANT, compound.getInt(TAG_VARIANT));
  }

  @Nonnull
  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void writeSpawnData(PacketBuffer buffer) {
    buffer.writeVarInt(this.getVariantIndex());
    buffer.writeBlockPos(this.hangingPosition);
    buffer.writeVarInt(this.facingDirection.getIndex());
  }

  @Override
  public void readSpawnData(PacketBuffer buffer) {
    this.dataManager.set(VARIANT, buffer.readVarInt());
    this.hangingPosition = buffer.readBlockPos();
    this.updateFacingWithBoundingBox(Direction.byIndex(buffer.readVarInt()));
  }

  private static void removeClickEvents(ITextComponent p_207712_0_) {
    p_207712_0_.applyTextStyle((p_213318_0_) -> {
      p_213318_0_.setClickEvent(null);
    }).getSiblings().forEach(FancyItemFrameEntity::removeClickEvents);
  }

  @Override
  public ITextComponent getName() {
    ITextComponent itextcomponent = this.getCustomName();
    if (itextcomponent != null) {
      ITextComponent textComponent = itextcomponent.deepCopy();
      removeClickEvents(textComponent);
      return textComponent;
    } else {
      String translationKey = this.getType().getTranslationKey();

      return new TranslationTextComponent(translationKey + "." + this.getFrameType().getName());
    }
  }
}
