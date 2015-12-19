package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;

public class ModLuck extends Modifier {

  private static final int baseCount = 120;

  private final LuckAspect aspect;

  public ModLuck() {
    super("luck");

    aspect = new LuckAspect(this);

    addAspects(aspect, new ModifierAspect.CategoryAnyAspect(Category.HARVEST, Category.WEAPON));
  }

  @Override
  public boolean canApplyTogether(Enchantment enchantment) {
    return enchantment != Enchantment.silkTouch;
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    // taken care of by aspects
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);

    int lvl = aspect.getLevel(data.current);

    boolean harvest = false;
    boolean weapon = false;

    for(Category category : TagUtil.getCategories(rootCompound)) {
      if(category == Category.HARVEST) harvest = true;
      if(category == Category.WEAPON) weapon = true;
    }

    // weapons get looting
    if(weapon) {
      while(lvl > ToolBuilder.getEnchantmentLevel(rootCompound, Enchantment.looting)) {
        ToolBuilder.addEnchantment(rootCompound, Enchantment.looting);
      }
    }
    // harvest tools get fortune
    if(harvest) {
      while(lvl > ToolBuilder.getEnchantmentLevel(rootCompound, Enchantment.fortune)) {
        ToolBuilder.addEnchantment(rootCompound, Enchantment.fortune);
      }
    }
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    return getLeveledTooltip(modifierTag, detailed);
  }

  public static class LuckAspect extends ModifierAspect.MultiAspect {

    public LuckAspect(IModifier parent) {
      super(parent, 0x5a82e2, 3, baseCount, 1);
    }

    @Override
    protected int getMaxForLevel(int level) {
      level = (level + (level+1))/2; // sum(n)
      return countPerLevel * level;
    }

    public int getLevel(int current) {
      int i = 0;
      while(current >= getMaxForLevel(i+1)) i++;
      return i;
    }
  }
}
