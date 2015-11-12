package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

/**
 * Increases mining speed in water and during rain
 */
public class TraitAquadynamic extends AbstractTrait {

  public TraitAquadynamic() {
    super("aquadynamic", EnumChatFormatting.AQUA);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    float coeff = 1f;
    // is the player in water?
    if(event.entityPlayer.isInWater()) {
      coeff += 5.5f; // being in water causes speed to be 1/5th. These values work fine.
    }
    // is it raining?
    if(event.entityPlayer.worldObj.isRaining()) {
      coeff += event.entityPlayer.worldObj.getBiomeGenForCoords(event.entityPlayer.getPosition()).getFloatRainfall()/1.6f;
    }

    event.newSpeed += event.originalSpeed * coeff;
  }
}
