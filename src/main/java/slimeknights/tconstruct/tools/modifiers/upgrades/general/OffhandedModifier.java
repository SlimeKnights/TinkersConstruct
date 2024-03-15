package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.Lazy;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.Arrays;
import java.util.List;

public class OffhandedModifier extends Modifier implements VolatileDataModifierHook {
  private final Lazy<Component> noHandedName = Lazy.of(() -> applyStyle(Component.translatable(getTranslationKey() + ".2")));
  private final Lazy<List<Component>> noHandedDescription = Lazy.of(() -> Arrays.asList(
    Component.translatable(getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
    Component.translatable(getTranslationKey() + ".description.2")));

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.VOLATILE_DATA);
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    volatileData.putBoolean(IModifiable.DEFER_OFFHAND, true);
    if (modifier.getLevel() > 1) {
      volatileData.putBoolean(IModifiable.NO_INTERACTION, true);
    }
  }

  @Override
  public Component getDisplayName(int level) {
    if (level > 1) {
      return noHandedName.get();
    }
    return super.getDisplayName();
  }

  @Override
  public List<Component> getDescriptionList(int level) {
    if (level > 1) {
      return noHandedDescription.get();
    }
    return super.getDescriptionList();
  }
}
