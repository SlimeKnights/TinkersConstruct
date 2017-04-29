package slimeknights.tconstruct.smeltery.events;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import slimeknights.tconstruct.library.events.TinkerEvent;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.smeltery.tileentity.TileCasting;

public class TinkerCastingEvent extends TinkerEvent {

  public final ICastingRecipe recipe;
  public final TileCasting tile;

  public TinkerCastingEvent(ICastingRecipe recipe, TileCasting tile) {
    this.recipe = recipe;
    this.tile = tile;
  }


  /**
   * Fired when a casting block wants to begins casting
   * Can be cancelled to prevent the casting
   */
  @Cancelable
  public static class OnCasting extends TinkerCastingEvent {

    public OnCasting(ICastingRecipe recipe, TileCasting tile) {
      super(recipe, tile);
    }

    public static boolean fire(ICastingRecipe recipe, TileCasting tile) {
      OnCasting event = new OnCasting(recipe, tile);
      MinecraftForge.EVENT_BUS.post(event);
      return !event.isCanceled();
    }
  }

  public static class OnCasted extends TinkerCastingEvent {

    public ItemStack output;
    public boolean consumeCast;
    public boolean switchOutputs;

    public OnCasted(ICastingRecipe recipe, TileCasting tile) {
      super(recipe, tile);
      ItemStack cast = tile.getStackInSlot(0);
      assert tile.tank.getFluid() != null;
      Fluid fluid = tile.tank.getFluid().getFluid();
      this.output = recipe.getResult(cast, fluid).copy();
      this.consumeCast = recipe.consumesCast();
      this.switchOutputs = recipe.switchOutputs();
    }

    public static OnCasted fire(ICastingRecipe recipe, TileCasting tile) {
      OnCasted event = new OnCasted(recipe, tile);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }
  }
}
