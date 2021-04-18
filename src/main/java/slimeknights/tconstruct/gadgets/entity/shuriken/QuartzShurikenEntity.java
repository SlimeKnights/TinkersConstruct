package slimeknights.tconstruct.gadgets.entity.shuriken;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

public class QuartzShurikenEntity extends ShurikenEntityBase {

  public QuartzShurikenEntity(EntityType<? extends QuartzShurikenEntity> p_i50159_1_, World p_i50159_2_) {
    super(p_i50159_1_, p_i50159_2_);
  }

  public QuartzShurikenEntity(World worldIn, double x, double y, double z) {
    super(TinkerGadgets.quartzShurikenEntity, x, y, z, worldIn);
  }

  public QuartzShurikenEntity(World worldIn, LivingEntity throwerIn) {
    super(TinkerGadgets.quartzShurikenEntity, throwerIn, worldIn);
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
