package slimeknights.tconstruct.world;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.EntityIDs;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.world.block.BlockSlimeDirt;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;
import slimeknights.tconstruct.world.block.BlockSlimeLeaves;
import slimeknights.tconstruct.world.block.BlockSlimeSapling;
import slimeknights.tconstruct.world.block.BlockSlimeVine;
import slimeknights.tconstruct.world.block.BlockTallSlimeGrass;
import slimeknights.tconstruct.world.entity.EntityBlueSlime;
import slimeknights.tconstruct.world.item.ItemBlockLeaves;
import slimeknights.tconstruct.world.worldgen.MagmaSlimeIslandGenerator;
import slimeknights.tconstruct.world.worldgen.SlimeIslandGenerator;

@Pulse(id = TinkerWorld.PulseId, description = "Everything that's found in the world and worldgen")
public class TinkerWorld extends TinkerPulse {

  public static final String PulseId = "TinkerWorld";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.world.WorldClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static BlockSlimeDirt slimeDirt;
  public static BlockSlimeGrass slimeGrass;
  public static BlockSlimeLeaves slimeLeaves;
  public static BlockTallSlimeGrass slimeGrassTall;
  public static BlockSlimeSapling slimeSapling;
  public static BlockSlimeVine slimeVineBlue1;
  public static BlockSlimeVine slimeVinePurple1;
  public static BlockSlimeVine slimeVineBlue2;
  public static BlockSlimeVine slimeVinePurple2;
  public static BlockSlimeVine slimeVineBlue3;
  public static BlockSlimeVine slimeVinePurple3;

  public static final EnumPlantType slimePlantType = EnumPlantType.getPlantType("slime");

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();

    slimeDirt = registerBlock(registry, new BlockSlimeDirt(), "slime_dirt");
    slimeGrass = registerBlock(registry, new BlockSlimeGrass(), "slime_grass");
    slimeLeaves = registerBlock(registry, new BlockSlimeLeaves(), "slime_leaves");
    slimeGrassTall = registerBlock(registry, new BlockTallSlimeGrass(), "slime_grass_tall");
    slimeSapling = registerBlock(registry, new BlockSlimeSapling(), "slime_sapling");

    slimeVineBlue3 = registerBlock(registry, new BlockSlimeVine(BlockSlimeGrass.FoliageType.BLUE, null), "slime_vine_blue_end");
    slimeVineBlue2 = registerBlock(registry, new BlockSlimeVine(BlockSlimeGrass.FoliageType.BLUE, slimeVineBlue3), "slime_vine_blue_mid");
    slimeVineBlue1 = registerBlock(registry, new BlockSlimeVine(BlockSlimeGrass.FoliageType.BLUE, slimeVineBlue2), "slime_vine_blue");

    slimeVinePurple3 = registerBlock(registry, new BlockSlimeVine(BlockSlimeGrass.FoliageType.PURPLE, null), "slime_vine_purple_end");
    slimeVinePurple2 = registerBlock(registry, new BlockSlimeVine(BlockSlimeGrass.FoliageType.PURPLE, slimeVinePurple3), "slime_vine_purple_mid");
    slimeVinePurple1 = registerBlock(registry, new BlockSlimeVine(BlockSlimeGrass.FoliageType.PURPLE, slimeVinePurple2), "slime_vine_purple");
  }

  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();

    slimeDirt = registerEnumItemBlock(registry, slimeDirt);
    slimeGrass = registerItemBlockProp(registry, new ItemBlockMeta(slimeGrass), BlockSlimeGrass.TYPE);
    slimeLeaves = registerItemBlockProp(registry, new ItemBlockLeaves(slimeLeaves), BlockSlimeGrass.FOLIAGE);
    slimeGrassTall = registerItemBlockProp(registry, new ItemBlockMeta(slimeGrassTall), BlockTallSlimeGrass.TYPE);
    slimeSapling = registerItemBlockProp(registry, new ItemBlockMeta(slimeSapling), BlockSlimeGrass.FOLIAGE);

    slimeVineBlue3 = registerItemBlock(registry, slimeVineBlue3);
    slimeVineBlue2 = registerItemBlock(registry, slimeVineBlue2);
    slimeVineBlue1 = registerItemBlock(registry, slimeVineBlue1);

    slimeVinePurple3 = registerItemBlock(registry, slimeVinePurple3);
    slimeVinePurple2 = registerItemBlock(registry, slimeVinePurple2);
    slimeVinePurple1 = registerItemBlock(registry, slimeVinePurple1);
  }

  @SubscribeEvent
  public void registerEntities(Register<EntityEntry> event) {
    EntityRegistry.registerModEntity(Util.getResource("blueslime"), EntityBlueSlime.class, Util.prefix("blueslime"), EntityIDs.BLUESLIME, TConstruct.instance, 64, 5, true, 0x47eff5, 0xacfff4);
    LootTableList.register(EntityBlueSlime.LOOT_TABLE);
    //EntitySpawnPlacementRegistry.setPlacementType(EntityBlueSlime.class, EntityLiving.SpawnPlacementType.IN_WATER);
  }

  @SubscribeEvent
  public void registerModels(ModelRegistryEvent event) {
    proxy.registerModels();
  }

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    proxy.preInit();
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    proxy.init();
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    GameRegistry.registerWorldGenerator(SlimeIslandGenerator.INSTANCE, 25);
    GameRegistry.registerWorldGenerator(MagmaSlimeIslandGenerator.INSTANCE, 25);

    MinecraftForge.EVENT_BUS.register(new WorldEvents());

    proxy.postInit();

    TinkerRegistry.tabWorld.setDisplayIcon(new ItemStack(slimeSapling));
  }
}
