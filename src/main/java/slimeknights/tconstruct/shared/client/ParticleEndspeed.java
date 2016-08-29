package slimeknights.tconstruct.shared.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleEndspeed extends Particle {

  public ParticleEndspeed(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
    super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);

    //this.setAlphaF(0.9f);
    this.setParticleTextureIndex(176);
    this.particleScale = 1f;
    this.particleMaxAge = 20;

    this.motionX = xSpeedIn;
    this.motionY = ySpeedIn;
    this.motionZ = zSpeedIn;
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    //this.particleScale -= 0.0001f;
    this.particleAlpha -= 0.05f;
  }
}
