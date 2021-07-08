package slimeknights.tconstruct.common.registration;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.block.WoodType;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;

import java.util.function.Supplier;

/** Extension of the fence object with all other wood blocks */
public class WoodBlockObject extends FenceBuildingBlockObject {
  @Getter
  private final WoodType woodType;
  // basic
  private final Supplier<? extends Block> log;
  private final Supplier<? extends Block> strippedLog;
  private final Supplier<? extends Block> wood;
  private final Supplier<? extends Block> strippedWood;
  // doors
  private final Supplier<? extends FenceGateBlock> fenceGate;
  private final Supplier<? extends DoorBlock> door;
  private final Supplier<? extends TrapDoorBlock> trapdoor;
  // redstone
  private final Supplier<? extends PressurePlateBlock> pressurePlate;
  private final Supplier<? extends WoodButtonBlock> button;
  // signs
  private final Supplier<? extends StandingSignBlock> sign;
  private final Supplier<? extends WallSignBlock> wallSign;
  // tags
  @Getter
  private final IOptionalNamedTag<Block> logBlockTag;
  @Getter
  private final IOptionalNamedTag<Item> logItemTag;

  public WoodBlockObject(ResourceLocation name, WoodType woodType, BuildingBlockObject planks,
                         Supplier<? extends Block> log, Supplier<? extends Block> strippedLog, Supplier<? extends Block> wood, Supplier<? extends Block> strippedWood,
                         Supplier<? extends FenceBlock> fence, Supplier<? extends FenceGateBlock> fenceGate, Supplier<? extends DoorBlock> door, Supplier<? extends TrapDoorBlock> trapdoor,
                         Supplier<? extends PressurePlateBlock> pressurePlate, Supplier<? extends WoodButtonBlock> button,
                         Supplier<? extends StandingSignBlock> sign, Supplier<? extends WallSignBlock> wallSign) {
    super(planks, fence);
    this.woodType = woodType;
    this.log = log;
    this.strippedLog = strippedLog;
    this.wood = wood;
    this.strippedWood = strippedWood;
    this.fenceGate = fenceGate;
    this.door = door;
    this.trapdoor = trapdoor;
    this.pressurePlate = pressurePlate;
    this.button = button;
    this.sign = sign;
    this.wallSign = wallSign;
    ResourceLocation tagName = new ResourceLocation(name.getNamespace(), name.getPath() + "_logs");
    this.logBlockTag = BlockTags.createOptional(tagName);
    this.logItemTag = ItemTags.createOptional(tagName);
  }

  /** Gets the log for this wood type */
  public Block getLog() {
    return log.get();
  }

  /** Gets the stripped log for this wood type */
  public Block getStrippedLog() {
    return strippedLog.get();
  }

  /** Gets the wood for this wood type */
  public Block getWood() {
    return wood.get();
  }

  /** Gets the stripped wood for this wood type */
  public Block getStrippedWood() {
    return strippedWood.get();
  }

  /* Doors */

  /** Gets the fence gate for this wood type */
  public FenceGateBlock getFenceGate() {
    return fenceGate.get();
  }

  /** Gets the door for this wood type */
  public DoorBlock getDoor() {
    return door.get();
  }

  /** Gets the trapdoor for this wood type */
  public TrapDoorBlock getTrapdoor() {
    return trapdoor.get();
  }

  /* Redstone */

  /** Gets the pressure plate for this wood type */
  public PressurePlateBlock getPressurePlate() {
    return pressurePlate.get();
  }

  /** Gets the button for this wood type */
  public WoodButtonBlock getButton() {
    return button.get();
  }

  /* Signs */

  /* Gets the sign for this wood type, can also be used to get the item */
  public StandingSignBlock getSign() {
    return sign.get();
  }

  /* Gets the wall sign for this wood type */
  public WallSignBlock getWallSign() {
    return wallSign.get();
  }
}
