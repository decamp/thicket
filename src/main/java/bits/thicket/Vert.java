/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

/**
 * Represents a single vertex in a graph.
 *
 * @author decamp
 */
public class Vert {

    /**
     * set-by: user during initialization, by library for each layout step
     */
    public float mX;
    public float mY;
    public float mZ;

    /**
     * set-by: user
     */
    public float mWeight = 1f;

    /**
     * set-by: library on each layout step
     **/
    public float mForceX;
    public float mForceY;
    public float mForceZ;


    /**
     * set-by: library during graph construction
     **/
    public Edge mEdges;
    public Vert mGraphNext;
    public Vert mGraphOwner;

    /**
     * set-by: library on each layout step
     **/
    public Vert  mTempNext;
    public float mTempDist;

    /**
     * set-by: library on each layout step
     **/
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


    public String formatPosition() {
        return String.format( "[%6.3f, %6.3f, %6.3f]", mX, mY, mZ );
    }

}

