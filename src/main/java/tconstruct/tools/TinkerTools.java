package tconstruct.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Logger;

import codechicken.lib.render.ModelRegistryHelper;
import mantle.client.ModelHelper;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.TinkerPulse;
import tconstruct.Util;
import tconstruct.debug.TestBlock;
import tconstruct.debug.TestBlockModel;
import tconstruct.debug.TestTool;
import tconstruct.library.tinkering.PartMaterialWrapper;
import tconstruct.library.tinkering.TinkersItem;
import tconstruct.library.tinkering.ToolPart;
import tconstruct.library.tinkering.materials.ToolMaterialStats;

@Pulse(id = TinkerTools.PulseId, description = "This module contains all the tools and everything related to it.")
public class TinkerTools extends TinkerPulse {

  public static final String PulseId = "TinkerTools";
  static final Logger log = Util.getLogger(PulseId);

  @Handler
  public void preInit(FMLPreInitializationEvent event) {
    TinkerMaterials.registerToolMaterials();
    MinecraftForge.EVENT_BUS.register(this);
  }

  @Handler
  public void init(FMLInitializationEvent event) {
    ToolPart a, b;
    a = new ToolPart();
    b = new ToolPart();

    GameRegistry.registerItem(a, "ItemA");
    GameRegistry.registerItem(b, "ItemB");

    PartMaterialWrapper c, d;
    c = new PartMaterialWrapper(a, ToolMaterialStats.TYPE);
    d = new PartMaterialWrapper(b, ToolMaterialStats.TYPE);

    TinkersItem testTool = new TestTool(c, d);

    GameRegistry.registerItem(testTool, "TestTool");

    ItemStack e, f;
    e = new ItemStack(a, 1, TinkerMaterials.stone.metadata);
    f = new ItemStack(b, 1, TinkerMaterials.wood.metadata);

    ItemStack result = testTool.buildItemFromStacks(new ItemStack[]{e, f});
    log.info(result.hasTagCompound());

    TestBlock testBlock = new TestBlock();
    GameRegistry.registerBlock(testBlock, "TestBlock");

    if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
      Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().registerBlockWithStateMapper(
          testBlock, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_) {
              return new ModelResourceLocation("TConstruct:TestBlock");
            }
          });
    }
  }

  @SubscribeEvent
  public void modelTestStuff(ModelBakeEvent event)
  {
    //event.modelRegistry.putObject(new ModelResourceLocation("TConstruct:TestTool"), new TestModel());
    event.modelRegistry.putObject(new ModelResourceLocation("TConstruct:TestTool", "inventory"), new TestModel(event.modelManager.getTextureMap().getMissingSprite()));
    //event.modelManager.getModel(new ModelResourceLocation("TConstruct:TestTool", "inventory"));

    event.modelRegistry.putObject(new ModelResourceLocation("TConstruct:TestBlock"), new TestBlockModel(event.modelManager.getTextureMap().getMissingSprite()));
  }
}
