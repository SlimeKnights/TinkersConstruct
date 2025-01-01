package slimeknights.tconstruct.library.data.tinkering;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.library.data.AbstractTagProvider;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;

/** Tag provider for materials */
public abstract class AbstractMaterialTagProvider extends AbstractTagProvider<IMaterial> {
  protected AbstractMaterialTagProvider(PackOutput packOutput, String modId, ExistingFileHelper existingFileHelper) {
    super(packOutput, modId, MaterialManager.TAG_FOLDER, IMaterial::getIdentifier, id -> true, existingFileHelper);
  }
}
