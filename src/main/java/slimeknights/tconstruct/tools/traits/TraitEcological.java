package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;


// Your tool is green!
// This is a very weak version of repair/moss.. be careful not to catch any splinters!
public class TraitEcological extends AbstractTrait {

  private static int chance = 40; // 1/X chance of getting the effect

  public TraitEcological() {
    super("ecological", TextFormatting.GREEN);
  }

  @Override
  public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
    // *20 because 20 ticks in a second
    if(!world.isRemote && entity instanceof EntityLivingBase && random.nextInt(20 * chance) == 0) {
      if(((EntityLivingBase) entity).getActiveItemStack() != tool) {
        ToolHelper.healTool(tool, 1, (EntityLivingBase) entity);
      }
    }
  }
}
