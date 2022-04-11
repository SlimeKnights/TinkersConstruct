package slimeknights.tconstruct.tools.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** This item applies the correct texture for a given material */
public class SlimesuitItem extends ModifiableArmorItem {
  /** Cache of armor texture names */
  private static final Map<String,String> ARMOR_TEXTURE_CACHE = new HashMap<>();
  /** Cache of leg texture names */
  private static final Map<String,String> LEG_TEXTURE_CACHE = new HashMap<>();
  /** Function to get armor names */
  private static final Function<String,String> ARMOR_GETTER = mat -> makeArmorTexture(mat, "layer_1");
  /** Function to get leg names */
  private static final Function<String,String> LEG_GETTER = mat -> makeArmorTexture(mat, "layer_2");

  public SlimesuitItem(ModifiableArmorMaterial material, ArmorSlotType slotType, Properties properties) {
    super(material, slotType, properties);
  }

  /** Gets the material from a given stack */
  public static String getMaterial(ItemStack stack) {
    if (ModifierUtil.getModifierLevel(stack, TinkerModifiers.golden.getId()) > 0) {
      return MaterialIds.gold.toString();
    }
    String key = ModifierUtil.getPersistentString(stack, TinkerModifiers.embellishment.getId());
    if (key.isEmpty()) {
      return MaterialIds.enderslime.toString();
    }
    return key;
  }

  /** Creates the texture for a regular armor texture */
  public static String makeArmorTexture(String material, String texture) {
    ResourceLocation location = ResourceLocation.tryParse(material);
    if (location == null) {
      location = MaterialIds.enderslime;
    }
    return String.format("%s:textures/models/armor/slime/%s_%s_%s.png", TConstruct.MOD_ID, texture, location.getNamespace(), location.getPath());
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
    String material = getMaterial(stack);
    if (slot == EquipmentSlot.LEGS) {
      return LEG_TEXTURE_CACHE.computeIfAbsent(material, LEG_GETTER);
    }
    return ARMOR_TEXTURE_CACHE.computeIfAbsent(material, ARMOR_GETTER);
  }
}
