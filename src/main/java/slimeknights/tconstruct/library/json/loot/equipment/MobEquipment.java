package slimeknights.tconstruct.library.json.loot.equipment;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.living.MobSpawnEvent.FinalizeSpawn;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** JSON object representing equipment replacement encoded in JSON */
public record MobEquipment(EquipmentSlot slot, IJsonPredicate<Item> match, ItemOutput tool, List<RandomMaterial> materials, @Nullable TagKey<Fluid> fluid, float chance, int priority) {
  public static final RecordLoadable<MobEquipment> LOADABLE = RecordLoadable.create(
    TinkerLoadables.EQUIPMENT_SLOT.requiredField("slot", MobEquipment::slot),
    ItemPredicate.LOADER.defaultField("match", MobEquipment::match),
    ItemOutput.Loadable.REQUIRED_STACK.requiredField("tool", MobEquipment::tool),
    RandomMaterial.LOADER.list(0).defaultField("materials", List.of(), false, MobEquipment::materials),
    Loadables.FLUID_TAG.nullableField("fluid", MobEquipment::fluid),
    FloatLoadable.PERCENT.defaultField("chance", 0.05f, true, MobEquipment::chance),
    IntLoadable.FROM_ZERO.defaultField("priority", 100, true, MobEquipment::priority),
    MobEquipment::new);
  /** Loadable for the list of entries in JSON */
  public static final Loadable<List<MobEquipment>> LIST_LOADABLE = LOADABLE.list(ArrayLoadable.COMPACT);

  /** @apiNote use {@link Builder#slot(EquipmentSlot)} */
  @Internal
  public MobEquipment {}

  /** Creates a new builder instance */
  public static Builder builder() {
    return new MobEquipment.Builder();
  }

  /** Applies this replacement to the target mob */
  @SuppressWarnings({"deprecation", "OverrideOnly"})  // in that event, I can't call the event method, or I'll get a stack overflow
  public static boolean apply(List<MobEquipment> replace, Mob mob, FinalizeSpawn event) {
    // first, figure out which slots are going to apply. This is because we take over mob finalizing only if at least one applies
    RandomSource random = mob.getRandom();
    List<MobEquipment> apply = new ArrayList<>(replace.size());
    for (MobEquipment slot : replace) {
      if (random.nextFloat() < slot.chance()) {
        apply.add(slot);
      }
    }
    // forge event runs before finalize spawn so we can't just set our item now, or it may get overwritten
    // instead, we cancel the event (which blocks vanilla finalize), then finalize ourself, then can set our item after
    // since this is risky, only do this if we know we want our equipment there
    if (!apply.isEmpty()) {
      ServerLevelAccessor level = event.getLevel();
      mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), event.getSpawnType(), event.getSpawnData(), event.getSpawnTag());

      // apply any replacements
      for (MobEquipment slot : apply) {
        slot.apply(mob);
      }
      return true;
    }
    return false;
  }

  /** Applies this replacement to the target */
  public void apply(Mob mob) {
    // check if the slot is replaceable
    if (match.matches(mob.getItemBySlot(slot).getItem())) {
      ItemStack replacement = tool.copy();
      // if its a tool, apply randomized materials
      if (replacement.is(TinkerTags.Items.MODIFIABLE)) {
        ToolStack tool = ToolStack.from(replacement);
        ToolDefinition definition = tool.getDefinition();
        RandomSource random = mob.getRandom();
        if (definition.hasMaterials() && !materials.isEmpty()) {
          tool.setMaterials(RandomMaterial.build(ToolMaterialHook.stats(definition), materials, random));
        } else {
          tool.rebuildStats();
        }
        // if requested, fill with fluid
        if (fluid != null) {
          // fill with between 0mb and the max amount
          int capacity = ToolTankHelper.TANK_HELPER.getCapacity(tool);
          int amount = random.nextInt(capacity + 1);
          if (amount > 0) {
            // select fluid from tag
            Fluid fluid = BuiltInRegistries.FLUID.getTag(this.fluid)
              .flatMap(tag -> tag.getRandomElement(random))
              .map(Holder::get)
              .orElse(Fluids.EMPTY);
            if (fluid != Fluids.EMPTY) {
              ToolTankHelper.TANK_HELPER.setFluid(tool, new FluidStack(fluid, amount));
            }
          }
        }
      }
      mob.setItemSlot(slot, replacement);
    }
  }

  /** Builder for datagen */
  public static class Builder {
    private final List<SlotBuilder> replace = new ArrayList<>();

    private Builder() {}

    /** Adds an entry for the given slot */
    public SlotBuilder slot(EquipmentSlot slot) {
      SlotBuilder builder = new SlotBuilder(slot);
      replace.add(builder);
      return builder;
    }

    /** Builds the final list */
    public List<MobEquipment> build() {
      return replace.stream().map(SlotBuilder::build).toList();
    }

    @Accessors(fluent = true)
    @Setter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class SlotBuilder {
      /** Slot for the tool */
      private final EquipmentSlot slot;
      /** Condition the existing tool must match to be replaced */
      private IJsonPredicate<Item> match = ItemPredicate.ANY;
      /** Tool to give the entity */
      private ItemOutput tool = null;
      /** List of materials to assign to the tool */
      private final List<RandomMaterial> materials = new ArrayList<>();
      /** Tool will be filled with a random fluid from the passed tag. If null, no fluid is used. */
      @Nullable
      private TagKey<Fluid> fluid = null;
      /** Chance the tool is given to the entity */
      private float chance = 0.05f;
      /** Order this entry applies if multiple entries are on the same target. Higher numbers run earlier. */
      private int priority = 100;

      /** Sets the tool item */
      public SlotBuilder tool(ItemOutput tool) {
        this.tool = tool;
        return this;
      }

      /** Sets the tool item */
      public SlotBuilder tool(ItemLike tool) {
        return tool(ItemOutput.fromItem(tool));
      }

      /** Adds a material to the builder */
      public SlotBuilder material(RandomMaterial material) {
        materials.add(material);
        return this;
      }

      /** Adds a material to the builder */
      public SlotBuilder material(RandomMaterial... materials) {
        Collections.addAll(this.materials, materials);
        return this;
      }

      /** Builds the final equipment */
      private MobEquipment build() {
        return new MobEquipment(slot, match, tool, materials, fluid, chance, priority);
      }

      /** Finishes this slot */
      public Builder end() {
        return Builder.this;
      }
    }
  }
}
