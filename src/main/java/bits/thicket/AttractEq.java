/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

/**
 * @author decamp
 */
public enum AttractEq {
    LOG_DIST,
    LINEAR_DIST,
    SQUARE_DIST;


    public static AttractFunc newFunc( AttractEq eq, int dim ) {
        switch( eq ) {
        case LOG_DIST:
            switch( dim ) {
            case 2: return new AttractFunc.Log2();
            case 3: return new AttractFunc.Log3();
            }
            break;

        case LINEAR_DIST:
            switch( dim ) {
            case 2:  return new AttractFunc.Linear2();
            case 3:  return new AttractFunc.Linear3();
            }
            break;

        case SQUARE_DIST:
        default:
            switch( dim ) {
            case 2:  return new AttractFunc.Square2();
            case 3:  return new AttractFunc.Square3();
            }
            break;
        }

        throw new IllegalArgumentException( "Dimension not supported: " + dim ); 
    }

}
