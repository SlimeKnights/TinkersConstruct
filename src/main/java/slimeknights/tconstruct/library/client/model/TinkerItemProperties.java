package slimeknights.tconstruct.library.client.model;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.item.ModifiableLauncherItem;

/** Properties for tinker tools */
public class TinkerItemProperties {
  /** ID for the pull property */
  private static final ResourceLocation PULL_ID = new ResourceLocation("pull");
  /** Property for bow pull amount */
  private static final ItemPropertyFunction PULL = (stack, level, holder, seed) -> {
    if (holder == null || holder.getUseItem() != stack) {
      return 0.0F;
    }
    float drawSpeed = holder.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(ModifiableLauncherItem.DRAWSPEED)).orElse(1/20f);
    return (float)(stack.getUseDuration() - holder.getUseItemRemainingTicks()) * drawSpeed;
  };

  /** ID for the pulling property */
  private static final ResourceLocation PULLING_ID = new ResourceLocation("pulling");
  /** Boolean indicating the bow is pulling */
  private static final ItemPropertyFunction PULLING = (stack, level, holder, seed) -> holder != null && holder.isUsingItem() && holder.getUseItem() == stack ? 1.0F : 0.0F;

  /** Registers properties for a bow */
  public static void registerBowProperties(Item item) {
    ItemProperties.register(item, PULL_ID, PULL);
    ItemProperties.register(item, PULLING_ID, PULLING);
  }
}
