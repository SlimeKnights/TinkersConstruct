package slimeknights.tconstruct.common.registration;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.PressurePlateBlock.Sensitivity;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SignItem;
import net.minecraft.item.TallBlockItem;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import slimeknights.mantle.registration.deferred.BlockDeferredRegister;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.shared.item.BurnableBlockItem;
import slimeknights.tconstruct.shared.item.BurnableTallBlockItem;
import slimeknights.tconstruct.world.block.StrippableLogBlock;
import slimeknights.tconstruct.world.block.WoodenDoorBlock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDeferredRegisterExtension extends BlockDeferredRegister {
  /** List of all sign blocks to inject into the sign tile entity */
  private final Collection<Supplier<? extends Block>> signBlocks = new ArrayList<>();

  public BlockDeferredRegisterExtension(String modID) {
    super(modID);
  }

  /**
   * Creates a new metal item object
   * @param name           Metal name
   * @param tagName        Name to use for tags for this block
   * @param blockSupplier  Supplier for the block
   * @param blockItem      Block item
   * @param itemProps      Properties for the item
   * @return  Metal item object
   */
  public MetalItemObject registerMetal(String name, String tagName, Supplier<Block> blockSupplier, Function<Block,? extends BlockItem> blockItem, Item.Properties itemProps) {
    ItemObject<Block> block = register(name + "_block", blockSupplier, blockItem);
    Supplier<Item> itemSupplier = () -> new Item(itemProps);
    RegistryObject<Item> ingot = itemRegister.register(name + "_ingot", itemSupplier);
    RegistryObject<Item> nugget = itemRegister.register(name + "_nugget", itemSupplier);
    return new MetalItemObject(tagName, block, ingot, nugget);
  }

  /**
   * Creates a new metal item object
   * @param name           Metal name
   * @param blockSupplier  Supplier for the block
   * @param blockItem      Block item
   * @param itemProps      Properties for the item
   * @return  Metal item object
   */
  public MetalItemObject registerMetal(String name, Supplier<Block> blockSupplier, Function<Block,? extends BlockItem> blockItem, Item.Properties itemProps) {
    return registerMetal(name, name, blockSupplier, blockItem, itemProps);
  }

  /**
   * Creates a new metal item object
   * @param name        Metal name
   * @param tagName     Name to use for tags for this block
   * @param blockProps  Properties for the block
   * @param blockItem   Block item
   * @param itemProps   Properties for the item
   * @return  Metal item object
   */
  public MetalItemObject registerMetal(String name, String tagName, AbstractBlock.Properties blockProps, Function<Block,? extends BlockItem> blockItem, Item.Properties itemProps) {
    return registerMetal(name, tagName, () -> new Block(blockProps), blockItem, itemProps);
  }

  /**
   * Creates a new metal item object
   * @param name        Metal name
   * @param blockProps  Properties for the block
   * @param blockItem   Block item
   * @param itemProps   Properties for the item
   * @return  Metal item object
   */
  public MetalItemObject registerMetal(String name, AbstractBlock.Properties blockProps, Function<Block,? extends BlockItem> blockItem, Item.Properties itemProps) {
    return registerMetal(name, name, blockProps, blockItem, itemProps);
  }

  /**
   * Registers a new wood object
   * @param name             Name of the wood object
   * @param planksMaterial   Material for the planks
   * @param planksColor      Map color for the planks
   * @param plankSound       Sound for the planks
   * @param planksTool       Tool for the planks
   * @param barkMaterial     Bark material
   * @param barkColor        Map color for the bark
   * @param barkSound        Sound for the bark
   * @param group            Item group
   * @return Wood object
   */
  public WoodBlockObject registerWood(String name, Material planksMaterial, MaterialColor planksColor, SoundType plankSound, ToolType planksTool, Material barkMaterial, MaterialColor barkColor, SoundType barkSound, ItemGroup group) {
    WoodType woodType = WoodType.create(resourceName(name));
    WoodType.register(woodType);
    Item.Properties itemProps = new Item.Properties().group(group);

    // many of these are already burnable via tags, but simplier to set them all here
    Function<Integer, Function<? super Block, ? extends BlockItem>> burnableItem;
    Function<Integer, Function<? super Block, ? extends BlockItem>> burnableTallItem;
    if (barkMaterial.isFlammable()) {
      burnableItem     = burnTime -> block -> new BurnableBlockItem(block, itemProps, burnTime);
      burnableTallItem = burnTime -> block -> new BurnableTallBlockItem(block, itemProps, burnTime);
    } else {
      Function<? super Block, ? extends BlockItem> defaultItemBlock = block -> new BlockItem(block, itemProps);
      burnableItem = burnTime -> defaultItemBlock;
      burnableTallItem = burnTime -> block -> new TallBlockItem(block, itemProps);
    }

    // planks
    Function<? super Block, ? extends BlockItem> burnable300 = burnableItem.apply(300);
    AbstractBlock.Properties planksProps = AbstractBlock.Properties.create(planksMaterial, planksColor).harvestTool(planksTool).hardnessAndResistance(2.0f, 3.0f).sound(plankSound);
    BuildingBlockObject planks = registerBuilding(name + "_planks", planksProps, burnable300);
    ItemObject<FenceBlock> fence = register(name + "_fence", () -> new FenceBlock(Properties.from(planks.get())), burnable300);
    // logs and wood
    Supplier<? extends RotatedPillarBlock> stripped = () -> new RotatedPillarBlock(AbstractBlock.Properties.create(planksMaterial, planksColor).harvestTool(planksTool).hardnessAndResistance(2.0f).sound(plankSound));
    ItemObject<RotatedPillarBlock> strippedLog = register("stripped_" + name + "_log", stripped, burnable300);
    ItemObject<RotatedPillarBlock> strippedWood = register("stripped_" + name + "_wood", stripped, burnable300);
    ItemObject<RotatedPillarBlock> log = register(name + "_log", () -> new StrippableLogBlock(strippedLog,
      AbstractBlock.Properties.create(barkMaterial, state -> state.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? planksColor : barkColor)
                              .harvestTool(ToolType.AXE).hardnessAndResistance(2.0f).sound(barkSound)), burnable300);
    ItemObject<RotatedPillarBlock> wood = register(name + "_wood", () -> new StrippableLogBlock(strippedWood, AbstractBlock.Properties.create(barkMaterial, barkColor).harvestTool(ToolType.AXE).hardnessAndResistance(2.0f).sound(barkSound)), burnable300);

    // doors
    ItemObject<DoorBlock> door = register(name + "_door", () -> new WoodenDoorBlock(AbstractBlock.Properties.create(planksMaterial, planksColor).harvestTool(planksTool).hardnessAndResistance(3.0F).sound(plankSound).notSolid()), burnableTallItem.apply(200));
    ItemObject<TrapDoorBlock> trapdoor = register(name + "_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.create(planksMaterial, planksColor).harvestTool(planksTool).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid().setAllowsSpawn(Blocks::neverAllowSpawn)), burnable300);
    ItemObject<FenceGateBlock> fenceGate = register(name + "_fence_gate", () -> new FenceGateBlock(planksProps), burnable300);
    // redstone
    AbstractBlock.Properties redstoneProps = AbstractBlock.Properties.create(planksMaterial, planksColor).harvestTool(planksTool).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(plankSound);
    ItemObject<PressurePlateBlock> pressurePlate = register(name + "_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, redstoneProps), burnable300);
    ItemObject<WoodButtonBlock> button = register(name + "_button", () -> new WoodButtonBlock(redstoneProps), burnableItem.apply(100));
    // signs
    RegistryObject<StandingSignBlock> standingSign = registerNoItem(name + "_sign", () -> new StandingSignBlock(AbstractBlock.Properties.create(planksMaterial, planksColor).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(plankSound), woodType));
    RegistryObject<WallSignBlock> wallSign = registerNoItem(name + "_wall_sign", () -> new WallSignBlock(AbstractBlock.Properties.create(planksMaterial, planksColor).doesNotBlockMovement().hardnessAndResistance(1.0F).sound(plankSound).lootFrom(standingSign), woodType));
    signBlocks.add(standingSign);
    signBlocks.add(wallSign);
    // sign is included automatically in asItem of the standing sign
    this.itemRegister.register(name + "_sign", () -> new SignItem(new Item.Properties().maxStackSize(16).group(group), standingSign.get(), wallSign.get()));
    // finally, return
    return new WoodBlockObject(resource(name), woodType, planks, log, strippedLog, wood, strippedWood, fence, fenceGate, door, trapdoor, pressurePlate, button, standingSign, wallSign);
  }

  /**
   * Runs the given consumer on each registered sign block
   * @param consumer  Consumer to run
   */
  public void forEachSignBlock(Consumer<? super Block> consumer) {
    signBlocks.forEach(sup -> consumer.accept(sup.get()));
  }
}
