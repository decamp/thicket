/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

/**
 * Determines how a given vertex is weighted and may be combined.
 * 
 * @author decamp
 */
public interface VertWeightModel {
    
    /** 
     * @return computed weight of vertex
     */
    public float atomicWeight( Vert v );
    
    /**
     * @return combined weight of two vertices.
     */
    public float combinedWeight( Vert a, Vert b );

    
    
    public static final VertWeightModel PREDEFINED = new VertWeightModel() {
        public float atomicWeight( Vert v ) {
            return v.mWeight;
        }
      
        public float combinedWeight( Vert a, Vert b ) {
            return a.mWeight + b.mWeight;
        }
    };
    
    
    public static final VertWeightModel UNIT = new VertWeightModel() {
        public float atomicWeight( Vert v ) {
            return v.mWeight;
        }
        
        public float combinedWeight( Vert a, Vert b ) {
            return a.mWeight + b.mWeight;
        }
    };
    
    
    public static final VertWeightModel DEGREE = new VertWeightModel() {
        public float atomicWeight( Vert v ) {
            int ret = 1;
            for( Edge e = v.mEdges; e != null; e = e.next( v ) ) {
                ret++;
            }
            return ret;
        }
        
        public float combinedWeight( Vert a, Vert b ) {
            return a.mWeight + b.mWeight;
        }
    };
    
}
