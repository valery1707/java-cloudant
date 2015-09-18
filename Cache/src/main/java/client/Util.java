/**
 * 
 */
package client;

import java.util.Date;

/**
 * @author ArunIyengar Utility methods called by several other classes
 * 
 */
public class Util {

    public static long getTime() {
        return (new Date()).getTime();
    }
}
