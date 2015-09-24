package slimeknights.tconstruct.tools.modifiers;

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
    double coeff = ((Math.pow(1.25, 3d*biome.temperature) - 1.25d) / 10d);
    event.newSpeed += event.originalSpeed * coeff;
  }
}
