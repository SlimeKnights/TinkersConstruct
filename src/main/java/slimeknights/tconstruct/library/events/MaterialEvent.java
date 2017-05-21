package slimeknights.tconstruct.library.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;

import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;

public abstract class MaterialEvent extends TinkerEvent {

  public final Material material;

  public MaterialEvent(Material material) {
    this.material = material;
  }


  /**
   * Fired when a new material is registered. Is cancelable, allows you to prevent the material from getting registered.
   */
  @Cancelable
  public static class MaterialRegisterEvent extends MaterialEvent {

    public MaterialRegisterEvent(Material material) {
      super(material);
    }
  }

  /**
   * Fired when a stat type is getting added to a material. Allows you to overwrite the result.
   * If Result is ALLOW, newStats will be added to the material instead.
   */
  @HasResult
  public static class StatRegisterEvent<T extends IMaterialStats> extends MaterialEvent {

    public final T stats;
    public T newStats;

    public StatRegisterEvent(Material material, T stats) {
      super(material);
      this.stats = stats;
    }

    public void overrideResult(T newStats) {
      if(!stats.getIdentifier().equals(newStats.getIdentifier())) {
        TinkerRegistry.log.error("StatRegisterEvent: New stats don't match old stats type. New is {}, old was {}",
                                 newStats.getIdentifier(), stats.getIdentifier());
        return;
      }

      this.newStats = newStats;
      this.setResult(Result.ALLOW);
    }
  }

  /**
   * Fired when a trait is added to a material. If cancelled the trait will not be added.
   */
  @Cancelable
  public static class TraitRegisterEvent<T extends ITrait> extends MaterialEvent {

    public final T trait;

    public TraitRegisterEvent(Material material, T trait) {
      super(material);
      this.trait = trait;
    }
  }

  /**
   * Fired when a material should be integrated the default way. If cancelled the integration will not take place.
   */
  @Cancelable
  public static class IntegrationEvent extends MaterialEvent {

    public final MaterialIntegration materialIntegration;

    public IntegrationEvent(Material material, MaterialIntegration materialIntegration) {
      super(material);
      this.materialIntegration = materialIntegration;
    }
  }
}
