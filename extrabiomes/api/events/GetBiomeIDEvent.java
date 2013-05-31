/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.api.events;

import net.minecraftforge.event.Event;


/**
 * Used by the API to retrieve a biomeID
 * 
 * @author Scott
 * 
 */
public class GetBiomeIDEvent extends Event {

    /**
     * Valid values:
     * 
     * <pre>
     *     ALPINE
     *     AUTUMNWOODS
     *     BIRCHFOREST
     *     EXTREMEJUNGLE
     *     FORESTEDHILLS
     *     FORESTEDISLAND
     *     GLACIER
     *     GREENHILLS
     *     GREENSWAMP
     *     ICEWASTELAND
     *     MARSH
     *     MEADOW
     *     MINIJUNGLE
     *     MOUNTAINDESERT
     *     MOUNTAINRIDGE
     *     MOUNTAINTAIGA
     *     PINEFOREST
     *     RAINFOREST
     *     REDWOODFOREST
     *     REDWOODLUSH
     *     SAVANNA
     *     SHRUBLAND
     *     SNOWYFOREST
     *     SNOWYRAINFOREST
     *     TEMPORATERAINFOREST
     *     TUNDRA
     *     WASTELAND
     *     WOODLANDS
     * </pre>
     */
    public final String targetBiome;
    public int          biomeID;

    public GetBiomeIDEvent(String targetBiome) {
        this.targetBiome = targetBiome;
    }

}
