package slimeknights.tconstruct.smeltery;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.client.FaucetRenderer;
import slimeknights.tconstruct.smeltery.client.SmelteryRenderer;
import slimeknights.tconstruct.smeltery.client.TankRenderer;
import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;
import slimeknights.tconstruct.tools.ToolClientEvents;
import slimeknights.tconstruct.tools.block.BlockToolTable;

public class SmelteryClientProxy extends ClientProxy {

  @Override
  public void preInit() {
    super.preInit();

    MinecraftForge.EVENT_BUS.register(new SmelteryClientEvents());
  }

  @Override
  protected void registerModels() {
    // Blocks
    registerItemModel(Item.getItemFromBlock(TinkerSmeltery.smelteryController));
    registerItemModel(Item.getItemFromBlock(TinkerSmeltery.faucet));
    registerItemModel(Item.getItemFromBlock(TinkerSmeltery.smelteryIO));

    // TEs
    ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileSmeltery.class, new SmelteryRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileFaucet.class, new FaucetRenderer());


    // Items
    final ResourceLocation castLoc = SmelteryClientEvents.locBlankCast;
    CustomTextureCreator.castModelLocation = new ResourceLocation(castLoc.getResourceDomain(), "item/" + castLoc.getResourcePath());
    ModelLoader.setCustomMeshDefinition(TinkerSmeltery.cast, new PatternMeshDefinition(castLoc));

    TinkerSmeltery.castCustom.registerItemModels("cast_");

    // universal bucket
    ModelLoader.setBucketModelDefinition(TinkerSmeltery.bucket);
  }
}
