package slimeknights.tconstruct.library.client.particle;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;

import javax.annotation.Nullable;

// not part of the tic particle system since it uses vanilla particles
public class SlimeParticle extends BreakingItemParticle {

  public SlimeParticle(ClientLevel worldIn, double posXIn, double posYIn, double posZIn, ItemStack stack) {
    super(worldIn, posXIn, posYIn, posZIn, stack);
  }

  public SlimeParticle(ClientLevel worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, ItemStack stack) {
    super(worldIn, posXIn, posYIn, posZIn, xSpeedIn, ySpeedIn, zSpeedIn, stack);
  }

  @RequiredArgsConstructor
  public static class Factory implements ParticleProvider<SimpleParticleType> {
    private final ItemLike slime;

    public Factory(SlimeType type) {
      this.slime = TinkerCommons.slimeball.get(type);
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      return new SlimeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, new ItemStack(slime));
    }
  }
}
