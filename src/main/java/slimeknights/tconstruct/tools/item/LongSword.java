package slimeknights.tconstruct.tools.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.tools.TinkerTools;

public class LongSword extends BroadSword {

  public LongSword() {
    super(new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.swordBlade),
          new PartMaterialType.ToolPartType(TinkerTools.toolRod));
  }

  @Override
  public float damagePotential() {
    return 1.2f;
  }

  @Override
  public int attackSpeed() {
    return 0;
  }

  @Override
  public float damageCutoff() {
    return 18f;
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.NONE;
  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    // has to be done in onUpdate because onTickUsing is too early and gets overwritten. bleh.
    preventSlowDown(entityIn, 0.9f);

    super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
    int time = this.getMaxItemUseDuration(stack) - timeLeft;
    if (time > 5)
    {
      player.addExhaustion(0.2F);
      player.setSprinting(true);

      float increase = (float) (0.02 * time + 0.2);
      if (increase > 0.56f)
        increase = 0.56f;
      player.motionY += increase;

      float speed = 0.05F * time;
      if (speed > 0.925f)
        speed = 0.925f;
      player.motionX = (double) (-MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed);
      player.motionZ = (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * speed);
    }

    super.onPlayerStoppedUsing(stack, world, player, timeLeft);
  }
}
