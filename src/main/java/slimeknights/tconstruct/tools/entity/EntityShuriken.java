package slimeknights.tconstruct.tools.entity;

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

  public EntityShuriken(World world, EntityPlayer player, float speed, float accuracy, ItemStack stack) {
    super(world, player, speed, accuracy, stack);
  }

  @Override
  protected void init() {
    setSize(0.3f, 0.1f);
    this.bounceOnNoDamage = false;
  }

  @Override
  protected double getGravity() {
    return (this.ticksExisted/8) * 0.018d; // integer division. so the first 20 ticks it will have no gravity at all.
  }

  @Override
  protected double getSlowdown() {
    return 0.15f;
  }

  @Override
  protected double getStuckDepth() {
    return 0.8d;
  }

  @Override
  public void readSpawnData(ByteBuf data) {
    super.readSpawnData(data);

  // this is only relevant clientside only, so we don't actually have it on the server
    spin = rand.nextInt(360);
    rollAngle = 7 - rand.nextInt(14);
  }
}
