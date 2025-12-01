package slimeknights.tconstruct.library.modifiers.hook.ranged;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.Schedule;
import slimeknights.tconstruct.library.utils.Schedule.ListScheduler;
import slimeknights.tconstruct.library.utils.Schedule.ScheduleBuilder;
import slimeknights.tconstruct.library.utils.Schedule.Scheduler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/** Hook for scheduling tasks on a projectile. Takes the place of a tick based hook. */
public interface ScheduledProjectileTaskModifierHook {
  /**
   * Hook to schedule tasks to run later in the projectile's life.
   * @param tool            Ammo tool instance.
   * @param modifier        Modifier being used
   * @param ammo            Ammo stack used to fire this projectile.
   * @param projectile      Projectile to modify
   * @param arrow           Arrow to modify as most modifiers wish to change that, will be null for non-arrow projectiles
   * @param persistentData  Persistent data instance stored on the arrow to write arbitrary data. Note the modifier list was already written.
   * @param scheduler       Scheduler for adding tasks. Task index is already localized to your modifier hook, so start from 0. Time is in ticks.
   */
  void scheduleProjectileTask(IToolStackView tool, ModifierEntry modifier, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, Scheduler scheduler);

  /**
   * Called when a schedule task's time is met to perform the task.
   * @param tool            Ammo tool instance.
   * @param modifier        Modifier being used
   * @param ammo            Ammo stack used to fire this projectile.
   * @param projectile      Acting projectile
   * @param arrow           Acting arrow
   * @param persistentData  Persistent data instance stored on the arrow to write arbitrary data.
   * @param task            Task index that was run. Should match the index you passed in {@link #scheduleProjectileTask(IToolStackView, ModifierEntry, ItemStack, Projectile, AbstractArrow, ModDataNBT, Scheduler)}.
   *                        It's a good idea to validate the index even when only using one to minimize issues with task indices breaking when datapacks change.
   */
  void onScheduledProjectileTask(IToolStackView tool, ModifierEntry modifier, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, int task);


  /* Helpers */

  /** Creates the schedule for the given tool */
  static Schedule createSchedule(IToolStackView tool, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData) {
    List<ModifierEntry> modifiers = tool.getModifiers().getModifiers();
    ScheduleBuilder builder = new ScheduleBuilder();
    int size = modifiers.size();
    ListScheduler scheduler = builder.list(size);
    for (int i = 0; i < size; i++) {
      ModifierEntry entry = modifiers.get(i);
      scheduler.setIndex(i);
      entry.getHook(ModifierHooks.SCHEDULE_PROJECTILE_TASK).scheduleProjectileTask(tool, entry, ammo, projectile, arrow, persistentData, scheduler);
    }
    return builder.build();
  }

  /** Creates the schedule for the given tool */
  static void checkSchedule(IToolStackView tool, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, Schedule schedule) {
    do {
      int task = schedule.check(projectile.tickCount);
      if (task < 0) {
        break;
      }
      ModDataNBT persistentData = PersistentDataCapability.getOrWarn(projectile);
      List<ModifierEntry> modifiers = tool.getModifiers().getModifiers();
      int size = modifiers.size();
      int index = Schedule.index(task, size);
      if (index < size) {
        ModifierEntry entry = modifiers.get(index);
        entry.getHook(ModifierHooks.SCHEDULE_PROJECTILE_TASK).onScheduledProjectileTask(tool, entry, ammo, projectile, arrow, persistentData, Schedule.local(task, size));
      }
    } while (true);
  }

  /** Hook merger to combine multiple scheduled tasks */
  record ScheduleMerger(List<ScheduledProjectileTaskModifierHook> modules) implements ScheduledProjectileTaskModifierHook {
    public ScheduleMerger(Collection<ScheduledProjectileTaskModifierHook> modules) {
      this(List.copyOf(modules));
    }

    @Override
    public void scheduleProjectileTask(IToolStackView tool, ModifierEntry modifier, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, Scheduler scheduler) {
      int size = modules.size();
      ListScheduler listScheduler = scheduler.list(modules.size());
      for (int i = 0; i < size; i++) {
        listScheduler.setIndex(i);
        modules.get(i).scheduleProjectileTask(tool, modifier, ammo, projectile, arrow, persistentData, listScheduler);
      }
    }

    @Override
    public void onScheduledProjectileTask(IToolStackView tool, ModifierEntry modifier, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, int task) {
      int size = modules.size();
      int index = Schedule.index(task, size);
      if (index < size) {
        modules.get(index).onScheduledProjectileTask(tool, modifier, ammo, projectile, arrow, persistentData, Schedule.local(task, size));
      }
    }
  }
}
