package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.tools.potion.TinkerPotion;

public class TraitInsatiable extends AbstractTrait {

  public static TinkerPotion Insatiable = new TinkerPotion(Util.getResource("insatiable"), true, false);

  public TraitInsatiable() {
    super("insatiable", EnumChatFormatting.DARK_PURPLE);
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    float bonus = Insatiable.getLevel(player) / 3f;
    return super.onHit(tool, player, target, damage, newDamage, isCritical) + bonus;
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    int level = 1;
    level += Insatiable.getLevel(player);

    level = Math.min(10, level);

    Insatiable.apply(player, 15 * 20, level);
  }

  @Override
  public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
    int level = Insatiable.getLevel(entity) + 1;
    return super.onToolDamage(tool, damage, newDamage, entity) + level;
  }
}
