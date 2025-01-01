package slimeknights.tconstruct.tools.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.ArrayList;
import java.util.List;

/** Internal item used by crystalshot modifier */
public class CrystalshotItem extends ArrowItem {
  /** Possible variants for a random crystalshot, so addons can register their own if desired */
  public static final List<String> RANDOM_VARIANTS;
  static {
    RANDOM_VARIANTS = new ArrayList<>();
    RANDOM_VARIANTS.add("amethyst");
    RANDOM_VARIANTS.add("earthslime");
    RANDOM_VARIANTS.add("skyslime");
    RANDOM_VARIANTS.add("ichor");
    RANDOM_VARIANTS.add("enderslime");
    RANDOM_VARIANTS.add("quartz");
  }
  /** NBT key for variants on the stack and entity */
  private static final String TAG_VARIANT = "variant";
  public CrystalshotItem(Properties props) {
    super(props);
  }

  @Override
  public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
    CrystalshotEntity arrow = new CrystalshotEntity(pLevel, pShooter);
    String variant = "random";
    CompoundTag tag = pStack.getTag();
    if (tag != null) {
      variant = tag.getString(TAG_VARIANT);
    }
    if ("random".equals(variant)) {
      variant = RANDOM_VARIANTS.get(pShooter.getRandom().nextInt(RANDOM_VARIANTS.size()));
    }
    arrow.setVariant(variant);
    return arrow;
  }

  @Override
  public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
    return bow.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0;
  }

  /** Creates a crystal shot with the given variant */
  public static ItemStack withVariant(String variant, int size) {
    ItemStack stack = new ItemStack(TinkerTools.crystalshotItem);
    stack.setCount(size);
    stack.getOrCreateTag().putString(TAG_VARIANT, variant);
    return stack;
  }

  public static class CrystalshotEntity extends AbstractArrow {
    private static final EntityDataAccessor<String> SYNC_VARIANT = SynchedEntityData.defineId(CrystalshotEntity.class, EntityDataSerializers.STRING);

    public CrystalshotEntity(EntityType<? extends CrystalshotEntity> type, Level level) {
      super(type, level);
      pickup = Pickup.CREATIVE_ONLY;
      soundEvent = Sounds.CRYSTALSHOT.getSound();
    }

    public CrystalshotEntity(Level level, LivingEntity shooter) {
      super(TinkerTools.crystalshotEntity.get(), shooter, level);
      pickup = Pickup.CREATIVE_ONLY;
      soundEvent = Sounds.CRYSTALSHOT.getSound();
    }

    @Override
    public void setSoundEvent(SoundEvent sound) {
      if (sound != SoundEvents.ARROW_HIT && sound != SoundEvents.CROSSBOW_HIT) {
        super.setSoundEvent(sound);
      }
    }

    @Override
    protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(SYNC_VARIANT, "");
    }

    /** Gets the texture variant of this shot */
    public String getVariant() {
      String variant = this.entityData.get(SYNC_VARIANT);
      if (variant.isEmpty()) {
        return "amethyst";
      }
      return variant;
    }

    /** Sets the arrow's variant */
    public void setVariant(String variant) {
      this.entityData.set(SYNC_VARIANT, variant);
    }

    @Override
    public ItemStack getPickupItem() {
      return withVariant(getVariant(), 1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putString(TAG_VARIANT, getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      setVariant(tag.getString(TAG_VARIANT));
    }
  }
}
