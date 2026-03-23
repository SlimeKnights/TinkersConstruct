package slimeknights.tconstruct.tools.data;

import net.minecraft.data.PackOutput;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.DyedArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.FirstArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.FixedArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.MaterialArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.MaterialHasFallbackTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.TrimArmorTextureSupplier;
import slimeknights.tconstruct.library.client.data.AbstractArmorModelProvider;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.tools.ArmorDefinitions;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class ArmorModelProvider extends AbstractArmorModelProvider {
  public ArmorModelProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  protected void addModels() {
    ModifierId dyed = TinkerModifiers.dyed.getId();
    addModel(ArmorDefinitions.TRAVELERS, name -> new ArmorTextureSupplier[] {
      FixedArmorTextureSupplier.builder(name, "/base_").build(),
      new FirstArmorTextureSupplier(
        new DyedArmorTextureSupplier(name, "/cuirass_", dyed, null),
        new MaterialArmorTextureSupplier.Material(name, "/cuirass_", 1)
      ),
      new MaterialArmorTextureSupplier.Material(name, "/metal_", 0),
      TrimArmorTextureSupplier.INSTANCE
    });
    addModel(ArmorDefinitions.PLATE, name -> new ArmorTextureSupplier[] {
      new MaterialArmorTextureSupplier.Material(name, "/plating_", 0),
      new FirstArmorTextureSupplier(
        new MaterialHasFallbackTextureSupplier(1, new DyedArmorTextureSupplier(name.withSuffix("/maille_"), "_metal", dyed, null, 0), "metal", "metal_contrast"),
        new DyedArmorTextureSupplier(name.withSuffix("/maille_"), "_cloth", dyed, null, 0),
        new MaterialArmorTextureSupplier.Material(name, "/maille_", 1)
      ),
      TrimArmorTextureSupplier.INSTANCE
    });
    addModel(ArmorDefinitions.SLIMESUIT, name -> new ArmorTextureSupplier[] {
      new MaterialArmorTextureSupplier.Material(name, "/", 1),
      TrimArmorTextureSupplier.INSTANCE
    });
    addModel(TConstruct.getResource("slimelytra"),
      new MaterialArmorTextureSupplier.Material(TConstruct.getResource("slime"), "/", 0),
      TrimArmorTextureSupplier.INSTANCE
    );
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Armor Models";
  }
}
