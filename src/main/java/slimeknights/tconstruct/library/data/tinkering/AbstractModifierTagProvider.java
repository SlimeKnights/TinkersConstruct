package slimeknights.tconstruct.library.data.tinkering;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.library.data.AbstractTagProvider;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

/** Tag provider to generate modifier tags */
public abstract class AbstractModifierTagProvider extends AbstractTagProvider<Modifier> {
  protected AbstractModifierTagProvider(DataGenerator generator, String modId, ExistingFileHelper existingFileHelper) {
    super(generator, modId, ModifierManager.TAG_FOLDER, Modifier::getId, id -> ModifierManager.INSTANCE.containsStatic(new ModifierId(id)), existingFileHelper);
  }
}
