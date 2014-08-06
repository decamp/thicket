/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

/**
 * Represents a weighted graph edge.
 *
 * @author decamp
 */
public class Edge {

    /**
     * One vert in Edge.
     * <p>
     * set-by: user
     */
    public final Vert mA;

    /**
     * Other vert in Edge.
     * <p>
     * set-by: user
     */
    public final Vert mB;

    /**
     * Weight of edge.
     * <p>
     * set-by: user
     */
    public float mWeight;

    /**
     * Remainder of linked-list of edges connecting to Vert A.
     * <p>
     * set-by: lib
     */
    public Edge mANext = null;

    /**
     * Remainder of linked-list of edges connecting to Vert B.
     * <p>
     * set-by: lib
     */
    public Edge mBNext = null;

    /**
     * Remainder of linked-list of all edges in graph.
     * <p>
     * set-by: lib
     */
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
