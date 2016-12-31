package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

/**
 * Increases mining speed and damage in hot/dry areas, decreases a bit in wet areas
 */
public class TraitAridiculous extends AbstractTrait {

  public TraitAridiculous() {
    super("aridiculous", TextFormatting.DARK_RED);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    // speedup or slowdown depending on biome temperature. hotter areas are much faster
    float coeff = calcAridiculousness(event.getEntityPlayer().getEntityWorld(), event.getPos()) / 10f; // /10 = 10% for a coeff of 1. But can be bigger.
    event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * coeff);
  }

  @Override
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    float extraDamage = 2f * calcAridiculousness(player.getEntityWorld(), player.getPosition());
    return extraDamage + super.damage(tool, player, target, damage, newDamage, isCritical);
  }

  protected float calcAridiculousness(World world, BlockPos pos) {
    Biome biome = world.getBiomeForCoordsBody(pos);
    float rain = world.isRaining() ? biome.getRainfall() / 2f : 0f;
    return (float) (Math.pow(1.25, 3d * (0.5f + biome.getTemperature() - biome.getRainfall())) - 1.25d) - rain;
  }
}
