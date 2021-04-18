package slimeknights.tconstruct.gadgets.entity.shuriken;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

public class FlintShurikenEntity extends ShurikenEntityBase {

  public FlintShurikenEntity(EntityType<? extends FlintShurikenEntity> p_i50159_1_, World p_i50159_2_) {
    super(p_i50159_1_, p_i50159_2_);
  }

  public FlintShurikenEntity(World worldIn, double x, double y, double z) {
    super(TinkerGadgets.flintShurikenEntity, x, y, z, worldIn);
  }

  public FlintShurikenEntity(World worldIn, LivingEntity throwerIn) {
    super(TinkerGadgets.flintShurikenEntity, throwerIn, worldIn);
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
