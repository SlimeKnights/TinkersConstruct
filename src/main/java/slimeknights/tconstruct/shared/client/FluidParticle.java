package slimeknights.tconstruct.shared.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.tconstruct.shared.particle.FluidParticleData;

/** Particle type that renders a fluid still texture */
public class FluidParticle extends SpriteTexturedParticle {
  private final FluidStack fluid;
  private final float uCoord;
  private final float vCoord;
  protected FluidParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, FluidStack fluid) {
    super(world, x, y, z, motionX, motionY, motionZ);
    this.fluid = fluid;
    FluidAttributes attributes = fluid.getFluid().getAttributes();
    this.setSprite(Minecraft.getInstance().getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(attributes.getStillTexture(fluid)));
    this.particleGravity = 1.0F;
    int color = attributes.getColor(fluid);
    this.particleAlpha = ((color >> 24) & 0xFF) / 255f;
    this.particleRed   = ((color >> 16) & 0xFF) / 255f;
    this.particleGreen = ((color >>  8) & 0xFF) / 255f;
    this.particleBlue  = ( color        & 0xFF) / 255f;
    this.particleScale /= 2.0F;
    this.uCoord = this.rand.nextFloat() * 3.0F;
    this.vCoord = this.rand.nextFloat() * 3.0F;
  }

  @Override
  public IParticleRenderType getRenderType() {
    return IParticleRenderType.TERRAIN_SHEET;
  }

  @Override
  protected float getMinU() {
    return this.sprite.getInterpolatedU((this.uCoord + 1.0F) / 4.0F * 16.0F);
  }

  @Override
  protected float getMaxU() {
    return this.sprite.getInterpolatedU(this.uCoord / 4.0F * 16.0F);
  }

  @Override
  protected float getMinV() {
    return this.sprite.getInterpolatedV(this.vCoord / 4.0F * 16.0F);
  }

  @Override
  protected float getMaxV() {
    return this.sprite.getInterpolatedV((this.vCoord + 1.0F) / 4.0F * 16.0F);
  }

  @Override
  public int getBrightnessForRender(float partialTick) {
    return FluidRenderer.withBlockLight(super.getBrightnessForRender(partialTick), fluid.getFluid().getAttributes().getLuminosity(fluid));
  }

  /** Factory to create a fluid particle */
  public static class Factory implements IParticleFactory<FluidParticleData> {
    @Override
    public Particle makeParticle(FluidParticleData data, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      FluidStack fluid = data.getFluid();
      return !fluid.isEmpty() ? new FluidParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, fluid) : null;
    }
  }
}
