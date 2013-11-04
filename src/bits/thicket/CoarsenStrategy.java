package bits.thicket;

/**
 * A class that may combine some set of verts into a smaller set. While
 * some kind of coarsening function is essential for multilevel layouts,
 * in practice I have not found much difference between the algorithm used.
 * 
 * @author decamp
 */
interface CoarsenStrategy {
    
    /**
     * For given vertex, selects the edge with greatest weight. 
     * Runs in O(E) time.
     */
    public static final CoarsenStrategy MAX_EDGE = new CoarsenStrategy() {
        public Vert coarsenVerts( LayoutParams params, Vert list ) {
            Vert ret = null;
            
            // Find set of nodes to collapse.
            for( Vert vert = list; vert != null; vert = vert.mGraphNext ) {
                // Check if already collapsed
                if( vert.mGraphOwner != null ) {
                    continue;
                }
                
                // Find edge of greatest weight.
                Edge maxEdge    = null;
                float maxWeight = Float.NEGATIVE_INFINITY;
                
                for( Edge edge = vert.mEdges; edge != null; edge = edge.next( vert ) ) {
                    if( edge.mWeight > maxWeight && edge.other( vert ).mGraphOwner == null ) {
                        maxWeight = edge.mWeight;
                        maxEdge   = edge;
                    }
                }
                if( maxEdge == null ) {
                    continue;
                }
                
                // Combine nodes to create coarser parent. 
                Vert combined = Graphs.combine( maxEdge.mA, maxEdge.mB, params.mVertWeightModel );
                combined.mGraphNext = ret;
                ret = combined;
            }
            
            return ret;
        }
    };
    
    /**
     * For given vertex, finds connected vertex that has the largest
     * portion of shared edges by weight. For example, if vertex A and B
     * have the same set of neighbors, the correlation of their edge
     * signature will be 1.0. If they have no shared neighbors, 
     * the signature will be much lower.
     * <p>
     * In practice, I've found this provides a <i>tiny</i> improvement
     * to quality. However, this function is not optimized, and runs
     * in O(VE^2) time (I think), where V is the number of verts and E is the
     * number of edges.
     * 
     */
    public static final CoarsenStrategy EDGE_SIGNATURE = new CoarsenStrategy() {
        public Vert coarsenVerts( LayoutParams params, Vert list ) {
            Vert ret = null;
            int vertCount = 0;
            
            // Clear temp variables
            for( Vert v = list; v != null; v = v.mGraphNext ) {
                v.mTempNext = null;
                vertCount++;
            }
            
            // Find set of nodes to collapse.
            for( Vert v = list; v != null; v = v.mGraphNext ) {
                // Check if already collapsed
                if( v.mGraphOwner != null ) {
                    continue;
                }
                
                // Mark all vertices that connect to v.
                float vTotal = 0f;
                v.mTempNext = v;
                for( Edge e = v.mEdges; e != null; e = e.next( v ) ) {
                    e.other( v ).mTempNext = v;
                    vTotal += e.mWeight;
                }
                
                // For each vertex connected to v that is not collapsed, find number of shared vertices.
                Edge bestEdge = null;
                float bestMatch = 0f;
                
                for( Edge e = v.mEdges; e != null; e = e.next( v ) ) {
                    Vert u = e.other( v );
                    if( u.mGraphOwner != null ) {
                        continue;
                    }
                    
                    float uTotal = 0f;
                    float sTotal = 0f;
                    
                    for( Edge f = u.mEdges; f != null; f = f.next( u ) ) {
                        uTotal += f.mWeight;
                        if( f.other( u ).mTempNext == v ) {
                            sTotal += f.mWeight;
                        }
                    }
                    
                    float match = sTotal / ( uTotal + vTotal );
                    if( match > bestMatch ) {
                        bestMatch = match;
                        bestEdge  = e;
                    }
                }
                
                if( bestEdge == null ) {
                    continue;
                }
                
                // Combine nodes to create coarser parent. 
                Vert combined = Graphs.combine( bestEdge.mA, bestEdge.mB, params.mVertWeightModel );
                combined.mGraphNext = ret;
                ret = combined;
            }
            
            return ret;
        }
    };
    
    /**
     * Generates list of vertices, each one an aggregate of two verts 
     * from the provided list.
     * <p>
     * The provided Verts will be modified during this call. Mainly, 
     * on return, v.mGraphOwner will point at its new owner for
     * some Vert 'v' in verts. If 'v' did not receive a new owner,
     * v.mGraphOwner will be null. 
     * <p>
     * Potentially modified fields for a Vert 'v' in 'verts': <br/>
     * v.mGraphOwner <br/>
     * v.mTempNext <br/>
     * v.mTempDist <br/>
     * <p>
     * Note that no changes will be made to the v.mWeight or v.mEdges.
     */
    public Vert coarsenVerts( LayoutParams params, Vert verts );
    
}
