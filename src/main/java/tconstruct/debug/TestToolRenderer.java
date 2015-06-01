package tconstruct.debug;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

import tconstruct.library.Util;

public class TestToolRenderer implements ItemMeshDefinition {

  @Override
  public ModelResourceLocation getModelLocation(ItemStack stack) {
    return new ModelResourceLocation(Util.resource("models/test.obj"));
  }
}
