package slimeknights.tconstruct.tools.common.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import io.netty.buffer.ByteBuf;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;

public class EntityShuriken extends EntityProjectileBase {

  // animation
  public int spin = 0;
  public int rollAngle = 0;

  public EntityShuriken(World world) {
    super(world);
  }

  public EntityShuriken(World world, double d, double d1, double d2) {
    super(world, d, d1, d2);
  }

  public EntityShuriken(World world, EntityPlayer player, float speed, float inaccuracy, ItemStack stack, ItemStack launchingStack) {
    super(world, player, speed, inaccuracy, 1f, stack, launchingStack);
  }

  @Override
  protected void init() {
    setSize(0.3f, 0.1f);
    this.bounceOnNoDamage = false;
  }

  @Override
  public double getGravity() {
    return (this.ticksExisted / 10) * 0.04d; // integer division. so the first ticks it will have no gravity at all.
  }

  @Override
  public double getSlowdown() {
    return 0.05f;
  }

  @Override
  protected void playHitEntitySound() {

  }

  @Override
  public void readSpawnData(ByteBuf data) {
    super.readSpawnData(data);

    // this is only relevant clientside only, so we don't actually have it on the server
    spin = rand.nextInt(360);
    rollAngle = 7 - rand.nextInt(14);
  }
}
