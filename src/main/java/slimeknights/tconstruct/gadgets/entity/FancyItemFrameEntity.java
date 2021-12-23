package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;

public class FancyItemFrameEntity extends ItemFrameEntity implements IEntityAdditionalSpawnData {
  private static final int DIAMOND_TIMER = 300;
  private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(FancyItemFrameEntity.class, DataSerializers.VARINT);
  private static final String TAG_VARIANT = "Variant";
  private static final String TAG_ROTATION_TIMER = "RotationTimer";

  private int rotationTimer = 0;
  public FancyItemFrameEntity(EntityType<? extends FancyItemFrameEntity> type, World world) {
    super(type, world);
  }

  public FancyItemFrameEntity(World worldIn, BlockPos blockPos, Direction face, FrameType variant) {
    super(TinkerGadgets.itemFrameEntity.get(), worldIn);
    this.hangingPosition = blockPos;
    this.updateFacingWithBoundingBox(face);
    this.dataManager.set(VARIANT, variant.getId());
  }

  /** Quick helper as two types spin */
  private static boolean doesRotate(int type) {
    return type == FrameType.GOLD.getId() || type == FrameType.REVERSED_GOLD.getId() || type == FrameType.DIAMOND.getId();
  }

  /** Resets the rotation timer to 0 */
  public void updateRotationTimer(boolean overturn) {
    this.rotationTimer = overturn ? -DIAMOND_TIMER : 0;
  }

  @Override
  public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
    if (!player.isSneaking() && getFrameId() == FrameType.CLEAR.getId() && !getDisplayedItem().isEmpty()) {
      BlockPos behind = getPosition().offset(facingDirection.getOpposite());
      BlockState state = world.getBlockState(behind);
      if (!state.isAir(world, behind)) {
        ActionResultType result = state.onBlockActivated(world, player, hand, Util.createTraceResult(behind, facingDirection, false));
        if (result.isSuccessOrConsume()) {
          return result;
        }
      }
    }
    return super.processInitialInteract(player, hand);
  }

  @Override
  public void tick() {
    super.tick();
    // diamond spins on both sides
    int frameId = getFrameId();
    if (frameId == FrameType.DIAMOND.getId()) {
      rotationTimer++;
      // diamond winds down every 30 seconds, but does not go past 0, makes a full timer 3:30
      if (rotationTimer >= 300) {
        rotationTimer = 0;
        if (!world.isRemote) {
          int curRotation = getRotation();
          if (curRotation > 0) {
            this.setItemRotation(curRotation - 1);
          }
        }
      }
      return;
    }
    // for gold and reversed gold, only increment timer serverside
    if (!world.isRemote) {
      if (doesRotate(frameId)) {
        rotationTimer++;
        if (rotationTimer >= 20) {
          rotationTimer = 0;
          int curRotation = getRotation();
          if (frameId == FrameType.REVERSED_GOLD.getId()) {
            // modulo is not positive bounded, so we have to manually ensure positive
            curRotation -= 1;
            if (curRotation == -1) {
              curRotation = 7;
            }
            this.setItemRotation(curRotation);
          } else {
            this.setItemRotation(curRotation + 1);
          }
        }
      }
    }
  }

  @Override
  public void setDisplayedItemWithUpdate(ItemStack stack, boolean updateComparator) {
    super.setDisplayedItemWithUpdate(stack, updateComparator);
    // spinning frames reset to 0 on changing item
    if (updateComparator && !world.isRemote && doesRotate(getFrameId())) {
      setRotation(0, false);
    }
  }

  /** Internal logic to set the rotation */
  private void setRotationRaw(int rotationIn, boolean updateComparator) {
    this.getDataManager().set(ROTATION, rotationIn);
    if (updateComparator && this.hangingPosition != null) {
      this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
    }
  }

  @Override
  protected void setRotation(int rotationIn, boolean updateComparator) {
    this.rotationTimer = 0;
    // diamond goes 0-8 rotation, no modulo and needs to sync with client
    if (getFrameId() == FrameType.DIAMOND.getId()) {
      if (!world.isRemote && updateComparator) {
        // play a sound as diamond is special
        this.playSound(Sounds.ITEM_FRAME_CLICK.getSound(), 1.0f, 1.0f);
      }
      // diamond allows rotation between 0 and 16
      setRotationRaw(Math.min(rotationIn, 16), updateComparator);
    } else {
      // non diamond rotates around after 7
      setRotationRaw(rotationIn % 8, updateComparator);
    }
  }

  @Override
  protected void registerData() {
    super.registerData();

    this.dataManager.register(VARIANT, 0);
  }

  /** Gets the frame type */
  public FrameType getFrameType() {
    return FrameType.byId(this.getFrameId());
  }

  /** Gets the frame type */
  public Item getFrameItem() {
    return TinkerGadgets.itemFrame.get(getFrameType());
  }

  /** Gets the index of the frame type */
  protected int getFrameId() {
    return this.dataManager.get(VARIANT);
  }

  @Nullable
  @Override
  public ItemEntity entityDropItem(ItemStack stack, float offset) {
    // rather than rewrite dropItemOrSelf, just sub in our item for item frames here
    if (stack.getItem() == Items.ITEM_FRAME) {
      stack = new ItemStack(getFrameItem());
    }
    return super.entityDropItem(stack, offset);
  }

  @Override
  public ItemStack getPickedResult(RayTraceResult target) {
    ItemStack held = this.getDisplayedItem();
    if (held.isEmpty()) {
      return new ItemStack(getFrameItem());
    } else {
      return held.copy();
    }
  }

  @Override
  public boolean isImmuneToFire() {
    return super.isImmuneToFire() || getFrameId() == FrameType.NETHERITE.getId();
  }

  @Override
  public boolean isImmuneToExplosions() {
    return super.isImmuneToExplosions() || getFrameId() == FrameType.NETHERITE.getId();
  }

  @Override
  public int getAnalogOutput() {
    if (this.getDisplayedItem().isEmpty()) {
      return 0;
    }
    int rotation = getRotation();
    if (getFrameId() == FrameType.DIAMOND.getId()) {
      return Math.min(15, rotation + 1);
    }
    return rotation % 8 + 1;
  }


  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    int frameId = this.getFrameId();
    compound.putInt(TAG_VARIANT, frameId);
    if (doesRotate(frameId)) {
      compound.putInt(TAG_ROTATION_TIMER, rotationTimer);
    }
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    int frameId = compound.getInt(TAG_VARIANT);
    this.dataManager.set(VARIANT, frameId);
    if (doesRotate(frameId)) {
      rotationTimer = compound.getInt(TAG_ROTATION_TIMER);
    }
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void writeSpawnData(PacketBuffer buffer) {
    buffer.writeVarInt(this.getFrameId());
    buffer.writeBlockPos(this.hangingPosition);
    buffer.writeVarInt(this.facingDirection.getIndex());
  }

  @Override
  public void readSpawnData(PacketBuffer buffer) {
    this.dataManager.set(VARIANT, buffer.readVarInt());
    this.hangingPosition = buffer.readBlockPos();
    this.updateFacingWithBoundingBox(Direction.byIndex(buffer.readVarInt()));
  }


  @Override
  protected ITextComponent getProfessionName() {
    return new TranslationTextComponent(getFrameItem().getTranslationKey());
  }
}
