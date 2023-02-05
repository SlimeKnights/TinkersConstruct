package slimeknights.tconstruct.plugin.jsonthings.item;

import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.client.TravelersGearModel;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/** Dyable armor instance for JSON Things */
public class FlexFlatEmbellishedArmor extends ModifiableArmorItem implements IFlexItem {
  /** Cache of armor texture names */
  private final Map<String,String> ARMOR_TEXTURE_CACHE = new HashMap<>();
  /** Cache of leg texture names */
  private final Map<String,String> LEG_TEXTURE_CACHE = new HashMap<>();
  /** Function to get armor names */
  private final Function<String,String> ARMOR_GETTER = mat -> makeArmorTexture(mat, "layer_1");
  /** Function to get leg names */
  private final Function<String,String> LEG_GETTER = mat -> makeArmorTexture(mat, "layer_2");

  private final Map<String, FlexEventHandler> eventHandlers = new HashMap<>();
  private final Set<CreativeModeTab> tabs = new HashSet<>();
  private final ResourceLocation name;
  private final MaterialId defaultMaterial;
  private final boolean dyeable;
  public FlexFlatEmbellishedArmor(ArmorMaterial materialIn, EquipmentSlot slot, Properties builderIn, ToolDefinition toolDefinition, ResourceLocation name, MaterialId defaultMaterial, boolean dyeable) {
    super(materialIn, slot, builderIn, toolDefinition);
    this.name = name;
    this.defaultMaterial = defaultMaterial;
    this.dyeable = dyeable;
  }

  /** Creates the texture for a regular armor texture */
  private String makeArmorTexture(String material, String texture) {
    ResourceLocation location = ResourceLocation.tryParse(material);
    if (location == null) {
      location = defaultMaterial;
    }
    return String.format("%s:textures/models/armor/%s/%s_%s_%s.png", name.getNamespace(), name.getPath(), texture, location.getNamespace(), location.getPath());
  }

  /** Gets the material from a given stack */
  private String getMaterial(ItemStack stack) {
    if (ModifierUtil.getModifierLevel(stack, TinkerModifiers.golden.getId()) > 0) {
      return MaterialIds.gold.toString();
    }
    String key = ModifierUtil.getPersistentString(stack, TinkerModifiers.embellishment.getId());
    if (key.isEmpty()) {
      return defaultMaterial.toString();
    }
    return key;
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

  @Override
  public void initializeClient(Consumer<IItemRenderProperties> consumer) {
    consumer.accept(new IItemRenderProperties() {
      @Nonnull
      @Override
      public Model getBaseArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
        if (dyeable) {
          return TravelersGearModel.getModel(itemStack, armorSlot, _default, name, true);
        }
        return _default;
      }
    });
  }


  /* JSON things does not use the item properties tab, they handle it via the below method */

  @Override
  public void addCreativeStack(StackContext stackContext, Iterable<CreativeModeTab> tabs) {
    for (CreativeModeTab tab : tabs) {
      this.tabs.add(tab);
    }
  }

  @Override
  protected boolean allowdedIn(CreativeModeTab category) {
    return this.tabs.contains(category);
  }


  /* not honestly sure what events do, but trivial to support */

  @Override
  public void addEventHandler(String name, FlexEventHandler flexEventHandler) {
    this.eventHandlers.put(name, flexEventHandler);
  }

  @Nullable
  @Override
  public FlexEventHandler getEventHandler(String name) {
    return this.eventHandlers.get(name);
  }
}
