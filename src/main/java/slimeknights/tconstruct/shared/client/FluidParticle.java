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
    this.setSprite(Minecraft.getInstance().getModelManager().getAtlas(PlayerContainer.BLOCK_ATLAS).getSprite(attributes.getStillTexture(fluid)));
    this.gravity = 1.0F;
    int color = attributes.getColor(fluid);
    this.alpha = ((color >> 24) & 0xFF) / 255f;
    this.rCol   = ((color >> 16) & 0xFF) / 255f;
    this.gCol = ((color >>  8) & 0xFF) / 255f;
    this.bCol  = ( color        & 0xFF) / 255f;
    this.quadSize /= 2.0F;
    this.uCoord = this.random.nextFloat() * 3.0F;
    this.vCoord = this.random.nextFloat() * 3.0F;
  }

  @Override
  public IParticleRenderType getRenderType() {
    return IParticleRenderType.TERRAIN_SHEET;
  }

  @Override
  protected float getU0() {
    return this.sprite.getU((this.uCoord + 1.0F) / 4.0F * 16.0F);
  }

  @Override
  protected float getU1() {
    return this.sprite.getU(this.uCoord / 4.0F * 16.0F);
  }

  @Override
  protected float getV0() {
    return this.sprite.getV(this.vCoord / 4.0F * 16.0F);
  }

  @Override
  protected float getV1() {
    return this.sprite.getV((this.vCoord + 1.0F) / 4.0F * 16.0F);
  }

  @Override
  public int getLightColor(float partialTick) {
    return FluidRenderer.withBlockLight(super.getLightColor(partialTick), fluid.getFluid().getAttributes().getLuminosity(fluid));
  }

  /** Factory to create a fluid particle */
  public static class Factory implements IParticleFactory<FluidParticleData> {
    @Override
    public Particle createParticle(FluidParticleData data, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      FluidStack fluid = data.getFluid();
      return !fluid.isEmpty() ? new FluidParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, fluid) : null;
    }
  }
}
