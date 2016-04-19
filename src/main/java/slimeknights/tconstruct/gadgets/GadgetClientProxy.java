package slimeknights.tconstruct.gadgets;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import java.util.Locale;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.gadgets.client.RenderFancyItemFrame;
import slimeknights.tconstruct.gadgets.client.RenderThrowball;
import slimeknights.tconstruct.gadgets.entity.EntityFancyItemFrame;
import slimeknights.tconstruct.gadgets.entity.EntityThrowball;
import slimeknights.tconstruct.gadgets.item.ItemThrowball;
import slimeknights.tconstruct.library.Util;

public class GadgetClientProxy extends ClientProxy {

  @Override
  protected void registerModels() {
    super.registerModels();

    // Blocks
    registerItemModel(Item.getItemFromBlock(TinkerGadgets.stoneTorch));
    registerItemModel(Item.getItemFromBlock(TinkerGadgets.stoneLadder));
    registerItemModel(Item.getItemFromBlock(TinkerGadgets.woodRail));
    registerItemModel(Item.getItemFromBlock(TinkerGadgets.punji));
    registerItemModel(Item.getItemFromBlock(TinkerGadgets.rack));
    registerItemBlockMeta(TinkerGadgets.driedClay);


    // Items
    registerItemModel(TinkerGadgets.slimeSling);
    registerItemModel(TinkerGadgets.slimeBoots);
    registerItemModel(TinkerGadgets.stoneStick);

    for(ItemThrowball.ThrowballType type : ItemThrowball.ThrowballType.values()) {
      registerItemModel(TinkerGadgets.throwball, type.ordinal(), type.name().toLowerCase(Locale.US));
    }

    // Entity
    RenderingRegistry.registerEntityRenderingHandler(EntityFancyItemFrame.class, RenderFancyItemFrame.FACTORY);

    for(EntityFancyItemFrame.FrameType type : EntityFancyItemFrame.FrameType.values()) {
      ModelResourceLocation loc = Util.getModelResource("fancy_frame", type.toString());
      ModelLoader.registerItemVariants(TinkerGadgets.fancyFrame, loc);
      ModelLoader.setCustomModelResourceLocation(TinkerGadgets.fancyFrame, type.ordinal(), loc);
    }
    RenderingRegistry.registerEntityRenderingHandler(EntityThrowball.class, RenderThrowball.FACTORY);
  }

  @Override
  public void postInit() {
    super.postInit();

    MinecraftForge.EVENT_BUS.register(new GadgetClientEvents());
  }
}
