package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;


// Your tool is green!
// This is a very weak version of repair/moss.. be careful not to catch any splinters!
public class TraitEcological extends AbstractTrait {

  public static DamageSource splinter = new DamageSource("splinter").setDamageBypassesArmor();
  private static int chance = 60; // 1/X chance of getting the effect

  public TraitEcological() {
    super("ecological", EnumChatFormatting.GREEN);
  }

  @Override
  public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
    // *20 because 20 ticks in a second
    if(!world.isRemote && entity instanceof EntityLivingBase && random.nextInt(20 * chance) == 0) {
      ToolHelper.healTool(tool, 1, (EntityLivingBase) entity);
    }
  }
}
