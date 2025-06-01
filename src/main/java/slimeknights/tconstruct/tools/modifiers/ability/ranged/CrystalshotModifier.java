package slimeknights.tconstruct.tools.modifiers.ability.ranged;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.behavior.InfinityModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.CrystalshotItem;

import javax.annotation.Nullable;

/** @deprecated use {@link InfinityModule} and {@link slimeknights.tconstruct.library.modifiers.modules.display.ModifierVariantColorModule} */
@Deprecated(forRemoval = true)
public class CrystalshotModifier extends NoLevelsModifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(new InfinityModule(new ItemStack(TinkerTools.crystalshotItem), CrystalshotItem.TAG_VARIANT,4, false));
  }

  @Override
  public int getPriority() {
    return 60; // after trick quiver, before bulk quiver, can't go after bulk due to desire to use inventory
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    // color the display name for the variant
    String variant = tool.getPersistentData().getString(getId());
    if (!variant.isEmpty()) {
      String key = getTranslationKey();
      return Component.translatable(getTranslationKey())
        .withStyle(style -> style.withColor(ResourceColorManager.getTextColor(key + "." + variant)));
    }
    return super.getDisplayName();
  }
}
