package slimeknights.tconstruct.library.client.armor.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.listener.ResourceValidator;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;

/** Logic to get an armor texture given a stack */
public interface ArmorTextureSupplier extends IHaveLoader {
  /** Folder for tinkers armor, intentionally not the normal armor folder to make the texture scan low risk */
  String FOLDER = "textures/tinker_armor";
  /** Validator checking if armor textures exist */
  ResourceValidator TEXTURE_VALIDATOR = new ResourceValidator(FOLDER, FOLDER, ".png");

  /** Registry for Json Things */
  GenericLoaderRegistry<ArmorTextureSupplier> LOADER = new GenericLoaderRegistry<>("Armor texture type", true);

  /** Gets the texture and color to display for the given stack. Use {@link ArmorTexture#EMPTY} to indicates this texture will not render */
  ArmorTexture getArmorTexture(ItemStack stack, TextureType leggings, RegistryAccess access);

  /** Pair of texture and color */
  interface ArmorTexture {
    /** Empty instance since caches don't support caching null. */
    ArmorTexture EMPTY = new ArmorTexture() {
      @Override
      public void renderTexture(Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, boolean hasGlint) {}
    };

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
