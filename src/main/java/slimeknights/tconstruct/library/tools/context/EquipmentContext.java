package slimeknights.tconstruct.library.tools.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentIterator.EquipmentEntry;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static slimeknights.tconstruct.common.TinkerTags.Items.MODIFIABLE;

/** Context for a modifier hook that runs on multiple equipment slots */
@RequiredArgsConstructor
public class EquipmentContext {
  /** Entity who changed equipment */
  @Getter
  private final LivingEntity entity;
  /** Determines if the tool in the given slot was fetched */
  protected final boolean[] fetchedTool = new boolean[6];
  /** Array of tools currently on the entity */
  protected final IToolStackView[] toolsInSlots = new IToolStackView[6];
  /** Cached tinker data capability, saves capability lookup times slightly */
  private LazyOptional<TinkerDataCapability.Holder> tinkerData = null;

  /** Creates a context with an existing tool instance */
  public static EquipmentContext withTool(LivingEntity living, IToolStackView tool, EquipmentSlot slot) {
    EquipmentContext context = new EquipmentContext(living);
    int index = slot.getFilterFlag();
    context.toolsInSlots[index] = tool;
    context.fetchedTool[index] = true;
    return context;
  }

  /** Gets a tool stack if the stack is modifiable, null otherwise */
  @Nullable
  protected static IToolStackView getToolStackIfModifiable(ItemStack stack) {
    if (!stack.isEmpty() && stack.is(MODIFIABLE)) {
      return ToolStack.from(stack);
    }
    return null;
  }

  /** Gets the level for this context */
  public Level getLevel() {
    return entity.level();
  }

  /**
   * Gets the tool stack in the given slot
   * @param slotType  Slot type
   * @return  Tool stack in the given slot, or null if the slot is not modifiable
   */
  @Nullable
  public IToolStackView getToolInSlot(EquipmentSlot slotType) {
    int index = slotType.getFilterFlag();
    if (!fetchedTool[index]) {
      toolsInSlots[index] = getToolStackIfModifiable(entity.getItemBySlot(slotType));
      fetchedTool[index] = true;
    }
    return toolsInSlots[index];
  }

  /** Same as {@link #getToolInSlot(EquipmentSlot)}, but validates that the tool can be used in this slot */
  @Nullable
  public IToolStackView getValidTool(EquipmentSlot slotType) {
    return ModifierUtil.validArmorSlot(entity, slotType) ? getToolInSlot(slotType) : null;
  }

  /** Checks if any of the armor items are modifiable, limiting to the passed slots. Filters out holding armor to get its effects. */
  public boolean hasModifiableArmor(EquipmentSlot... slots) {
    for (EquipmentSlot slotType : slots) {
      if (getValidTool(slotType) != null) {
        return true;
      }
    }
    return false;
  }

  /** Checks if any of the armor items are modifiable. Filters out holding armor to get its effects. */
  public boolean hasModifiableArmor() {
    return hasModifiableArmor(EquipmentSlot.values());
  }

  /** Gets the tinker data capability */
  public LazyOptional<TinkerDataCapability.Holder> getTinkerData() {
    if (tinkerData == null) {
      tinkerData = entity.getCapability(TinkerDataCapability.CAPABILITY);
    }
    return tinkerData;
  }


  /* Iteration */
  private Iterable<EquipmentEntry> toolIterable, armorIterable, wornArmorIterable;

  /** Gets all tools from the given function */
  public Iterable<EquipmentEntry> makeIterable(Function<EquipmentSlot,IToolStackView> getter) {
    List<IToolStackView> tools = new ArrayList<>(6);
    List<EquipmentSlot> slots = new ArrayList<>(6);
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      IToolStackView tool = getter.apply(slot);
      if (tool != null && !tool.isBroken() && !tool.getModifiers().isEmpty()) {
        tools.add(tool);
        slots.add(slot);
      }
    }
    return EquipmentIterator.iterable(tools, slots);
  }

  /** Iterates all non-broken tools that are in a valid slot in this context. */
  public Iterable<EquipmentEntry> iterateTools() {
    if (toolIterable == null) {
      toolIterable = makeIterable(this::getValidTool);
    }
    return toolIterable;
  }

  /** Iterates all non-broken armor in a valid slot in this context. */
  public Iterable<EquipmentEntry> iterateArmor() {
    if (armorIterable == null) {
      armorIterable = makeIterable(slot -> entity.getItemBySlot(slot).is(TinkerTags.Items.ARMOR) ? getValidTool(slot) : null);
    }
    return armorIterable;
  }

  /** Iterates all non-broken armor in a valid slot in this context. */
  public Iterable<EquipmentEntry> iterateWornArmor() {
    if (wornArmorIterable == null) {
      wornArmorIterable = makeIterable(slot -> slot.isArmor() ? getToolInSlot(slot) : null);
    }
    return wornArmorIterable;
  }
}
