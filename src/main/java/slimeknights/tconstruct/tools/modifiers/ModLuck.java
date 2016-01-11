package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;

public class ModLuck extends ModifierTrait {

  protected static final int baseCount = 120;
  protected static final int maxLevel = 3;

  // we have a bit of redundancy going on here with the luckAspect and the trait class
  private final LuckAspect aspect;

  public ModLuck() {
    super("luck", 0x5a82e2, maxLevel, 0);

    aspects.clear();
    aspect = new LuckAspect(this);
    addAspects(aspect, new ModifierAspect.CategoryAnyAspect(Category.HARVEST, Category.WEAPON));
  }

  @Override
  public boolean canApplyTogether(Enchantment enchantment) {
    return enchantment != Enchantment.silkTouch;
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    super.applyEffect(rootCompound, modifierTag);
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);

    int lvl = aspect.getLevel(data.current);

    applyEnchantments(rootCompound, lvl);
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    rewardProgress(tool);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    if(player.worldObj.isRemote || !wasHit) {
      return;
    }
    // we reward one chance per full heart damage dealt. No chance for 0.5 heart hits, sorry :(
    for(int i = (int)(damageDealt/2f); i > 0; i--) {
      rewardProgress(tool);
    }
  }

  public void rewardProgress(ItemStack tool) {
    // 2% chance
    if(random.nextFloat() > 0.02f) return;

    try {
      if(canApply(tool))
        apply(tool);
    } catch(TinkerGuiException e) {
      // no user feedback
    }
  }

  protected void applyEnchantments(NBTTagCompound rootCompound, int lvl) {
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
      super(parent, 0x5a82e2, maxLevel, baseCount, 1);

      freeModifierAspect = new FreeFirstModifierAspect(parent, 1);
    }

    @Override
    protected int getMaxForLevel(int level) {
      return (countPerLevel * level * (level+1))/2; // sum(n)
    }

    public int getLevel(int current) {
      int i = 0;
      while(current >= getMaxForLevel(i+1)) i++;
      return i;
    }
  }
}
