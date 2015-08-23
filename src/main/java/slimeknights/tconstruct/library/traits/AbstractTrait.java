package slimeknights.tconstruct.library.traits;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;

public abstract class AbstractTrait implements ITrait {

  public static final String LOC_Name = Modifier.LOC_Name;
  public static final String LOC_Desc = Modifier.LOC_Desc;
  private final String identifier;

  public AbstractTrait(String identifier) {
    this.identifier = Util.sanitizeLocalizationString(identifier);
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
  public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player) {
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
}
