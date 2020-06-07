package slimeknights.tconstruct.library.registration.object;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumBuildingBlockObject<T extends Enum<T>> {

  private Map<T,BuildingBlockObject> map;

  public EnumBuildingBlockObject(Map<T,BuildingBlockObject> map) {
    this.map = map;
  }

  /**
   * Gets a block supplier for the given value
   * @param value  Value to get
   * @return  BlockItemObject
   */
  public BuildingBlockObject getBuilding(T value) {
    return map.get(value);
  }

  /**
   * Gets the block for the given value
   * @param value  Value to get
   * @return  Block instance
   */
  public Block get(T value) {
    if (!map.containsKey(value)) {
      return null;
    }
    return getBuilding(value).get();
  }

  /**
   * Gets the slab for the given value
   * @param value  Value to get
   * @return  Slab instance
   */
  public SlabBlock getSlab(T value) {
    if (!map.containsKey(value)) {
      return null;
    }
    return getBuilding(value).getSlab();
  }

  /**
   * Gets the stairs for the given value
   * @param value  Value to get
   * @return  Stairs instance
   */
  public StairsBlock getStairs(T value) {
    if (!map.containsKey(value)) {
      return null;
    }
    return getBuilding(value).getStairs();
  }

  /**
   * Checks if the given block is in the list
   * @param block  Block to check
   * @return  True if its in the list
   */
  public boolean containsBlock(Block block) {
    return this.map.values().stream().map(BuildingBlockObject::get).anyMatch(block::equals);
  }

  /**
   * Checks if the given slab is in the list
   * @param block  Block to check
   * @return  True if its in the list
   */
  public boolean containsSlab(Block block) {
    return this.map.values().stream().map(BuildingBlockObject::getSlab).anyMatch(block::equals);
  }

  /**
   * Checks if the given stairs is in the list
   * @param block  Block to check
   * @return  True if its in the list
   */
  public boolean containsStairs(Block block) {
    return this.map.values().stream().map(BuildingBlockObject::getStairs).anyMatch(block::equals);
  }

  /**
   * Checks if the given block is in the block, slab, or stairs lists
   * @param block  Block to check
   * @return  True if its in the list
   */
  public boolean contains(Block block) {
    return this.containsBlock(block) || this.containsSlab(block) || this.containsStairs(block);
  }

  /**
   * Gets all blocks contained in this object
   * @return  Blocks contained in this object
   */
  public List<Block> values() {
    return this.map.values().stream().map(BuildingBlockObject::values).flatMap(Collection::stream).collect(Collectors.toList());
  }

  /**
   * Gets all base blocks contained in this object
   * @return  Base blocks contained in this object
   */
  public List<Block> blockValues() {
    return this.map.values().stream().map(BuildingBlockObject::get).collect(Collectors.toList());
  }
}
