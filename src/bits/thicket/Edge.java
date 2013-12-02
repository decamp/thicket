package bits.thicket;

/**
 * @author decamp
 */
public class Edge {

    public final Vert mA;
    public final Vert mB;
    public float mWeight;
    
    public Edge mANext     = null;
    public Edge mBNext     = null;
    public Edge mGraphNext = null;
    
    
    public Edge( Vert a, Vert b, float weight ) {
        mA = a;
        mB = b;
        mWeight = weight;
    }
    
    
    public Vert other( Vert v ) {
        return v == mA ? mB : mA;
    }
   
    
    public boolean contains( Vert v ) {
        return v == mA || v == mB;
    }
    
    
    public Edge next( Vert source ) {
        return source == mA ? mANext : mBNext;
    }
    
    
    public void setNext( Vert source, Edge next ) {
        if( source == mA ) {
            mANext = next;
        } else {
            mBNext = next;
        }
    }
   
    
    public String toString() {
        return String.format( "%s->%s", mA, mB );
    }
    
}
