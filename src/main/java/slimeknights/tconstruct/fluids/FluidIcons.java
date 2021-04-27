package slimeknights.tconstruct.fluids;

import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;

public class FluidIcons {

  public static final ResourceLocation LIQUID_STILL = texture("liquid", false);
  public static final ResourceLocation LIQUID_FLOWING = texture("liquid", true);

  public static final ResourceLocation MOLTEN_STILL = texture("molten", false);
  public static final ResourceLocation MOLTEN_FLOWING = texture("molten", true);

  public static final ResourceLocation STONE_STILL = texture("stone", false);
  public static final ResourceLocation STONE_FLOWING = texture("stone", true);

  public static final ResourceLocation BLAZE_STILL = texture("blaze", false);
  public static final ResourceLocation BLAZE_FLOWING = texture("blaze", true);

  public static final ResourceLocation MAGMA_CREAM_STILL = texture("magma_cream", false);
  public static final ResourceLocation MAGMA_CREAM_FLOWING = texture("magma_cream", true);

  public static final ResourceLocation SLIMESTEEL_STILL = texture("slimesteel", false);
  public static final ResourceLocation SLIMESTEEL_FLOWING = texture("slimesteel", true);

  /** Makes a texture for this class */
  private static ResourceLocation texture(String name, boolean flowing) {
    return Util.getResource("block/fluid/" + name + (flowing ? "/flowing" : "/still"));
  }
}
