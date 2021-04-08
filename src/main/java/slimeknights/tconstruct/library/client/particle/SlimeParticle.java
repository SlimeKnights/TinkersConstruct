package slimeknights.tconstruct.library.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;

import javax.annotation.Nullable;

// not part of the tic particle system since it uses vanilla particles
@Environment(EnvType.CLIENT)
public class SlimeParticle extends CrackParticle {

  public SlimeParticle(ClientWorld worldIn, double posXIn, double posYIn, double posZIn, ItemStack stack) {
    super(worldIn, posXIn, posYIn, posZIn, stack);
  }

  public SlimeParticle(ClientWorld worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, ItemStack stack) {
    super(worldIn, posXIn, posYIn, posZIn, xSpeedIn, ySpeedIn, zSpeedIn, stack);
  }

  public static class Factory implements ParticleFactory<DefaultParticleType> {
    public Factory() {

    }
    @Nullable
    @Override
    public Particle makeParticle(DefaultParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      return new SlimeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, new ItemStack(TinkerCommons.slimeball.get(SlimeType.SKY)));
    }
  }
}
