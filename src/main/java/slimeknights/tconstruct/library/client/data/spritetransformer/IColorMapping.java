package slimeknights.tconstruct.library.client.data.spritetransformer;


import slimeknights.mantle.data.gson.GenericRegisteredSerializer;
import slimeknights.mantle.data.gson.GenericRegisteredSerializer.IJsonSerializable;

/** Logic to map a color to another color */
public interface IColorMapping extends IJsonSerializable {
  /** Serializer used for this transformer, can register your deserializers with it */
  GenericRegisteredSerializer<IColorMapping> SERIALIZER = new GenericRegisteredSerializer<>();

  /**
   * Maps the given color
   * @param color  Input color in AABBGGRR format
   * @return New color in AABBGGRR format
   */
  int mapColor(int color);
}
