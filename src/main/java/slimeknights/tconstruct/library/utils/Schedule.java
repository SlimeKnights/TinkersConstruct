package slimeknights.tconstruct.library.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a serializable list of tasks scheduled on creation of an object. Reduces the amount of work run each tick.
 * See {@link slimeknights.tconstruct.library.modifiers.hook.ranged.ScheduledProjectileTaskModifierHook} for an example of usage.
 */
@RequiredArgsConstructor
public class Schedule {
  public static final Schedule EMPTY = new Schedule(new ScheduleEntry[0]);

  private final ScheduleEntry[] entries;
  private int nextIndex = 0;

  /** Checks if the schedule is empty, meaning either it has no tasks or all tasks are finished */
  public boolean isEmpty() {
    return nextIndex >= entries.length;
  }

  /**
   * Checks the next entry to see if it's ready to perform for the current time.
   * Note its possible there are multiple tasks ready, so its advised to call this in a loop.
   * @param time  Current time
   * @return Task that is ready to be performed, or -1 if no task is ready.
   */
  public int check(int time) {
    if (nextIndex < entries.length) {
      ScheduleEntry nextEntry = entries[nextIndex];
      if (time >= nextEntry.time) {
        nextIndex++;
        return nextEntry.task;
      }
    }
    return -1;
  }

  /** Gets the index of the entry in a list that queued the task. */
  public static int index(int task, int size) {
    return task % size;
  }

  /** Gets the local task index given the list task */
  public static int local(int task, int size) {
    return task / size;
  }

  /* NBT */

  /** Serializes the schedule to NBT */
  public ListTag serialize() {
    ListTag list = new ListTag();
    // don't serialize complete tasks, saves effort
    for (int i = nextIndex; i < entries.length; i++) {
      list.add(entries[i].serialize());
    }
    return list;
  }

  /** Deserializes an schedule from NBT */
  public static Schedule deserialize(ListTag list) {
    if (list.isEmpty()) {
      return EMPTY;
    }
    ScheduleEntry[] entries = new ScheduleEntry[list.size()];
    for (int i = 0; i < entries.length; i++) {
      entries[i] = ScheduleEntry.deserialize(list.getCompound(i));
    }
    return new Schedule(entries);
  }

  /** Single entry in the schedule */
  private record ScheduleEntry(int task, int time) {
    /** Serializes an entry to NBT */
    public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("task", task);
      tag.putInt("time", time);
      return tag;
    }

    /** Deserializes an entry from NBT */
    public static ScheduleEntry deserialize(CompoundTag nbt) {
      return new ScheduleEntry(nbt.getInt("task"), nbt.getInt("time"));
    }
  }

  /** Common logic between {@link ScheduleBuilder} and {@link ListScheduler} */
  public interface Scheduler {
    /** Adds a task to the schedule */
    Scheduler add(int task, int time);

    /** Starts scheduling a list. Use {@link ListScheduler#setIndex(int)} to prepare for each entry. */
    default ListScheduler list(int size) {
      return new ListScheduler(this, size);
    }
  }

  /** Builder for a schedule */
  public static class ScheduleBuilder implements Scheduler {
    private final List<ScheduleEntry> entries = new ArrayList<>();

    @Override
    public Scheduler add(int task, int time) {
      if (task < 0) {
        throw new IllegalArgumentException("Task must be non-negative");
      }
      entries.add(new ScheduleEntry(task, time));
      return this;
    }

    /** Builds the final schedule */
    public Schedule build() {
      return new Schedule(entries.stream().sorted(Comparator.comparing(ScheduleEntry::time)).toArray(ScheduleEntry[]::new));
    }
  }

  /** Handles delegating a schedule to a list, ensuring no conflicts between the list entries. */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ListScheduler implements Scheduler {
    private final Scheduler parent;
    private final int size;
    @Setter
    private int index;

    @Override
    public Scheduler add(int task, int time) {
      return parent.add(task * size + index, time);
    }
  }
}
