package slimeknights.tconstruct.library.materials.stats.types;

import slimeknights.tconstruct.library.tools.stat.ToolStatId;

class NoSuchToolStatException extends RuntimeException {
    public NoSuchToolStatException(ToolStatId stat) {
        super("No such tool stat: " + stat.toString());
    }
}