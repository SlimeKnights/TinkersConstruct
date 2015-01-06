/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Zeno410Utils;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

/**
 *
 * @author Zeno410
 */
public interface Streamable {

        abstract public void readFrom(DataInput input) throws IOException ;
        abstract public void writeTo(DataOutput output) throws IOException;
}
