package slimeknights.tconstruct.library.tools.part;

/** Interface to allow creating additional repair kits */
public interface IRepairKitItem extends IMaterialItem {
  /** Gets the amount repaired by this kit in terms of ingots. Separate from the actual cost to create the kit */
  float getRepairAmount();
}
