package slimeknights.tconstruct.tools.data.sprite;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.client.data.material.TrimMaterialPaletteGenerator;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.RecolorSpriteTransformer;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

public class TinkerTrimMaterialPaletteGenerator extends TrimMaterialPaletteGenerator {
  public TinkerTrimMaterialPaletteGenerator(PackOutput packOutput, ExistingFileHelper existingFileHelper, AbstractMaterialSpriteProvider materialProvider) {
    super(packOutput, TConstruct.MOD_ID, existingFileHelper, materialProvider, MaterialIds.TRIM_MATERIALS);
  }

  @Override
  protected ISpriteTransformer getTransformer(MaterialId material) {
    // queens slime is normally a spacially aware generator, use flat colors
    if (MaterialIds.queensSlime.equals(material)) {
      return new RecolorSpriteTransformer(GreyToColorMapping.builderFromBlack().addARGB(63, 0xFF5F1100).addARGB(102, 0xFF893200).addARGB(140, 0xFF966A03).addARGB(178, 0xFF8C9226).addARGB(216, 0xFF52BB53).addARGB(255, 0xFF5DD45F).build());
    }
    if (MaterialIds.knightslime.equals(material)) {
      return new RecolorSpriteTransformer(GreyToColorMapping.builderFromBlack().addARGB(63, 0xFFC882FF).addARGB(102, 0xFF8C44FF).addARGB(140, 0xFF02040C).addARGB(178, 0xFF152237).addARGB(216, 0xFF243366).addARGB(255, 0xFF2C3E7B).build());
    }
    return super.getTransformer(material);
  }
}
