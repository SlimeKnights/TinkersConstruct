package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

/**
 * Increases mining speed and damage in hot/dry areas, decreases a bit in dry areas
 */
public class TraitAridiculous extends AbstractTrait {

  public TraitAridiculous() {
    super("aridiculous", EnumChatFormatting.DARK_RED);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    // speedup or slowdown depending on biome temperature. hotter areas are much faster
    float coeff = calcAridiculousness(event.entityPlayer.worldObj, event.pos) / 10f; // /10 = 10% for a coeff of 1. But can be bigger.
    event.newSpeed += event.originalSpeed * coeff;
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    float extraDamage = 2f * calcAridiculousness(player.worldObj, player.getPosition());
    return extraDamage + super.onHit(tool, player, target, damage, newDamage, isCritical);
  }

  protected float calcAridiculousness(World world, BlockPos pos) {
    BiomeGenBase biome = world.getBiomeGenForCoords(pos);
    float rain = world.isRaining() ? biome.getFloatRainfall() / 2f : 0f;
    return (float) (Math.pow(1.25, 3d * (0.5f + biome.temperature - biome.rainfall)) - 1.25d) - rain;
  }
}
