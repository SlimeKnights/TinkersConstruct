package slimeknights.tconstruct.library.materials.stats;

/**
 * Material stats that support repairing, requires durability as part of the stats
 */
public interface IRepairableMaterialStats extends IMaterialStats {
  /**
   * Gets the amount of durability for this stat type
   * @return  Durability
   */
  int getDurability();
}
