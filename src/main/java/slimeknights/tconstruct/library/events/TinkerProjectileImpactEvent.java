package slimeknights.tconstruct.library.events;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

import javax.annotation.Nonnull;

public class TinkerProjectileImpactEvent extends ProjectileImpactEvent {

  @Nonnull
  private final ItemStack tool;

  public TinkerProjectileImpactEvent(Entity entity, RayTraceResult ray, @Nonnull ItemStack tool) {
    super(entity, ray);
    this.tool = tool.copy();
  }

  @Nonnull
  public ItemStack getTool() {
    return tool;
  }
}
