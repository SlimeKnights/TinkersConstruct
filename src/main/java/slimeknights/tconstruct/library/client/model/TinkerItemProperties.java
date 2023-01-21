package slimeknights.tconstruct.library.client.model;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.item.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.item.ModifiableCrossbowItem;

import java.util.Objects;

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

  /** ID for ammo property */
  private static final ResourceLocation AMMO_ID = TConstruct.getResource("ammo");
  /** Int declaring ammo type */
  private static final ItemPropertyFunction AMMO = (stack, level, entity, seed) -> {
    CompoundTag nbt = stack.getTag();
    if (nbt != null) {
      CompoundTag persistentData = nbt.getCompound(ToolStack.TAG_PERSISTENT_MOD_DATA);
      if (!persistentData.isEmpty()) {
        CompoundTag ammo = persistentData.getCompound(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO.toString());
        if (!ammo.isEmpty()) {
          // no sense having two keys for ammo, just set 1 for arrow, 2 for fireworks
          return ammo.getString("id").equals(Objects.requireNonNull(Items.FIREWORK_ROCKET.getRegistryName()).toString()) ? 2 : 1;
        }
      }
    }
    return 0;
  };

  /** Registers properties for a bow */
  public static void registerBowProperties(Item item) {
    ItemProperties.register(item, PULL_ID, PULL);
    ItemProperties.register(item, PULLING_ID, PULLING);
  }

  /** Registers properties for a bow */
  public static void registerCrossbowProperties(Item item) {
    registerBowProperties(item);
    ItemProperties.register(item, AMMO_ID, AMMO);
  }
}
