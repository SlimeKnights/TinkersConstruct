package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;

import java.util.List;
import java.util.Set;

/**
 * The data defining a tinkers tool, e.g. a pickaxe or a hammer.
 * Note that this defines the tool metadata itself, not an instance of the tool.
 * Contains information about what's needed to craft the tool, how it behaves...
 */
public class ToolDefinition {

  /**
   * Inherent stats of the tool.
   */
  private final ToolBaseStatDefinition baseStatDefinition;
  /**
   * The tool parts required to build this tool.
   */
  protected final List<PartMaterialRequirement> requiredComponents;
  /**
   * Categories determine behaviour of the tool when interacting with things or displaying information.
   */
  protected final Set<Category> categories;

  public ToolDefinition(ToolBaseStatDefinition baseStatDefinition, List<PartMaterialRequirement> requiredComponents, Set<Category> categories) {
    this.baseStatDefinition = baseStatDefinition;
    this.requiredComponents = ImmutableList.copyOf(requiredComponents);
    this.categories = ImmutableSet.copyOf(categories);
  }

  /**
   * Gets the current tools base stats definition
   *
   * @return the tools base stats definition
   */
  public ToolBaseStatDefinition getBaseStatDefinition() {
    return this.baseStatDefinition;
  }

  /**
   * Gets the required components for the given tool definition
   * @return the required components
   */
  public List<PartMaterialRequirement> getRequiredComponents() {
    return this.requiredComponents;
  }

  /**
   * Checks if the tool has the given category or not
   *
   * @param category the category to check for
   * @return if the tool has the category or not
   */
  public boolean hasCategory(Category category) {
    return this.categories.contains(category);
  }

  /**
   * Gets all the categories for the given tool
   *
   * @return the list of categories
   */
  public Set<Category> getCategories() {
    return this.categories;
  }

  /* Repairing */
// todo: repairing
  /** Returns indices of the parts that are used for repairing */
/*
  public int[] getRepairParts() {
    return new int[] { 1 }; // index 1 usually is the head. 0 is handle.
  }

  public float getRepairModifierForPart(int index) {
    return 1f;
  }
*/
}
