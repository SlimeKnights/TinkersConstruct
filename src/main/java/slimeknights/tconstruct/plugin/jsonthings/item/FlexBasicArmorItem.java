package slimeknights.tconstruct.plugin.jsonthings.item;

import dev.gigaherz.jsonthings.things.CompletionMode;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.client.IItemRenderProperties;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.client.TravelersGearModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/** Dyable armor instance for JSON Things */
public class FlexBasicArmorItem extends ModifiableArmorItem implements IFlexItem {
  private final Map<String, FlexEventHandler> eventHandlers = new HashMap<>();
  private final Set<CreativeModeTab> tabs = new HashSet<>();
  private final ResourceLocation name;
  private final boolean dyeable;
  @Nullable
  private final String golden;
  public FlexBasicArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builderIn, ToolDefinition toolDefinition, ResourceLocation name, boolean dyeable, boolean hasGolden) {
    super(materialIn, slot, builderIn, toolDefinition);
    this.name = name;
    this.dyeable = dyeable;
    if (hasGolden) {
      this.golden = name.getNamespace() + ":textures/models/armor/" + name.getPath() + "_golden_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png";
    } else {
      this.golden = null;
    }
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
    if (golden != null && ModifierUtil.getModifierLevel(stack, TinkerModifiers.golden.getId()) > 0) {
      return golden;
    }
    return null;
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


  /* All of these things are handled via modifiers/JSON already, so no-op them */

  @Override
  public void setUseAction(UseAnim useAnim) {}

  @Override
  public UseAnim getUseAction() {
    return UseAnim.NONE;
  }

  @Override
  public void setUseTime(int i) {}

  @Override
  public int getUseTime() {
    return 0;
  }

  @Override
  public void setUseFinishMode(CompletionMode completionMode) {}

  @Override
  public CompletionMode getUseFinishMode() {
    return CompletionMode.USE_ITEM;
  }

  @Override
  public void addAttributeModifier(@Nullable EquipmentSlot equipmentSlot, Attribute attribute, AttributeModifier attributeModifier) {}

  @Override
  public void setLore(List<MutableComponent> list) {}
}
