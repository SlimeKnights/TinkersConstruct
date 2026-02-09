package slimeknights.tconstruct.library.tools.context;

import com.google.common.collect.AbstractIterator;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentIterator.EquipmentEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.CheckForNull;
import java.util.Collections;
import java.util.List;

/**
 * Iterator for calling a modifier hook on all four equipment slots, respecting priority across all pieces.
 * Effectively is like a merge sort, only instead of sorting the list we compute the values inline.
 */
public class EquipmentIterator extends AbstractIterator<EquipmentEntry> {
  /** Creates an iterable over the given tools */
  public static Iterable<EquipmentEntry> iterable(List<IToolStackView> tools, List<EquipmentSlot> slots) {
    if (tools.size() != slots.size()) {
      throw new IllegalArgumentException("Tools and slots sizes must match");
    }
    if (tools.isEmpty()) {
      return Collections.emptyList();
    }
    // if the size is 1, we can optimize by just worrying about a single tool
    if (tools.size() == 1) {
      // only bind the values, not the whole list
      IToolStackView tool = tools.get(0);
      EquipmentSlot slot = slots.get(0);
      return () -> new SingleEquipmentIterator(tool, slot);
    }
    // create the array to pass into the iterator
    ToolEntry[] toolEntries = new ToolEntry[tools.size()];
    for (int i = 0; i < toolEntries.length; i++) {
      toolEntries[i] = ToolEntry.from(tools.get(i), slots.get(i));
    }
    return () -> new EquipmentIterator(toolEntries);
  }


  /** Entry to return during iteration. Values inside the entry are mutated for efficiency */
  protected final EquipmentEntry entry = new EquipmentEntry();
  /** Tools to iterate */
  private final ToolEntry[] tools;
  /** Last index within each tool modifier list */
  private final int[] lastModifier;
  /** Index of the last tool considered. If -1, we ran out of data. */
  private int lastTool = -1;
  /** Priority of the last modifier that ran */
  private int lastPriority = Integer.MAX_VALUE;

  private EquipmentIterator(ToolEntry[] tools) {
    this.tools = tools;
    this.lastModifier = new int[tools.length];
    // find our first modifier, if we have none then go to end of data immediately
    findNextModifier();
    if (lastTool == -1) {
      endOfData();
    }
  }

  /** Finds which tool has the modifier with the highest priority */
  private void findNextModifier() {
    int max = Integer.MIN_VALUE;
    int index = -1;
    // search each tool for the one with the highest index
    for (int toolIndex = 0; toolIndex < tools.length; toolIndex++) {
      ToolEntry tool = tools[toolIndex];
      int modifierIndex = lastModifier[toolIndex];
      if (modifierIndex < tool.size) {
        ModifierEntry entry = tool.modifiers.get(modifierIndex);
        int priority = entry.getModifier().getPriority();
        if (priority > max) {
          max = priority;
          index = toolIndex;
        }
      }
    }
    // out of data? clear the tool to save memory
    if (index == -1) {
      entry.tool = null;
    } else {
      lastPriority = max;
      // if the index changed, update our tool
      if (lastTool != index) {
        entry.tool = tools[index].tool;
        entry.slot = tools[index].slot;
      }
    }
    lastTool = index;
  }

  @CheckForNull
  @Override
  protected EquipmentEntry computeNext() {
    // out of entries for this tool?
    int modifierIndex = lastModifier[lastTool];
    ToolEntry tool = tools[lastTool];
    // have new modifier for this tool?
    if (modifierIndex < tool.size) {
      ModifierEntry modifier = tool.modifiers.get(modifierIndex);
      // priority must match, otherwise it might be someone else's turn
      if (modifier.getModifier().getPriority() == lastPriority) {
        // store the entry and increment the index. Tool is already stored
        this.entry.modifier = modifier;
        lastModifier[lastTool] = modifierIndex + 1;
        return this.entry;
      }
    }
    // either ran out of modifiers or the priority did not match, find new priority
    findNextModifier();
    // ran out of tools to iterate? we are done
    if (lastTool == -1) {
      return endOfData();
    }
    // findNextModifier() guarantees us a valid modifier here
    modifierIndex = lastModifier[lastTool];
    this.entry.modifier = tools[lastTool].modifiers.get(modifierIndex);
    lastModifier[lastTool] = modifierIndex + 1;
    return this.entry;
  }


  /** Optimization for when the context has just 1 tool. */
  private static class SingleEquipmentIterator extends AbstractIterator<EquipmentEntry> {
    private final EquipmentEntry entry = new EquipmentEntry();
    private final List<ModifierEntry> modifiers;
    private final int size;
    private int index = 0;

    private SingleEquipmentIterator(IToolStackView tool, EquipmentSlot slot) {
      this.entry.tool = tool;
      this.entry.slot = slot;
      this.modifiers = tool.getModifierList();
      this.size = modifiers.size();
    }

    @CheckForNull
    @Override
    protected EquipmentEntry computeNext() {
      // out of data?
      if (index >= size) {
        return endOfData();
      }
      // fetch the new entry, increment, then return
      entry.modifier = modifiers.get(index);
      index++;
      return entry;
    }
  }


  /* Helpers */

  /** Entry of a tool and its modifiers */
  private record ToolEntry(IToolStackView tool, List<ModifierEntry> modifiers, int size, EquipmentSlot slot) {
    /** Creates a new instance from the given tool */
    public static ToolEntry from(IToolStackView tool, EquipmentSlot slot) {
      List<ModifierEntry> modifiers = tool.getModifierList();
      return new ToolEntry(tool, modifiers, modifiers.size(), slot);
    }
  }

  /** Data class containing the current tool and modifier to run hooks */
  @Accessors(fluent = true)
  @Getter
  public static class EquipmentEntry {
    /** Active tool for this iteration */
    private IToolStackView tool;
    /** Slot containing the active tool */
    private EquipmentSlot slot;
    /** Active modifier */
    private ModifierEntry modifier;
  }
}
