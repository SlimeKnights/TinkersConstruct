package slimeknights.tconstruct.library.materials.stats.types;

import slimeknights.tconstruct.library.tools.stat.ToolStatId;

class ToolStatTypeNotMatchException extends RuntimeException {
    public ToolStatTypeNotMatchException(ToolStatId stat, String expectedStatType) {
        super("Tool stat " + stat.toString() + " is not of type " + expectedStatType);
    }
}