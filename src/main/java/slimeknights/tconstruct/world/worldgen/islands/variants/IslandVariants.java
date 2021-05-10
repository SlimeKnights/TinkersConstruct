package slimeknights.tconstruct.world.worldgen.islands.variants;

import slimeknights.tconstruct.shared.block.SlimeType;

public class IslandVariants {
  private static final IIslandVariant[] VARIANTS = new IIslandVariant[4];

  public static final IIslandVariant SKY_BLUE = addVariant(new SkySlimeIslandVariant(0, SlimeType.SKY));
  public static final IIslandVariant SKY_GREEN = addVariant(new SkySlimeIslandVariant(1, SlimeType.EARTH));
  public static final IIslandVariant ENDER = addVariant(new EnderSlimeIslandVariant(2));
  public static final IIslandVariant BLOOD = addVariant(new BloodSlimeIslandVariant(3));


  /** Adds a new variant */
  private static IIslandVariant addVariant(IIslandVariant variant) {
    VARIANTS[variant.getIndex()] = variant;
    return variant;
  }

  /** Gets the variant for the given index */
  public static IIslandVariant getVariantFromIndex(int index) {
    if (index >= VARIANTS.length || index < 0) {
      index = 0;
    }
    return VARIANTS[index];
  }
}
