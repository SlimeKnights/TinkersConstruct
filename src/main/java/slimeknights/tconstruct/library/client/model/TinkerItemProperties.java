package slimeknights.tconstruct.library.client.model;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.ToolActions;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/** Properties for tinker tools */
public class TinkerItemProperties {
  /** ID for broken property */
  private static final ResourceLocation BROKEN_ID = TConstruct.getResource("broken");
  /** Property declaring broken */
  private static final ItemPropertyFunction BROKEN = (stack, level, entity, seed) -> {
    return ToolDamageUtil.isBroken(stack) ? 1 : 0;
  };

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
          return ammo.getString("id").equals(BuiltInRegistries.ITEM.getKey(Items.FIREWORK_ROCKET).toString()) ? 2 : 1;
        }
      }
    }
    return 0;
  };

  /** ID for the pulling property */
  private static final ResourceLocation CHARGING_ID = TConstruct.getResource("charging");
  /** Boolean indicating the bow is pulling */
  private static final ItemPropertyFunction CHARGING = (stack, level, holder, seed) -> {
    if (holder != null && holder.isUsingItem() && holder.getUseItem() == stack) {
      // if boolean is set, change the numbers to remove the arrow
      boolean arrow = ModifierUtil.checkPersistentPresent(stack, ModifiableLauncherItem.KEY_DRAWBACK_AMMO);
      UseAnim anim = stack.getUseAnimation();
      if (anim == UseAnim.BLOCK) {
        return arrow ? 2.5f : 2;
      }
      if (anim != UseAnim.EAT && anim != UseAnim.DRINK) {
        return arrow ? 1.5f : 1;
      }
    }
    return 0;
  };
  /** ID for the pull property */
  private static final ResourceLocation CHARGE_ID = TConstruct.getResource("charge");
  /** Property for bow pull amount */
  private static final ItemPropertyFunction CHARGE = (stack, level, holder, seed) -> {
    if (holder == null || holder.getUseItem() != stack) {
      return 0.0F;
    }
    int drawtime = ModifierUtil.getPersistentInt(stack, GeneralInteractionModifierHook.KEY_DRAWTIME, -1);
    return drawtime == -1 ? 0 : (float)(stack.getUseDuration() - holder.getUseItemRemainingTicks()) / drawtime;
  };
  /** ID for the cast fishing rods */
  private static final ResourceLocation CAST_ID = TConstruct.getResource("cast");
  /** Property for casting a fishing rod */
  private static final ItemPropertyFunction CAST = (stack, level, holder, seed) -> {
    // must be a fishing rod, and the player must be fishing
    // does player check first since its the fastest, avoids NBT parsing
    if (holder instanceof Player player && player.fishing != null && stack.canPerformAction(ToolActions.FISHING_ROD_CAST)) {
      // must be in a hand, but if both hands have fishing rods, must be the one in the main hand
      ItemStack mainhand = holder.getMainHandItem();
      if (mainhand == stack || holder.getOffhandItem() == stack && !mainhand.canPerformAction(ToolActions.FISHING_ROD_CAST)) {
        return 1;
      }
    }
    return 0;
  };

  /** Registers properties for a tool, including the option to have charge/block animations */
  public static void registerBrokenProperty(Item item) {
    ItemProperties.register(item, BROKEN_ID, BROKEN);
  }

  /** Registers properties for a tool, including the option to have charge/block animations */
  public static void registerToolProperties(ItemLike itemlike) {
    Item item = itemlike.asItem();
    registerBrokenProperty(item);
    ItemProperties.register(item, CHARGING_ID, CHARGING);
    ItemProperties.register(item, CHARGE_ID, CHARGE);
    ItemProperties.register(item, CAST_ID, CAST);
  }

  /** Registers properties for a bow */
  public static void registerCrossbowProperties(ItemLike item) {
    registerToolProperties(item);
    ItemProperties.register(item.asItem(), AMMO_ID, AMMO);
  }
}
