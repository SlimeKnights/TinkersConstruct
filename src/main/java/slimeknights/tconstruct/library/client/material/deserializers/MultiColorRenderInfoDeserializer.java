package slimeknights.tconstruct.library.client.material.deserializers;

import slimeknights.tconstruct.library.client.MaterialRenderInfo;

public class MultiColorRenderInfoDeserializer extends AbstractRenderInfoDeserializer {

  protected String dark;
  protected String mid;
  protected String bright;

  @Override
  public MaterialRenderInfo getMaterialRenderInfo() {
    return new MaterialRenderInfo.MultiColor(fromHex(dark), fromHex(mid), fromHex(bright));
  }
}
