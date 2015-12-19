package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

import javax.vecmath.Vector3d;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.tools.potion.TinkerPotion;

public class TraitMagnetic extends AbstractTrait {

  public static TinkerPotion Magnetic = new MagneticPotion();

  public TraitMagnetic() {
    super("magnetic", EnumChatFormatting.GRAY);
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    Magnetic.apply(player, 20);
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    Magnetic.apply(player, 10);
    return super.onHit(tool, player, target, damage, newDamage, isCritical);
  }

  private static class MagneticPotion extends TinkerPotion {

    public MagneticPotion() {
      super(Util.getResource("magnetic"), false, false);
    }

    @Override
    public boolean isReady(int duration, int strength) {
      return (duration & 1) == 0; // basically %2
    }

    @Override
    public void performEffect(EntityLivingBase entity, int id) {
      // super magnetic - inspired by botanias code
      double x = entity.posX;
      double y = entity.posY;
      double z = entity.posZ;
      double range = 1.7d;

      List<EntityItem> items = entity.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.fromBounds(x - range, y - range, z - range, x + range, y + range, z + range));
      int pulled = 0;
      for(EntityItem item : items) {
        if(item.getEntityItem() == null || item.getEntityItem().getItem() == null || item.isDead) {
          continue;
        }

        if(pulled > 200) {
          break;
        }

        // constant force!
        float strength = 0.07f;

        // calculate direction: item -> player
        Vector3d vec = new Vector3d(x, y, z);
        vec.sub(new Vector3d(item.posX, item.posY, item.posZ));

        vec.normalize();
        vec.scale(strength);

        // we calculated the movement vector and set it to the correct strength.. now we apply it \o/
        item.motionX += vec.x;
        item.motionY += vec.y;
        item.motionZ += vec.z;

        pulled++;
      }
    }
  }
}
