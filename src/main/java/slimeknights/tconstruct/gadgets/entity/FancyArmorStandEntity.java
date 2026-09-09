package slimeknights.tconstruct.gadgets.entity;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.HitResult;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

/** Extension of {@link ArmorStand} with extra features */
public class FancyArmorStandEntity extends ArmorStand {
  private static final String TAG_VARIANT = "Variant";
  private static final String TAG_LEFT = "LeftHanded";
  private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(FancyArmorStandEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Boolean> LEFT_HANDED = SynchedEntityData.defineId(FancyArmorStandEntity.class, EntityDataSerializers.BOOLEAN);

  public FancyArmorStandEntity(EntityType<? extends FancyArmorStandEntity> type, Level level) {
    super(type, level);
  }


  /* Data */

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(VARIANT, 0);
    this.entityData.define(LEFT_HANDED, false);
  }

  @Override
  public void addAdditionalSaveData(CompoundTag compound) {
    super.addAdditionalSaveData(compound);
    int frameId = this.getStandId();
    compound.putInt(TAG_VARIANT, frameId);
    compound.putBoolean(TAG_LEFT, isLeft());
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compound) {
    super.readAdditionalSaveData(compound);
    this.entityData.set(VARIANT, compound.getInt(TAG_VARIANT));
    this.entityData.set(LEFT_HANDED, compound.getBoolean(TAG_LEFT));
  }


  /* Extra behavior */

  @Override
  public boolean fireImmune() {
    return true;
  }

  /** Checks if this stand should not be rendered */
  public boolean isClearHidden() {
    // if its clear and it has an item, invisible
    if (getStandId() == StandType.CLEAR.id) {
      for (EquipmentSlot slot : EquipmentSlot.values()) {
        if (!getItemBySlot(slot).isEmpty()) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean isInvisibleTo(Player pPlayer) {
    return isClearHidden() || super.isInvisibleTo(pPlayer);
  }

  /* Hand */

  /** Checks if this entity is left handed, causing the weapon to be held on the left */
  private boolean isLeft() {
    return this.entityData.get(LEFT_HANDED);
  }

  /** Sets whether this stand is left handed */
  public void setLeft(boolean left) {
    this.entityData.set(LEFT_HANDED, left);
  }

  @Override
  public HumanoidArm getMainArm() {
    return isLeft() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
  }


  /* Variant definition */

  /** Gets the index of the stand type */
  protected int getStandId() {
    return this.entityData.get(VARIANT);
  }

  /** Gets the stand type */
  public StandType getStandType() {
    return StandType.byId(this.getStandId());
  }

  /** Sets the type of this stand */
  public void setStandType(StandType type) {
    this.entityData.set(VARIANT, type.getId());
  }

  /** Makes this armor stand small */
  private void setSmall() {
    this.entityData.set(DATA_CLIENT_FLAGS, (byte)(this.entityData.get(DATA_CLIENT_FLAGS) | 1));
  }


  /* Variant items */

  /** Gets the stand item to drop */
  public Item getStandItem() {
    return TinkerGadgets.armorStand.get(getStandType());
  }

  @Override
  public ItemStack getPickedResult(HitResult target) {
    return new ItemStack(getStandItem());
  }

  @Override
  protected Component getTypeName() {
    return Component.translatable(getStandItem().getDescriptionId());
  }

  @Override
  protected void brokenByPlayer(DamageSource source) {
    ItemStack stack = new ItemStack(getStandItem());
    if (this.hasCustomName()) {
      stack.setHoverName(this.getCustomName());
    }
    Block.popResource(this.level(), this.blockPosition(), stack);

    this.brokenByAnything(source);
  }


  /** List of all stand types */
  public enum StandType {
    /** Bamboo: small */
    BAMBOO {
      @Override
      public void onPlace(FancyArmorStandEntity entity) {
        super.onPlace(entity);
        entity.setSmall();
      }
    },
    /** Bone: arms */
    BONE {
      @Override
      public void onPlace(FancyArmorStandEntity entity) {
        super.onPlace(entity);
        entity.setShowArms(true);
      }
    },
    /** Necrotic Bone: fullbright and arms */
    NECROTIC_BONE {
      @Override
      public void onPlace(FancyArmorStandEntity entity) {
        super.onPlace(entity);
        entity.setShowArms(true);
      }
    },
    /** Invisible when wearing armor */
    CLEAR;

    private static final StandType[] VALUES = values();
    @Getter
    private final int id = ordinal();

    /** Callback on place to apply type specific properties */
    public void onPlace(FancyArmorStandEntity entity) {
      entity.setStandType(this);
    }

    /** Any variants that are lit */
    public boolean isFullbright() {
      return this == NECROTIC_BONE;
    }

    /** Gets the stand type for the given index */
    public static StandType byId(int id) {
      if (id < 0 || id >= VALUES.length) {
        id = 0;
      }

      return VALUES[id];
    }
  }
}
