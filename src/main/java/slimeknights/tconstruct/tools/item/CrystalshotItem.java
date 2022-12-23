package slimeknights.tconstruct.tools.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.tools.TinkerTools;

/** Internal item used by crystalshot modifier */
public class CrystalshotItem extends ArrowItem {
  private static final String TAG_VARIANT = "variant";
  public CrystalshotItem(Properties props) {
    super(props);
  }

  @Override
  public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
    CrystalshotEntity arrow = new CrystalshotEntity(pLevel, pShooter);
    CompoundTag tag = pStack.getTag();
    if (tag != null) {
      arrow.setVariant(tag.getString(TAG_VARIANT));
    }
    return arrow;
  }

  @Override
  public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
    return EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.INFINITY_ARROWS, bow) > 0;
  }

  @Override
  public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {}

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
    }

    public CrystalshotEntity(Level level, LivingEntity shooter) {
      super(TinkerTools.crystalshotEntity.get(), shooter, level);
      pickup = Pickup.CREATIVE_ONLY;
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
