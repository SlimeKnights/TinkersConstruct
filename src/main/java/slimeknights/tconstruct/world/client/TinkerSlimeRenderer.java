package slimeknights.tconstruct.world.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import slimeknights.tconstruct.library.Util;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class TinkerSlimeRenderer extends SlimeEntityRenderer {

  private final Identifier texture;
  public TinkerSlimeRenderer(EntityRenderDispatcher renderManagerIn, Identifier texture) {
    super(renderManagerIn);
    this.texture = texture;
  }

  @Override
  public Identifier getTexture(SlimeEntity entity) {
    return texture;
  }
}
