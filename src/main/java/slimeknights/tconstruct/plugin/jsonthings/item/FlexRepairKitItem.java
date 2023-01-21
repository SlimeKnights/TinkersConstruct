package slimeknights.tconstruct.plugin.jsonthings.item;

import dev.gigaherz.jsonthings.things.CompletionMode;
import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.StackContext;
import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import lombok.Getter;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.UseAnim;
import slimeknights.tconstruct.tools.item.RepairKitItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Item for custom repair kits */
public class FlexRepairKitItem extends RepairKitItem implements IFlexItem {
  private final Map<String,FlexEventHandler> eventHandlers = new HashMap<>();
  private final Set<CreativeModeTab> tabs = new HashSet<>();

  @Getter
  private final float repairAmount;
  public FlexRepairKitItem(Properties properties, float repairAmount) {
    super(properties);
    this.repairAmount = repairAmount;
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
