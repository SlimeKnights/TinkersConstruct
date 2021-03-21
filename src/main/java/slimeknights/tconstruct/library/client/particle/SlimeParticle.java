package slimeknights.tconstruct.library.client.particle;

import net.minecraft.client.particle.BreakingParticle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;

import javax.annotation.Nullable;

// not part of the tic particle system since it uses vanilla particles
@OnlyIn(Dist.CLIENT)
public class SlimeParticle extends BreakingParticle {

  public SlimeParticle(ClientWorld worldIn, double posXIn, double posYIn, double posZIn, ItemStack stack) {
    super(worldIn, posXIn, posYIn, posZIn, stack);
  }

  public SlimeParticle(ClientWorld worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, ItemStack stack) {
    super(worldIn, posXIn, posYIn, posZIn, xSpeedIn, ySpeedIn, zSpeedIn, stack);
  }

  public static class Factory implements IParticleFactory<BasicParticleType> {
    public Factory() {

    }
    @Nullable
    @Override
    public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      return new SlimeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, new ItemStack(TinkerCommons.slimeball.get(SlimeType.SKY)));
    }
  }
}
