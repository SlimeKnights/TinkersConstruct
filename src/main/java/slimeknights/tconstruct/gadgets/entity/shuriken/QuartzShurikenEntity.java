package slimeknights.tconstruct.gadgets.entity.shuriken;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

/** @deprecated use {@link slimeknights.tconstruct.tools.entity.ThrownShuriken} */
@Deprecated
public class QuartzShurikenEntity extends ShurikenEntityBase {

  public QuartzShurikenEntity(EntityType<? extends QuartzShurikenEntity> p_i50159_1_, Level p_i50159_2_) {
    super(p_i50159_1_, p_i50159_2_);
  }

  public QuartzShurikenEntity(Level worldIn, LivingEntity throwerIn) {
    super(TinkerGadgets.quartzShurikenEntity.get(), throwerIn, worldIn);
  }

  @Override
  protected Item getDefaultItem() {
    return TinkerGadgets.quartzShuriken.get();
  }

  @Override
  public float getDamage() {
    return 5.0F;
  }

  @Override
  public float getKnockback() {
    return 0.4F;
  }
}
