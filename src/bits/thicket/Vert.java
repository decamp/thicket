package bits.thicket;

/**
 * @author decamp
 */
public class Vert {
    
    public float mX;
    public float mY;
    public float mZ;
    public float mWeight = 1f;

    public float mForceX;
    public float mForceY;
    public float mForceZ;

    public Edge mEdges;
    public Vert mGraphNext;
    public Vert mGraphOwner;
    
    public Vert mTempNext;
    public float mTempDist;
    
    public float mPrevForceX;
    public float mPrevForceY;
    public float mPrevForceZ;
    
    
    public Vert() {}
  
    
    
    public void addEdge( Edge e ) {
        if( this == e.mA ) {
            e.mANext = mEdges;
        } else {
            e.mBNext = mEdges;
        }
        mEdges = e;
    }

    
    public boolean isConnectedTo( Vert b ) {
        for( Edge e = mEdges; e != null; e = e.next( this ) ) {
            if( e.contains( b ) ) {
                return true;
            }
        }
        
        return false;
    }

    
    public String toString() {
        return String.format( "Vert %d", (int)( mTempDist + 0.5f ) );
    }
    
}

