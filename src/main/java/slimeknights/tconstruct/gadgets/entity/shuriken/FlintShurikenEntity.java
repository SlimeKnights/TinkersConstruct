package slimeknights.tconstruct.gadgets.entity.shuriken;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

/** @deprecated use {@link slimeknights.tconstruct.tools.entity.ThrownShuriken} */
@Deprecated
public class FlintShurikenEntity extends ShurikenEntityBase {

  public FlintShurikenEntity(EntityType<? extends FlintShurikenEntity> p_i50159_1_, Level p_i50159_2_) {
    super(p_i50159_1_, p_i50159_2_);
  }

  public FlintShurikenEntity(Level worldIn, LivingEntity throwerIn) {
    super(TinkerGadgets.flintShurikenEntity.get(), throwerIn, worldIn);
  }

  @Override
  protected Item getDefaultItem() {
    return TinkerGadgets.flintShuriken.get();
  }

  @Override
  public float getDamage() {
    return 3.0F;
  }

  @Override
  public float getKnockback() {
    return 0.6F;
  }
}
