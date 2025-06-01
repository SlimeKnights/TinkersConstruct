package slimeknights.tconstruct.library.modifiers.modules.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Generic builder for a modifier module */
public abstract class ModuleBuilder<B extends ModuleBuilder<B,T>,T extends IToolContext> {
  /** Level range for this module */
  protected ModifierCondition<T> condition;

  /** Gets this builder casted */
  @SuppressWarnings("unchecked")
  protected B self() {
    return (B) this;
  }


  /* Tool predicate */

  /** Sets the tool condition for this module */
  public abstract B toolContext(IJsonPredicate<IToolContext> tool);

  /** Sets the tool condition for this module */
  public B toolItem(IJsonPredicate<Item> tool) {
    return toolContext(ToolContextPredicate.fallback(tool));
  }

  /** Sets the tool tag for this module */
  public B toolTag(TagKey<Item> tag) {
    return toolItem(ItemPredicate.tag(tag));
  }


  /* Level range */

  /** Sets the level range for this builder */
  private B setLevels(IntRange range) {
    this.condition = condition.with(range);
    return self();
  }

  /** Sets the modifier level range for this module */
  public B levelRange(int min, int max) {
    return setLevels(ModifierEntry.VALID_LEVEL.range(min, max));
  }

  /** Sets the modifier level range for this module */
  public B minLevel(int min) {
    return setLevels(ModifierEntry.VALID_LEVEL.min(min));
  }

  /** Sets the modifier level range for this module */
  public B maxLevel(int max) {
    return setLevels(ModifierEntry.VALID_LEVEL.max(max));
  }

  /** Sets the modifier level range for this module */
  public B exactLevel(int value) {
    return setLevels(ModifierEntry.VALID_LEVEL.exactly(value));
  }


  /** Builder for a module using tool context predicates */
  public static abstract class Context<B extends Context<B>> extends ModuleBuilder<B,IToolContext> {
    public Context() {
      this.condition = ModifierCondition.ANY_CONTEXT;
    }

    @Override
    public B toolContext(IJsonPredicate<IToolContext> tool) {
      this.condition = this.condition.with(tool);
      return self();
    }
  }

  /** Builder for a module using tool stack predicates */
  public static abstract class Stack<B extends Stack<B>> extends ModuleBuilder<B,IToolStackView> {
    public Stack() {
      this.condition = ModifierCondition.ANY_TOOL;
    }

    /** Sets the tool condition for this module */
    public B tool(IJsonPredicate<IToolStackView> tool) {
      this.condition = this.condition.with(tool);
      return self();
    }

    @Override
    public B toolContext(IJsonPredicate<IToolContext> tool) {
      return tool(ToolStackPredicate.context(tool));
    }
  }
}
