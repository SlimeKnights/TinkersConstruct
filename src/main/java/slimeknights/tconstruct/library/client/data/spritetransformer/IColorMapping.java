package slimeknights.tconstruct.library.client.data.spritetransformer;

/** Logic to map a color to another color */
@FunctionalInterface
public interface IColorMapping {
  /**
   * Maps the given color
   * @param color  Input color in AABBGGRR format
   * @return New color in AABBGGRR format
   */
  int mapColor(int color);
}
