package slimeknights.tconstruct.tools.traits;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

/**
 * Increases mining speed in water and during rain
 */
public class TraitAquadynamic extends AbstractTrait {

  public TraitAquadynamic() {
    super("aquadynamic", TextFormatting.AQUA);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    float coeff = 1f;
    // is the player in water?
    if(event.getEntityPlayer().isInWater()) {
      coeff += 5.5f; // being in water causes speed to be 1/5th. These values work fine.
    }
    // is it raining?
    if(event.getEntityPlayer().getEntityWorld().isRaining()) {
      coeff += event.getEntityPlayer().getEntityWorld().getBiomeForCoordsBody(event.getEntityPlayer().getPosition()).getRainfall() / 1.6f;
    }

    event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * coeff);
  }
}
