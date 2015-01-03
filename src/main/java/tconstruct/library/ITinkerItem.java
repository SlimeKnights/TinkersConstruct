package tconstruct.library;

public interface ITinkerItem {

  /**
   * Returns the tag key of the tag with the tinker data.
   */
  public String getTagName();

  /**
   * Returns an String of arrays, where each String represents an information about the tool.
   * Used to display Information about the item in a tooltip or the GUI
   */
  public String[] getInformation();
}
