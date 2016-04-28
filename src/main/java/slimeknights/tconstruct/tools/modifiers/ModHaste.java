package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public class ModHaste extends ToolModifier {

  private final int max;

  public ModHaste(int max) {
    super("haste", 0x910000);

    this.max = max;
    
    addAspects(new ModifierAspect.MultiAspect(this, 5, max, 1));
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);

    boolean harvest = false;
    
    for(Category category : TagUtil.getCategories(rootCompound)) {
      if(category == Category.HARVEST) harvest = true;
    }

    ToolNBT toolData = TagUtil.getOriginalToolStats(rootCompound);
    NBTTagCompound tag = TagUtil.getToolTag(rootCompound);
    int level = data.current / max;
    
    // only boost mining speed if we have a harvest tool
    if(harvest) {
      float speed = toolData.speed;
      for(int count = data.current; count > 0; count--) {
        if(speed <= 10f) {
          // linear scaling from 0.08 to 0.06 per piece till 10 miningspeed
          speed += 0.15f - 0.05f * speed / 10f;
        }
        else if(speed <= 20f) {
          speed += 0.1f - 0.05 * speed / 20f;
        }
        else {
          speed += 0.05;
        }
      }
  
      // each full level gives a flat 0.5 bonus, not influenced by dimishing returns
      speed += level * 0.5f;
  
      // save it to the tool
      speed -= toolData.speed;
      speed += tag.getFloat(Tags.MININGSPEED);
      tag.setFloat(Tags.MININGSPEED, speed);
    }
    
    // attack speed: each level adds 0.2 to the multiplier, meaning level 1 is 1.2 times the default speed, and 5 is 2.0 times it
    float attackSpeed = 1.0f + (level * 0.2f);

    // save it to the tool, adding the original boost from lightweight (if it exists)
    attackSpeed -= toolData.attackSpeed;
    attackSpeed += tag.getFloat(Tags.ATTACKSPEED);
    tag.setFloat(Tags.ATTACKSPEED, attackSpeed);
  }
  
  // don't allow on projectiles
  protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
    return !((ToolCore)stack.getItem()).hasCategory(Category.NO_MELEE);
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    return getLeveledTooltip(modifierTag, detailed);
  }
}
