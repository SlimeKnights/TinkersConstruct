package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitAridiculous extends AbstractTrait {

  public TraitAridiculous() {
    super("aridiculous", EnumChatFormatting.DARK_RED);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    BiomeGenBase biome = event.entityPlayer.worldObj.getBiomeGenForCoords(event.pos);
    // speedup or slowdown depending on biome temperature. hotter areas are much faster
    float coeff = calcAridiculousness(biome) / 10f; // /10 = 10% for a coeff of 1. But can be bigger.
    event.newSpeed += event.originalSpeed * coeff;
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(player.getPosition());
    float extraDamage = 2f * calcAridiculousness(biome);
    return extraDamage + super.onHit(tool, player, target, damage, newDamage, isCritical);
  }

  protected float calcAridiculousness(BiomeGenBase biome) {
    return (float) (Math.pow(1.25, 3d * (biome.temperature - biome.rainfall)) - 1.25d);
  }
}
