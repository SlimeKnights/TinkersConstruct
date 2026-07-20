package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import slimeknights.mantle.fluid.TextureFluidType;
import slimeknights.mantle.fluid.texture.ClientInvertedFluidType;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.function.Consumer;

/** Fluid Type that does not affect slimes */
public class SlimeFluidType extends TextureFluidType {
  public SlimeFluidType(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canDrownIn(LivingEntity entity) {
    return !entity.getType().is(TinkerTags.EntityTypes.SLIMES);
  }

  public static class Inverted extends SlimeFluidType {
    public Inverted(Properties properties) {
      super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
      consumer.accept(new ClientInvertedFluidType(this));
    }
  }
}
