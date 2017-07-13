package slimeknights.tconstruct.library.client.material.deserializers;

import net.minecraft.util.ResourceLocation;

import slimeknights.tconstruct.library.client.MaterialRenderInfo;

public class BlockRenderInfoDeserializer extends AbstractRenderInfoDeserializer {

  protected String texture;

  @Override
  public MaterialRenderInfo getMaterialRenderInfo() {
    return new MaterialRenderInfo.BlockTexture(new ResourceLocation(texture));
  }
}
