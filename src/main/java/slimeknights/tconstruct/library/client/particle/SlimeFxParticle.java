package slimeknights.tconstruct.library.client.particle;

import net.minecraft.client.particle.BreakingParticle;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// not part of the tic particle system since it uses vanilla particles
@OnlyIn(Dist.CLIENT)
public class SlimeFxParticle extends BreakingParticle {

  public SlimeFxParticle(World worldIn, double posXIn, double posYIn, double posZIn, ItemStack stack) {
    super(worldIn, posXIn, posYIn, posZIn, stack);
  }

  public SlimeFxParticle(World worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, ItemStack stack) {
    super(worldIn, posXIn, posYIn, posZIn, xSpeedIn, ySpeedIn, zSpeedIn, stack);
  }
}
