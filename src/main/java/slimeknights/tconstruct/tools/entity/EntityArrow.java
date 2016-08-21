package slimeknights.tconstruct.tools.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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
  protected void playHitBlockSound(float speed, IBlockState state) {
    this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
  }

  @Override
  public void readSpawnData(ByteBuf data) {
    super.readSpawnData(data);

    // animation stuff, it sometimes rotates left
    rollLeft = rand.nextBoolean();
  }
}
