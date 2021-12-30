package slimeknights.tconstruct.smeltery.block.entity.module.alloying;

/** Interface to keep the two alloying modules consistent */
public interface IAlloyingModule {
  /** Checks if any alloy recipe can be used */
  boolean canAlloy();

  /** Actually performs alloys for the tank */
  void doAlloy();
}
