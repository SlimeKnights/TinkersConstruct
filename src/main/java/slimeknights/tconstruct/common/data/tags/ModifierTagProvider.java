package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;
import slimeknights.tconstruct.tools.data.ModifierIds;

public class ModifierTagProvider extends AbstractModifierTagProvider {
  public ModifierTagProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(TinkerTags.Modifiers.GEMS).addOptional(ModifierIds.diamond, ModifierIds.emerald);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifier Tag Provider";
  }
}
