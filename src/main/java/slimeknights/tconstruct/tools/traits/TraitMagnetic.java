package slimeknights.tconstruct.tools.traits;

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
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.potion.TinkerPotion;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.AbstractTraitLeveled;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitMagnetic extends AbstractTraitLeveled {

  public static TinkerPotion Magnetic = new MagneticPotion();

  public TraitMagnetic(int levels) {
    super("magnetic", 0xdddddd, 3, levels);
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    ModifierNBT data = new ModifierNBT(TinkerUtil.getModifierTag(tool, name));
    Magnetic.apply(player, 30, data.level);
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    ModifierNBT data = new ModifierNBT(TinkerUtil.getModifierTag(tool, name));
    Magnetic.apply(player, 30, data.level);
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
      double range = 1.8d;
      range += entity.getActivePotionEffect(this).getAmplifier() * 0.3f;

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
