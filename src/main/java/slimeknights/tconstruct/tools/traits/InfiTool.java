package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class InfiTool extends AbstractTrait {

  public static InfiTool INSTANCE = new InfiTool();

  public InfiTool() {
    super("infitool", 0xffffff);
  }

  @Override
  public boolean isHidden() {
    return true;
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    super.applyEffect(rootCompound, modifierTag);

    ToolNBT stats = TagUtil.getToolStats(rootCompound);
    stats.durability = 999999;
    stats.attack = 999999f;
    stats.speed = 999999f;
    stats.modifiers = 0;
    TagUtil.setToolTag(rootCompound, stats.get());
    TagUtil.setNoRenameFlag(rootCompound, true);
  }

  @Override
  public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
    // doesn't take damage at all
    return 0;
  }
}
