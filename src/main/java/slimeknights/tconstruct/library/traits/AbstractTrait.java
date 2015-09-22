package slimeknights.tconstruct.library.traits;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.Random;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

// Trait and modifier in one! Useful because modifiers are saved as traits
public abstract class AbstractTrait extends Modifier implements ITrait {

  protected static final Random random = new Random();

  public static final String LOC_Name = Modifier.LOC_Name;
  public static final String LOC_Desc = Modifier.LOC_Desc;
  //private final String identifier;
  protected final EnumChatFormatting color;

  public AbstractTrait(String identifier, EnumChatFormatting color) {
    super(Util.sanitizeLocalizationString(identifier));
    //this.identifier = Util.sanitizeLocalizationString(identifier);
    this.color = color;

    // we assume traits can only be applied once.
    // If you want stacking traits you'll have to do that stuff yourself :P
    this.addAspects(new ModifierAspect.SingleAspect(this));
  }

  /**
   * The general assumption is that Traits don't have levels.
   * Should it be needed, however, this function allows you to easily get the level of a trait.
   *
   * @return Level of the trait. 0 If the trait is not present.
   */
  public int getTraitLevel(ItemStack stack) {
    /*
    NBTTagCompound traits = TagUtil.getTraitsTagList(stack);
    for (int i = 0; traits.hasKey(String.valueOf(i)); i++) {
      ModifierNBT data = ModifierNBT.read(traits, String.valueOf(i));
      if (identifier.equals(data.identifier)) {
        return data.level;
      }
    }
*/
    return 0;
  }

  @Override
  public int getMaxCount() {
    return 1;
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String getLocalizedName() {
    return Util.translate(LOC_Name, getIdentifier());
  }

  @Override
  public String getLocalizedDesc() {
    return Util.translate(LOC_Desc, getIdentifier());
  }


  /* Updating */

  @Override
  public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
  }

  @Override
  public void onArmorTick(ItemStack tool, World world, EntityPlayer player) {
  }


  /* Mining & Harvesting */

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
  }

  @Override
  public void beforeBlockBreak(ItemStack tool, BlockEvent.BreakEvent event) {
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
  }

  @Override
  public void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event) {
  }

  /* Attacking */

  @Override
  public boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target) {
    return false;
  }

  @Override
  public float onHit(ItemStack tool,  EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    return newDamage;
  }

  @Override
  public void afterHit(ItemStack tool,  EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
  }

  @Override
  public float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical) {
    return newKnockback;
  }

  @Override
  public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
  }

  /* Durability and repairing */

  @Override
  public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
    return newDamage;
  }

  @Override
  public int onToolHeal(ItemStack tool, int amount, int newAmount, EntityLivingBase entity) {
    return newAmount;
  }

  @Override
  public boolean onRepair(ItemStack tool, int amount, ItemStack repairItem) {
    return true;
  }

  /* Modifier things */
  @Override
  public boolean canApplyCustom(ItemStack stack) {
    // can only apply if the trait isn't present already
    NBTTagList tagList = TagUtil.getTraitsTagList(stack);
    int index = TinkerUtil.getIndexInList(tagList, this.getIdentifier());

    // not present yet
    return index < 0;
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    updateNBTWithColor(modifierTag, color);
  }

  public void updateNBTWithColor(NBTTagCompound modifierTag, EnumChatFormatting newColor) {
    ModifierNBT data = ModifierNBT.readTag(modifierTag);
    data.identifier = identifier;
    data.color = newColor;
    // we ensure at least lvl1 for compatibility with the level-aspect
    if(data.level == 0) {
      data.level = 1;
    }
    data.write(modifierTag);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // add the trait to the traitlist so it gets processed
    NBTTagList traits = TagUtil.getTraitsTagList(rootCompound);
    // if it's not already present
    for(int i = 0; i < traits.tagCount(); i++) {
      if(identifier.equals(traits.getStringTagAt(i))) {
        return;
      }
    }

    traits.appendTag(new NBTTagString(identifier));
    TagUtil.setTraitsTagList(rootCompound, traits);
  }
}
