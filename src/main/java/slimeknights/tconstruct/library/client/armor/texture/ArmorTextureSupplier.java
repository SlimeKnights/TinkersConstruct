package slimeknights.tconstruct.library.client.armor.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.listener.ResourceValidator;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;

/** Logic to get an armor texture given a stack */
public interface ArmorTextureSupplier extends IHaveLoader {
  /** Folder for tinkers armor, intentionally not the normal armor folder to make the texture scan low risk */
  String FOLDER = "textures/tinker_armor";
  /** Validator checking if armor textures exist */
  ResourceValidator TEXTURE_VALIDATOR = new ResourceValidator(FOLDER, FOLDER, ".png");

  /** Empty module instance. Used as fallback for {@link ConditionalArmorTextureSupplier} modules. Not registered. */
  ArmorTextureSupplier EMPTY = new ArmorTextureSupplier() {
    @Override
    public ArmorTexture getArmorTexture(ItemStack stack, TextureType leggings, RegistryAccess access) {
      return ArmorTexture.EMPTY;
    }

    @Override
    public RecordLoadable<? extends ArmorTextureSupplier> getLoader() {
      throw new UnsupportedOperationException("Cannot datagen an empty texture supplier.");
    }
  };
  /** Registry for resource packs */
  GenericLoaderRegistry<ArmorTextureSupplier> LOADER = new GenericLoaderRegistry<>("Armor texture type", EMPTY, true);

  /** Gets the texture and color to display for the given stack. Use {@link ArmorTexture#EMPTY} to indicates this texture will not render */
  ArmorTexture getArmorTexture(ItemStack stack, TextureType leggings, RegistryAccess access);

  /** Pair of texture and color */
  interface ArmorTexture {
    /** Empty instance since caches don't support caching null. */
    ArmorTexture EMPTY = (model, matrices, bufferSource, packedLight, packedOverlay, red, green, blue, alpha, hasGlint) -> {};

    /** Renders this texture to the given model */
    void renderTexture(Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, boolean hasGlint);
  }

  /** Texture variants, armor is used for helmet, chestplate, and boots, while leggings is leggings and wings is on chest for elytra */
  enum TextureType {
    ARMOR, LEGGINGS, WINGS;

    /** Gets the type for the given slot */
    public static TextureType fromSlot(EquipmentSlot slot) {
      return slot == EquipmentSlot.LEGS ? LEGGINGS : ARMOR;
    }
  }

  /**
   * Gets a texture using the named format
   */
  static ResourceLocation getTexturePath(ResourceLocation name) {
    return new ResourceLocation(name.getNamespace(), FOLDER + '/' + name.getPath() + ".png");
  }
}
