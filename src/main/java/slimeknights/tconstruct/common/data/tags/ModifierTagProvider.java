package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

public class ModifierTagProvider extends AbstractModifierTagProvider {
  public ModifierTagProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(TinkerTags.Modifiers.GEMS).addOptional(ModifierIds.diamond, ModifierIds.emerald);
    tag(TinkerTags.Modifiers.INVISIBLE_INK_BLACKLIST)
      .add(TinkerModifiers.embellishment.getId(), TinkerModifiers.dyed.getId(), TinkerModifiers.creativeSlot.getId(), TinkerModifiers.statOverride.getId())
      .addOptional(ModifierIds.shiny, TinkerModifiers.golden.getId());
    tag(TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST)
      .add(TinkerModifiers.embellishment.getId(), TinkerModifiers.dyed.getId(), TinkerModifiers.creativeSlot.getId(), TinkerModifiers.statOverride.getId());
    // blacklist modifiers that are not really slotless, they just have a slotless recipe
    tag(TinkerTags.Modifiers.EXTRACT_SLOTLESS_BLACKLIST).add(ModifierIds.luck, ModifierIds.toolBelt);

    // modifiers in this tag support both left click and right click interaction
    tag(TinkerTags.Modifiers.DUAL_INTERACTION)
      .add(TinkerModifiers.bucketing.getId(), TinkerModifiers.spilling.getId(),
           TinkerModifiers.glowing.getId(), TinkerModifiers.firestarter.getId(),
           ModifierIds.stripping, ModifierIds.tilling, ModifierIds.pathing,
           TinkerModifiers.shears.getId(), TinkerModifiers.harvest.getId())
      .addOptional(ModifierIds.pockets);
    tag(TinkerTags.Modifiers.SLIME_DEFENSE)
      .add(TinkerModifiers.meleeProtection.getId(), TinkerModifiers.projectileProtection.getId(),
           ModifierIds.fireProtection, TinkerModifiers.magicProtection.getId(),
           TinkerModifiers.blastProtection.getId(), TinkerModifiers.golden.getId());
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifier Tag Provider";
  }
}
