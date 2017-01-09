package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;

public class ModLuck extends ModifierTrait {

  protected static final int baseCount = 60;
  protected static final int maxLevel = 3;

  // we have a bit of redundancy going on here with the luckAspect and the trait class
  private final LuckAspect aspect;

  public ModLuck() {
    super("luck", 0x2d51e2, maxLevel, 0);

    aspects.clear();
    aspect = new LuckAspect(this);
    addAspects(aspect, new ModifierAspect.CategoryAnyAspect(Category.HARVEST, Category.WEAPON, Category.PROJECTILE));
  }

  public int getLuckLevel(ItemStack itemStack) {
    return getLuckLevel(TinkerUtil.getModifierTag(itemStack, getModifierIdentifier()));
  }

  public int getLuckLevel(NBTTagCompound modifierTag) {
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);
    return aspect.getLevel(data.current);
  }


  @Override
  public boolean canApplyTogether(Enchantment enchantment) {
    return enchantment != Enchantments.SILK_TOUCH;
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    super.applyEffect(rootCompound, modifierTag);
    int lvl = getLuckLevel(modifierTag);

    applyEnchantments(rootCompound, lvl);
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    rewardProgress(tool);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    if(player.world.isRemote || !wasHit) {
      return;
    }
    // we reward one chance per full heart damage dealt. No chance for 0.5 heart hits, sorry :(
    for(int i = (int) (damageDealt / 2f); i > 0; i--) {
      rewardProgress(tool);
    }
  }

  public void rewardProgress(ItemStack tool) {
    // 3% chance
    if(random.nextFloat() > 0.03f) {
      return;
    }

    try {
      if(canApply(tool, tool)) {
        apply(tool);
      }
    } catch(TinkerGuiException e) {
      // no user feedback
    }
  }

  protected void applyEnchantments(NBTTagCompound rootCompound, int lvl) {
    boolean harvest = false;
    boolean weapon = false;

    lvl = Math.min(lvl, Enchantments.LOOTING.getMaxLevel());

    for(Category category : TagUtil.getCategories(rootCompound)) {
      if(category == Category.HARVEST) {
        harvest = true;
      }
      if(category == Category.WEAPON) {
        weapon = true;
      }
    }

    // weapons get looting
    if(weapon) {
      while(lvl > ToolBuilder.getEnchantmentLevel(rootCompound, Enchantments.LOOTING)) {
        ToolBuilder.addEnchantment(rootCompound, Enchantments.LOOTING);
      }
    }
    // harvest tools get fortune
    if(harvest) {
      while(lvl > ToolBuilder.getEnchantmentLevel(rootCompound, Enchantments.FORTUNE)) {
        ToolBuilder.addEnchantment(rootCompound, Enchantments.FORTUNE);
      }
    }
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    int level = getLuckLevel(modifierTag);

    String tooltip = getLocalizedName();
    if(level > 0) {
      tooltip += " " + TinkerUtil.getRomanNumeral(level);
    }

    if(detailed) {
      ModifierNBT data = ModifierNBT.readInteger(modifierTag);
      tooltip += " " + data.extraInfo;
    }
    return tooltip;
  }

  public static class LuckAspect extends ModifierAspect.MultiAspect {

    public LuckAspect(IModifier parent) {
      super(parent, 0x5a82e2, maxLevel, baseCount, 1);

      freeModifierAspect = new FreeFirstModifierAspect(parent, 1);
    }

    @Override
    protected int getMaxForLevel(int level) {
      return (countPerLevel * level * (level + 1)) / 2; // sum(n)
    }

    public int getLevel(int current) {
      int i = 0;
      while(current >= getMaxForLevel(i + 1)) {
        i++;
      }
      return i;
    }
  }
}
