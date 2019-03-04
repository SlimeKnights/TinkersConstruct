package slimeknights.tconstruct.library.traits;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

// Trait and modifier in one! Useful because modifiers are saved as traits
public abstract class AbstractTrait extends Modifier implements ITrait {

  public static final String LOC_Name = Modifier.LOC_Name;
  public static final String LOC_Desc = Modifier.LOC_Desc;
  //private final String identifier;
  protected final int color;

  public AbstractTrait(String identifier, TextFormatting color) {
    this(identifier, Util.enumChatFormattingToColor(color));
  }

  public AbstractTrait(String identifier, int color) {
    super(Util.sanitizeLocalizationString(identifier));
    //this.identifier = Util.sanitizeLocalizationString(identifier);
    this.color = color;

    // we assume traits can only be applied once.
    // If you want stacking traits you'll have to do that stuff yourself :P
    this.addAspects(new ModifierAspect.SingleAspect(this));
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

  @Override
  public boolean isHidden() {
    return false;
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
  public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
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
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    return newDamage;
  }

  @Override
  public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
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
  public void onRepair(ItemStack tool, int amount) {
  }

  /* Modifier things */

  // The name the modifier tag is saved under
  public String getModifierIdentifier() {
    return identifier;
  }

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
    updateNBTforTrait(modifierTag, color);
  }

  public void updateNBTforTrait(NBTTagCompound modifierTag, int newColor) {
    ModifierNBT data = ModifierNBT.readTag(modifierTag);
    data.identifier = getModifierIdentifier();
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

  protected boolean isToolWithTrait(ItemStack itemStack) {
    return TinkerUtil.hasTrait(TagUtil.getTagSafe(itemStack), this.getIdentifier());
  }
}
