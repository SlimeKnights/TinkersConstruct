package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitTasty extends AbstractTrait {

  private static final int CHICKENWING = 2;
  public static final int NOM_COST = 5;

  public TraitTasty() {
    super("tasty", TextFormatting.RED);
  }

  @Override
  public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
    // needs to be in hand to be eaten!
    if(!isSelected || !(entity instanceof EntityPlayer) || entity.getEntityWorld().isRemote) {
      return;
    }

    FoodStats foodStats = ((EntityPlayer) entity).getFoodStats();
    float chance = 0.01f;
    // faster nomming if we're damanged
    if(((EntityPlayer) entity).getHealth() < ((EntityPlayer) entity).getMaxHealth()) {
      chance += 0.02;
    }
    // we only eat our tools if the food level is at least 3/4 empty
    if(!foodStats.needFood()) {
      return;
    }
    // more than 5 chickenwings left? we only take a bite randomly
    else if(foodStats.getFoodLevel() > 5 * CHICKENWING) {
      // on average we take a bite every 5 seconds, 0.01 chance (1/(5s * 20 ticks))
      if(random.nextFloat() < chance) {
        nom(tool, (EntityPlayer) entity);
      }
    }
    // less than 5 chickens left? we take a bite out before the situation becomes too.. dire(wolf20)
    else {
      chance += (5 * CHICKENWING - foodStats.getFoodLevel()) * 0.0025f;
      chance -= foodStats.getSaturationLevel() * 0.005f;

      if(random.nextFloat() < chance) {
        nom(tool, (EntityPlayer) entity);
      }
    }
  }

  protected void nom(ItemStack tool, EntityPlayer player) {
    if(ToolHelper.isBroken(tool) || ToolHelper.getCurrentDurability(tool) < NOM_COST) {
      return;
    }

    player.getFoodStats().addStats(1, 0);
    player.getEntityWorld().playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.8f, 1.0f);
    ToolHelper.damageTool(tool, NOM_COST, player);
  }
}
