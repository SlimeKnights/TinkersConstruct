package slimeknights.tconstruct.tools.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import io.netty.buffer.ByteBuf;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;

public class EntityArrow extends EntityProjectileBase {

  // animation
  public int roll = 0;
  public boolean rollLeft = false;

  public EntityArrow(World world) {
    super(world);
  }

  public EntityArrow(World world, double d, double d1, double d2) {
    super(world, d, d1, d2);
  }

  public EntityArrow(World world, EntityPlayer player, float speed, float inaccuracy, ItemStack stack) {
    super(world, player, speed, inaccuracy, stack);
  }

  @Override
  public void readSpawnData(ByteBuf data) {
    super.readSpawnData(data);

    // animation stuff, it sometimes rotates left
    rollLeft = rand.nextBoolean();
  }
}
