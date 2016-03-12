package slimeknights.tconstruct.gadgets;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import slimeknights.tconstruct.common.ClientProxy;

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
  }

  @Override
  public void postInit() {
    super.postInit();

    //MinecraftForge.EVENT_BUS.register(new GadgetClientEvents());
  }
}
