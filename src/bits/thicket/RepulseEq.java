package bits.thicket;

/**
 * @author decamp
 */
public enum RepulseEq {
    
    INV_LOG_DIST,
    INV_LINEAR_DIST,
    INV_SQUARE_DIST;
    
    static RepulseFunc newFunc( RepulseEq eq, int dim ) {
        switch( eq ) {
        case INV_LOG_DIST:
            switch( dim ) {
            case 2: return new RepulseFunc.InvLog2();
            case 3: return new RepulseFunc.InvLog3();
            }
            break;
        case INV_LINEAR_DIST:
        default:
            switch( dim ) {
            case 2: return new RepulseFunc.InvLinear2();
            case 3: return new RepulseFunc.InvLinear3();
            }
            break;
        case INV_SQUARE_DIST:
            switch( dim ) {
            case 2: return new RepulseFunc.InvSquare2();
            case 3: return new RepulseFunc.InvSquare3();
            }
            break; 
        }
        
        throw new IllegalArgumentException( "Dimensionality " + dim );
    }

}