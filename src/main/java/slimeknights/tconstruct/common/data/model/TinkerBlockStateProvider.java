package slimeknights.tconstruct.common.data.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.client.model.builder.ColoredModelBuilder;
import slimeknights.mantle.client.model.builder.ConnectedModelBuilder;
import slimeknights.mantle.client.model.builder.MantleItemLayerBuilder;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.function.Function;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;
import static slimeknights.mantle.util.IdExtender.INSTANCE;

@SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
public class TinkerBlockStateProvider extends BlockStateProvider {
  private final UncheckedModelFile GENERATED = new UncheckedModelFile("item/generated");

  public TinkerBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    addFenceBuildingBlock(TinkerMaterials.blazewood, "block/wood/blazewood/", "planks", blockTexture("wood/blazewood"));
    addFenceBuildingBlock(TinkerMaterials.nahuatl, "block/wood/nahuatl/", "planks", blockTexture("wood/nahuatl"));
    addWood(TinkerWorld.greenheart, false, RenderType.cutout());
    addWood(TinkerWorld.skyroot, true, RenderType.cutout());
    addWood(TinkerWorld.bloodshroom, true, RenderType.cutout());
    addWood(TinkerWorld.enderbark, true, RenderType.solid());
    basicBlock(TinkerWorld.enderbarkRoots.get(), models().withExistingParent("block/wood/enderbark/roots/empty", "block/mangrove_roots")
                                                         .renderType(RenderType.cutout().name)
                                                         .texture("side", blockTexture("wood/enderbark/roots"))
                                                         .texture("top", blockTexture("wood/enderbark/roots_top")));
    TinkerWorld.slimyEnderbarkRoots.forEach((type, block) -> {
      String name = type.getSerializedName();
      cubeColumn(block, "block/wood/enderbark/roots/" + name, blockTexture("wood/enderbark/roots/" + name), blockTexture("wood/enderbark/roots/" + name + "_top"));
    });

    // clear glass
    glassBlock(TinkerCommons.clearGlass.get(), TinkerCommons.clearGlassPane.get(), "clear_glass/", TConstruct.getResource("block/clear_glass"), -1, true, null);
    ResourceLocation clearStainedGlass = TConstruct.getResource("block/clear_stained_glass");
    RenderType translucent = RenderType.translucent();
    for (GlassColor color : GlassColor.values()) {
      glassBlock(TinkerCommons.clearStainedGlass.get(color), TinkerCommons.clearStainedGlassPane.get(color), "clear_glass/" + color.getSerializedName() + "/", clearStainedGlass, 0xFF000000 | color.getColor(), false, translucent);
    }
    glassBlock(TinkerCommons.soulGlass.get(), TinkerCommons.soulGlassPane.get(), "soul_glass/", TConstruct.getResource("block/soul_glass"), -1, false, translucent);
    // smeltery glass - share a common top texture
    glassBlock(TinkerSmeltery.searedGlass.get(),     TinkerSmeltery.searedGlassPane.get(),     "smeltery/glass/", TConstruct.getResource("block/smeltery/seared_glass"), -1, true, null);
    glassBlock(TinkerSmeltery.searedSoulGlass.get(), TinkerSmeltery.searedSoulGlassPane.get(), "smeltery/soul_glass/",
               TConstruct.getResource("block/smeltery/soul_glass"), TConstruct.getResource("block/smeltery/seared_glass_top"), -1, true, translucent);
    glassBlock(TinkerSmeltery.scorchedGlass.get(),     TinkerSmeltery.scorchedGlassPane.get(),     "foundry/glass/", TConstruct.getResource("block/foundry/glass"), -1, true, null);
    glassBlock(TinkerSmeltery.scorchedSoulGlass.get(), TinkerSmeltery.scorchedSoulGlassPane.get(), "foundry/soul_glass/",
               TConstruct.getResource("block/foundry/soul_glass"), TConstruct.getResource("block/foundry/glass_top"), -1, true, translucent);
    // obsidian pane
    ResourceLocation obsidian = new ResourceLocation("block/obsidian");
    paneBlock(TinkerCommons.obsidianPane.get(), "obsidian_pane/", obsidian, obsidian, false, -1, false, RenderType.solid());
  }


  /* Helpers */

  /** Creates a texture in the block folder */
  protected ResourceLocation blockTexture(String path) {
    return new ResourceLocation(TConstruct.MOD_ID, BLOCK_FOLDER + "/" + path);
  }

  /** Creates a texture in the block folder */
  protected ResourceLocation itemTexture(String path) {
    return new ResourceLocation(TConstruct.MOD_ID, ModelProvider.ITEM_FOLDER + "/" + path);
  }

  /** Creates all models for a building block object */
  protected void addBuildingBlock(BuildingBlockObject block, String folder, String name, ResourceLocation texture) {
    ModelFile blockModel = basicBlock(block.get(), folder + name, texture);
    slab(block.getSlab(), folder + "slab", blockModel, texture, texture, texture);
    stairs(block.getStairs(), folder + "stairs", texture, texture, texture);
  }

  /** Creates all models for a building block object */
  protected void addFenceBuildingBlock(FenceBuildingBlockObject block, String folder, String name, ResourceLocation texture) {
    addBuildingBlock(block, folder, name, texture);
    fence(block.getFence(), folder + "fence/", texture);
  }

  /** Creates all models for the given wood block object */
  protected void addWood(WoodBlockObject wood, boolean trapdoorOrientable, RenderType doorRenderType) {
    String plankPath = wood.getId().getPath();
    String name = plankPath.substring(0, plankPath.length() - "_planks".length());
    String folder = "block/wood/" + name + "/"; // forge model providers do not prefix with block if you have / in the path
    // helper to get textures for wood, since we put them in a nice folder
    Function<String,ResourceLocation> texture = suffix -> blockTexture("wood/" + name + "/" + suffix);
    ResourceLocation planks = texture.apply("planks");

    // planks and fences
    addFenceBuildingBlock(wood, folder, "planks", planks);
    fenceGate(wood.getFenceGate(), folder + "fence/gate", planks);
    // logs
    axisBlock(wood.getLog(),          folder + "log/log",           texture.apply("log"), true);
    axisBlock(wood.getStrippedLog(),  folder + "log/stripped",      texture.apply("stripped_log"), true);
    axisBlock(wood.getWood(),         folder + "log/wood",          texture.apply("log"), false);
    axisBlock(wood.getStrippedWood(), folder + "log/wood_stripped", texture.apply("stripped_log"), false);
    // doors
    door(wood.getDoor(), folder, doorRenderType, texture.apply("door_bottom"), texture.apply("door_top"));
    basicItem(wood.getDoor(), "wood/");
    trapdoor(wood.getTrapdoor(), folder + "trapdoor_", texture.apply("trapdoor"), trapdoorOrientable);
    // redstone
    pressurePlate(wood.getPressurePlate(), folder + "pressure_plate", planks);
    button(wood.getButton(), folder + "button", planks);
    // sign
    signBlock(wood.getSign(), wood.getWallSign(), models().sign(folder + "sign", planks));
    basicItem(wood.getSign(), "wood/");
  }


  /* forge seems to think all block models should be in the root folder. That's dumb, so we get to copy and paste their builders when its not practical to adapt */

  /** Gets the resource location key for a block */
  @SuppressWarnings("deprecation")
  private ResourceLocation key(Block block) {
    return Registry.BLOCK.getKey(block);
  }

  /** Gets the resource path for a block */
  private String name(Block block) {
    return key(block).getPath();
  }

  /** Gets the resource location key for a block */
  @SuppressWarnings("deprecation")
  private ResourceLocation itemKey(ItemLike item) {
    return Registry.ITEM.getKey(item.asItem());
  }

  /** Gets the resource location key for a block */
  private String itemName(ItemLike item) {
    return itemKey(item).getPath();
  }

  /** Creates a model for a generated item with 1 layer */
  protected ItemModelBuilder basicItem(ItemLike item, String texturePrefix) {
    return basicItem(itemKey(item), texturePrefix);
  }

  /** Creates a model for a generated item with 1 layer */
  protected ItemModelBuilder basicItem(ResourceLocation item, String texturePrefix) {
    return itemModels().getBuilder(item.toString()).parent(GENERATED).texture("layer0", itemTexture(texturePrefix + item.getPath()));
  }

  /**
   * Creates a model for a block with a simple model
   * @param block   Block
   * @param model   Model to use for the block and item
   * @return model file for the basic block
   */
  public ModelFile basicBlock(Block block, ModelFile model) {
    simpleBlock(block, model);
    simpleBlockItem(block, model);
    return model;
  }

  /**
   * Creates a model for a cube with the same texture on all sides and an item form
   * @param block     Block
   * @param location  Location for the block model
   * @param texture   Texture for all sides
   * @return model file for the basic block
   */
  public ModelFile basicBlock(Block block, String location, ResourceLocation texture) {
    return basicBlock(block, models().cubeAll(location, texture));
  }

  /**
   * Creates a model for a cube with the same texture on all sides and an item form
   * @param block     Block
   * @param location  Location for the block model
   * @param side      Texture for sides
   * @param top       Texture for top
   * @return model file for the basic block
   */
  public ModelFile cubeColumn(Block block, String location, ResourceLocation side, ResourceLocation top) {
    return basicBlock(block, models().cubeColumn(location, side, top));
  }

  /**
   * Adds a block with axis textures
   * @param block        Block to add, expected to be instance of RotatedPillarBlock
   * @param location     Location for the model
   * @param texture      Side texture
   * @param horizontal   If true, makes a top texture by suffixing the side texture and includes a horizontal model.
   *                     If false, uses the side for the top
   */
  public void axisBlock(Block block, String location, ResourceLocation texture, boolean horizontal) {
    ResourceLocation endTexture = horizontal ? INSTANCE.suffix(texture, "_top") : texture;
    ModelFile model = models().cubeColumn(TConstruct.resourceString(location), texture, endTexture);
    axisBlock((RotatedPillarBlock)block, model,
              horizontal ? models().cubeColumnHorizontal(TConstruct.resourceString(location + "_horizontal"), texture, endTexture) : model);
    simpleBlockItem(block, model);
  }

  /**
   * Creates block and item model for a slab
   * @param block           Slab block
   * @param location        Location for slab models, top slab will suffix top
   * @param doubleModel     Model for the double slab
   * @param sideTexture     Side texture
   * @param bottomTexture   Bottom texture
   * @param topTexture      Top texture
   */
  public void slab(SlabBlock block, String location, ModelFile doubleModel, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
    ModelFile slab = models().slab(location, sideTexture, bottomTexture, topTexture);
    slabBlock(
      block, slab,
      models().slabTop(location + "_top", sideTexture, bottomTexture, topTexture),
      doubleModel);
    simpleBlockItem(block, slab);
  }

  /**
   * Creates block and item model for stairs
   * @param block           Stairs block
   * @param location        Location for stair models, inner and outer will suffix
   * @param sideTexture     Side texture
   * @param bottomTexture   Bottom texture
   * @param topTexture      Top texture
   */
  public void stairs(StairBlock block, String location, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
    ModelFile stairs = models().stairs(location, sideTexture, bottomTexture, topTexture);
    stairsBlock(
      block, stairs,
      models().stairsInner(location + "_inner", sideTexture, bottomTexture, topTexture),
      models().stairsOuter(location + "_outer", sideTexture, bottomTexture, topTexture));
    simpleBlockItem(block, stairs);
  }
  /**
   * Adds a fence block with an item model
   * @param block    Fence block
   * @param prefix   Prefix for block files
   * @param texture  Fence texture
   */
  public void fence(FenceBlock block, String prefix, ResourceLocation texture) {
    fourWayBlock(
      block,
      models().fencePost(prefix + "post", texture),
      models().fenceSide(prefix + "side", texture));
    itemModels().withExistingParent(itemName(block), "minecraft:block/fence_inventory").texture("texture", texture);
  }

  public void fenceGate(FenceGateBlock block, String baseName, ResourceLocation texture) {
    ModelFile model = models().fenceGate(baseName, texture);
    fenceGateBlock(
      block, model,
      models().fenceGateOpen(baseName + "_open", texture),
      models().fenceGateWall(baseName + "_wall", texture),
      models().fenceGateWallOpen(baseName + "_wall_open", texture));
    simpleBlockItem(block, model);
  }

  /**
   * Adds a door block without an item model
   * @param block           Door block
   * @param prefix          Prefix for model files
   * @param doorRenderType  Render type to use for door models
   * @param bottomTexture   Bottom door texture
   * @param topTexture      Top door texture
   */
  public void door(DoorBlock block, String prefix, RenderType doorRenderType, ResourceLocation bottomTexture, ResourceLocation topTexture) {
    doorBlock(
      block,
      models().doorBottomLeft(     prefix + "door/bottom_left",       bottomTexture, topTexture)
              .renderType(doorRenderType.name),
      models().doorBottomLeftOpen( prefix + "door/bottom_left_open",  bottomTexture, topTexture)
              .renderType(doorRenderType.name),
      models().doorBottomRight(    prefix + "door/bottom_right",      bottomTexture, topTexture)
              .renderType(doorRenderType.name),
      models().doorBottomRightOpen(prefix + "door/bottom_right_open", bottomTexture, topTexture)
              .renderType(doorRenderType.name),
      models().doorTopLeft(        prefix + "door/top_left",          bottomTexture, topTexture)
              .renderType(doorRenderType.name),
      models().doorTopLeftOpen(    prefix + "door/top_left_open",     bottomTexture, topTexture)
              .renderType(doorRenderType.name),
      models().doorTopRight(       prefix + "door/top_right",         bottomTexture, topTexture)
              .renderType(doorRenderType.name),
      models().doorTopRightOpen(   prefix + "door/top_right_open",    bottomTexture, topTexture)
              .renderType(doorRenderType.name)
    );
  }

  /**
   * Adds a trapdoor block with an item model
   * @param block    Trapdoor block
   * @param prefix   Model location prefix
   * @param texture  Trapdoor texture
   * @param orientable  If true, it's an oriented model.
   */
  public void trapdoor(TrapDoorBlock block, String prefix, ResourceLocation texture, boolean orientable) {
    ModelFile bottom, top, open;

    if (orientable) {
      bottom = models().trapdoorOrientableBottom(prefix + "bottom", texture).renderType(RenderType.cutout().name);
      top = models().trapdoorOrientableTop(prefix + "top", texture).renderType(RenderType.cutout().name);
      open = models().trapdoorOrientableOpen(prefix + "open", texture).renderType(RenderType.cutout().name);
    } else {
      bottom = models().trapdoorBottom(prefix + "bottom", texture).renderType(RenderType.cutout().name);
      top = models().trapdoorTop(prefix + "top", texture).renderType(RenderType.cutout().name);
      open = models().trapdoorOpen(prefix + "open", texture).renderType(RenderType.cutout().name);
    }

    trapdoorBlock(block, bottom, top, open, orientable);
    simpleBlockItem(block, bottom);
  }

  /**
   * Adds a pressure plate with item model
   * @param block     Pressure plate block
   * @param location  Location for the model, pressed will be the location suffixed with down
   * @param texture   Texture for the plate
   */
  public void pressurePlate(PressurePlateBlock block, String location, ResourceLocation texture) {
    ModelFile pressurePlate = models().pressurePlate(location, texture);
    pressurePlateBlock(block, pressurePlate, models().pressurePlateDown(location + "_down", texture));
    simpleBlockItem(block, pressurePlate);
  }

  /**
   * Adds a button with item model
   * @param block     Button block
   * @param location  Location for the model, pressed will be the location suffixed with down
   * @param texture   Texture for the button
   */
  public void button(ButtonBlock block, String location, ResourceLocation texture) {
    ModelFile button = models().button(location, texture);
    buttonBlock(block, button, models().buttonPressed(location + "_pressed", texture));
    itemModels().withExistingParent(itemName(block), "minecraft:block/button_inventory").texture("texture", texture);
  }


  /* Panes and glass */

  /** Creates a pane model using the TConstruct templates */
  private BlockModelBuilder paneModel(String baseName, String variant, ResourceLocation pane, @Nullable ResourceLocation edge, @Nullable RenderType renderType, boolean connected, int tint) {
    BlockModelBuilder builder = models().withExistingParent(BLOCK_FOLDER + "/" + baseName + variant, TConstruct.getResource("block/template/pane/" + variant));
    builder.texture("pane", pane);
    if (edge != null) {
      builder.texture("edge", edge);
    }
    if (renderType != null) {
      builder.renderType(renderType.name);
    }
    if (connected) {
      ConnectedModelBuilder<BlockModelBuilder> cBuilder = builder.customLoader(ConnectedModelBuilder::new);
      cBuilder.connected("pane", "cornerless_full").setPredicate("pane");
      if (tint != -1) {
        cBuilder.color(tint);
      }
    } else if (tint != -1) {
      builder.customLoader(ColoredModelBuilder::new).color(tint);
    }
    return builder;
  }

  /** Creates a new pane block state */
  private void paneBlockWithEdge(IronBarsBlock block, ModelFile post, ModelFile side, ModelFile sideAlt, ModelFile noSide, ModelFile noSideAlt, ModelFile noSideEdge) {
    MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
      .part().modelFile(post).addModel().end();
    PipeBlock.PROPERTY_BY_DIRECTION.forEach((dir, value) -> {
      if (dir.getAxis().isHorizontal()) {
        boolean alt = dir == Direction.SOUTH;
        builder.part().modelFile(alt || dir == Direction.WEST ? sideAlt : side).rotationY(dir.getAxis() == Axis.X ? 90 : 0).addModel()
               .condition(value, true).end()
               .part().modelFile(alt || dir == Direction.EAST ? noSideAlt : noSide).rotationY(dir == Direction.WEST ? 270 : dir == Direction.SOUTH ? 90 : 0).addModel()
               .condition(value, false).end()
               .part().modelFile(noSideEdge).rotationY((int)dir.getOpposite().toYRot()).addModel()
               .condition(value, false)
               .condition(PipeBlock.PROPERTY_BY_DIRECTION.get(dir.getClockWise()), false)
               .condition(PipeBlock.PROPERTY_BY_DIRECTION.get(dir.getCounterClockWise()), false).end();
      }
    });
  }

  /** Creates a new pane block with all relevant models */
  public void paneBlock(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge, boolean connected, int tint, boolean solidEdge, @Nullable RenderType renderType) {
    // build block models
    ModelFile post      = paneModel(baseName, "post",       pane, edge, renderType, connected, tint);
    ModelFile side      = paneModel(baseName, "side",       pane, edge, renderType, connected, tint);
    ModelFile sideAlt   = paneModel(baseName, "side_alt",   pane, edge, renderType, connected, tint);
    ModelFile noSide    = paneModel(baseName, "noside",     pane, null, renderType, connected, tint);
    ModelFile noSideAlt = paneModel(baseName, "noside_alt", pane, null, renderType, connected, tint);
    if (solidEdge && !pane.equals(edge)) {
      ModelFile noSideEdge = paneModel(baseName, "noside_edge", pane, edge, renderType, false, tint);
      paneBlockWithEdge(block, post, side, sideAlt, noSide, noSideAlt, noSideEdge);
    } else {
      paneBlock(block, post, side, sideAlt, noSide, noSideAlt);
    }
    // build item model
    ItemModelBuilder item = itemModels().getBuilder(itemKey(block).toString()).parent(GENERATED).texture("layer0", pane);
    if (tint != -1) {
      item.customLoader(MantleItemLayerBuilder::new).color(tint);
    }
    if (renderType != null) {
      item.renderType(renderType.name);
    }
  }

  /** Adds models for a glass block with a glass pane */
  public void glassBlock(Block glass, IronBarsBlock pane, String baseName, ResourceLocation front, int tint, boolean solidEdge, @Nullable RenderType renderType) {
    glassBlock(glass, pane, baseName, front, INSTANCE.suffix(front, "_top"), tint, solidEdge, renderType);
  }

  /** Adds models for a glass block with a glass pane */
  public void glassBlock(Block glass, IronBarsBlock pane, String baseName, ResourceLocation front, ResourceLocation edge, int tint, boolean solidEdge, @Nullable RenderType renderType) {
    // make block model
    BlockModelBuilder block = models().cubeAll(BLOCK_FOLDER + "/" + baseName + "block", front);
    ConnectedModelBuilder<BlockModelBuilder> cBuilder = block.customLoader(ConnectedModelBuilder::new);
    cBuilder.connected("all", "cornerless_full");
    if (tint != -1) {
      cBuilder.color(tint);
    }
    if (renderType != null) {
      block.renderType(renderType.name);
    } else {
      // glass generally wants cutout
      block.renderType(RenderType.cutout().name);
    }
    basicBlock(glass, block);
    // make pane models
    paneBlock(pane, baseName + "pane_", front, edge, true, tint, solidEdge, renderType);
  }
}
