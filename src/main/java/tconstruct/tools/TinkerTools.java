package tconstruct.tools;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.CommonProxy;
import tconstruct.TinkerPulse;
import tconstruct.Util;
import tconstruct.library.tinkering.ToolPart;

@Pulse(id = TinkerTools.PulseId, description = "This module contains all the tools and everything related to it.")
public class TinkerTools extends TinkerPulse {

  public static final String PulseId = "TinkerTools";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "tconstruct.tools.ToolClientProxy", serverSide = "tconstruct.CommonProxy")
  public static CommonProxy proxy;

  // Tools
  public static Item pickaxe;

  // Tool Parts
  public static ToolPart pickHead;

  public static ToolPart toolrod;
  public static ToolPart binding;

  @Handler
  public void preInit(FMLPreInitializationEvent event) {
    TinkerMaterials.registerToolMaterials();
    MinecraftForge.EVENT_BUS.register(this);

    pickHead = registerItem(new ToolPart(), "PickHead");

    toolrod = registerItem(new ToolPart(), "ToolRod");
    binding = registerItem(new ToolPart(), "Binding");

    pickaxe = registerItem(new Item(), "Pickaxe");
  }

  @Handler
  public void init(FMLInitializationEvent event) {

    /*
    ToolPart a, b;
    a = new ToolPart();
    b = new ToolPart();

    GameRegistry.registerItem(a, "ItemA");
    GameRegistry.registerItem(b, "ItemB");

    PartMaterialWrapper c, d;
    c = new PartMaterialWrapper(a, ToolMaterialStats.TYPE);
    d = new PartMaterialWrapper(b, ToolMaterialStats.TYPE);

    TinkersItem testTool = new TestTool(c, d);

    GameRegistry.registerItem(testTool, "TestTool2");

    ItemStack e, f;
    e = new ItemStack(a, 1, TinkerMaterials.stone.metadata);
    f = new ItemStack(b, 1, TinkerMaterials.wood.metadata);

    ItemStack result = testTool.buildItemFromStacks(new ItemStack[]{e, f});
    log.info(result.hasTagCompound());

    TestBlock testBlock = new TestBlock();
    GameRegistry.registerBlock(testBlock, "TestBlock");

    if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && false) {
      Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(testTool,
                                                                             new ItemMeshDefinition() {
                                                                               @Override
                                                                               public ModelResourceLocation getModelLocation(
                                                                                   ItemStack stack) {
                                                                                 return new ModelResourceLocation(
                                                                                     "TConstruct:TestTool2",
                                                                                     "inventory");
                                                                               }
                                                                             });
      Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().registerBlockWithStateMapper(
          testBlock, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_) {
              return new ModelResourceLocation("TConstruct:TestBlock");
            }
          });

    }
    */
  }

  @Handler
  public void postInit(FMLPostInitializationEvent event) {
    //register models
    proxy.registerModels();
  }

  private void registerTools() {

  }
}
