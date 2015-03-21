package tconstruct.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

import tconstruct.CommonProxy;

public class TestClientProxy extends CommonProxy {

  @Override
  public void registerModels() {
    //Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
      //  .register(testItem, 0, new ModelResourceLocation("TConstruct:TestTool", "inventory"));
    registerModel(TinkerTest.testItem, 0, "pick_head", "pick_handle", "pick_binding");

    //ModelBakery.addVariantName(TinkerTest.testItem, "tconstruct:TestTool", "tconstruct:pick_head", "tconstruct:pick_handle",
      //                         "tconstruct:pick_binding");

    //ModelBakery.addVariantName(TinkerTest.testItem, "tconstruct:pick_head", "tconstruct:pick_handle",
      //                       "tconstruct:pick_binding");



    //ModelBakery.addVariantName(TinkerTest.testItem, Item.itemRegistry.getNameForObject(TinkerTest.testItem).toString(), "tconstruct:pick_head", "tconstruct:pick_handle",
      //                     "tconstruct:pick_binding");
  }
}
