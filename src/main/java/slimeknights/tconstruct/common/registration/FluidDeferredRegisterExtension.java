package slimeknights.tconstruct.common.registration;

import slimeknights.mantle.registration.deferred.FluidDeferredRegister;

/** Extension of the fluid register to add a few common tinkers fluid behaviors. */
public class FluidDeferredRegisterExtension extends FluidDeferredRegister {
  public FluidDeferredRegisterExtension(String modID) {
    super(modID);
  }

  /** Registers a slime fluid with slime-like behavior. Slows down the flow rate but doesn't reduce flow speed. */
  public Builder registerSlime(String name) {
    return register(name).tickRate(50);
  }

  /** Registers a fluid with stone-like behavior. Has even less flow distance than lava but flows a bit faster */
  public Builder registerStone(String name) {
    return register(name).levelDecreasePerBlock(2).slopeFindDistance(2).tickRate(40);
  }

  /** Registers a hot fluid with half flow rate. */
  public Builder registerMetal(String name) {
    return register(name).levelDecreasePerBlock(3).slopeFindDistance(3).tickRate(30);
  }

  /** Registers a fluid with gem-like behavior. Flows just the one block */
  public Builder registerGem(String name) {
    return register(name).levelDecreasePerBlock(4).slopeFindDistance(4).tickRate(45);
  }

  /** Registers a fluid with glass-like behavior. Minimal flow distance */
  public Builder registerGlass(String name) {
    return register(name).levelDecreasePerBlock(8).slopeFindDistance(8).tickRate(30);
  }
}
