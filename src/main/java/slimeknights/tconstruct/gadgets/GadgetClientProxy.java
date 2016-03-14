package slimeknights.tconstruct.gadgets;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.gadgets.client.RenderFancyItemFrame;
import slimeknights.tconstruct.gadgets.entity.EntityFancyItemFrame;
import slimeknights.tconstruct.library.Util;

public class GadgetClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    super.registerModels();

    registerItemModel(Item.getItemFromBlock(TinkerGadgets.stoneTorch));
    registerItemModel(Item.getItemFromBlock(TinkerGadgets.stoneLadder));
    registerItemModel(Item.getItemFromBlock(TinkerGadgets.woodRail));
    registerItemModel(Item.getItemFromBlock(TinkerGadgets.punji));

    registerItemModel(TinkerGadgets.slimeSling);
    registerItemModel(TinkerGadgets.slimeBoots);
    registerItemModel(TinkerGadgets.stoneStick);

    RenderingRegistry.registerEntityRenderingHandler(EntityFancyItemFrame.class, RenderFancyItemFrame.FACTORY);
    for(EntityFancyItemFrame.FrameType type : EntityFancyItemFrame.FrameType.values()) {
      ModelResourceLocation loc = Util.getModelResource("fancy_frame", type.toString());
      ModelLoader.registerItemVariants(TinkerGadgets.fancyFrame, loc);
      ModelLoader.setCustomModelResourceLocation(TinkerGadgets.fancyFrame, type.ordinal(), loc);
    }
  }

  @Override
  public void postInit() {
    super.postInit();

    //MinecraftForge.EVENT_BUS.register(new GadgetClientEvents());
  }
}
