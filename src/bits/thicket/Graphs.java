package bits.thicket;

import java.util.*;


/**
 * @author decamp
 */
public class Graphs {
    
   
    public static void computeBounds2( Vert verts, float[] outBox ) {
        if( verts == null ) {
            outBox[0] = 0;
            outBox[1] = 0;
            outBox[2] = 1;
            outBox[3] = 1;
            return;
        }
        
        float x0, x1, y0, y1;
        x0 = x1 = verts.mX;
        y0 = y1 = verts.mY;
        
        verts = verts.mGraphNext;
        while( verts != null ) {
            if      ( verts.mX < x0 ) x0 = verts.mX;
            else if ( verts.mX > x1 ) x1 = verts.mX;
            if      ( verts.mY < y0 ) y0 = verts.mY;
            else if ( verts.mY > y1 ) y1 = verts.mY;
            verts = verts.mGraphNext;
        }
        
        outBox[0] = x0;
        outBox[1] = y0;
        outBox[2] = x1;
        outBox[3] = y1;
    }

    
    public static void computeBounds3( Vert verts, float[] outBox ) {
        if( verts == null ) {
            outBox[0] = 0;
            outBox[1] = 0;
            outBox[2] = 0;
            outBox[3] = 1;
            outBox[4] = 1;
            outBox[5] = 1;
            return;
        }
        
        float x0, x1, y0, y1, z0, z1;
        x0 = x1 = verts.mX;
        y0 = y1 = verts.mY;
        z0 = z1 = verts.mZ;
        
        verts = verts.mGraphNext;
        while( verts != null ) {
            if      ( verts.mX < x0 ) x0 = verts.mX;
            else if ( verts.mX > x1 ) x1 = verts.mX;
            if      ( verts.mY < y0 ) y0 = verts.mY;
            else if ( verts.mY > y1 ) y1 = verts.mY;
            if      ( verts.mZ < z0 ) z0 = verts.mZ;
            else if ( verts.mZ > z1 ) z1 = verts.mZ;
            verts = verts.mGraphNext;
        }
        
        outBox[0] = x0;
        outBox[1] = y0;
        outBox[2] = z0;
        outBox[3] = x1;
        outBox[4] = y1;
        outBox[5] = z1;
    }
    
    
    public static void randomizePositions2( Vert verts, float[] bounds, Random optRand ) {
        if( verts == null ) {
            return;
        }
                
        if( optRand == null ) {
            optRand = new Random();
        }
        
        final float tx = bounds[0];
        final float sx = bounds[3] - bounds[0];
        final float ty = bounds[1];
        final float sy = bounds[4] - bounds[1];

        while( verts != null ) {
            verts.mX = sx * optRand.nextFloat() + tx;
            verts.mY = sy * optRand.nextFloat() + ty;
            verts = verts.mGraphNext;
        }
    }

    
    public static void randomizePositions3( Vert verts, float[] bounds, Random optRand ) {
        if( verts == null ) {
            return;
        }
                
        if( optRand == null ) {
            optRand = new Random();
        }
        
        final float tx = bounds[0];
        final float sx = bounds[3] - bounds[0];
        final float ty = bounds[1];
        final float sy = bounds[4] - bounds[1];
        final float tz = bounds[2];
        final float sz = bounds[5] - bounds[2];

        while( verts != null ) {
            verts.mX = sx * optRand.nextFloat() + tx;
            verts.mY = sy * optRand.nextFloat() + ty;
            verts.mZ = sz * optRand.nextFloat() + tz;
            verts = verts.mGraphNext;
        }
    }
    
    
    public static int size( Vert list ) {
        int ret = 0;
        while( list != null ) {
            ret++;
            list = list.mGraphNext;
        }
        return ret;
    }

    
    public static int size( Edge list ) {
        int ret = 0;
        while( list != null ) {
            ret++;
            list = list.mGraphNext;
        }
        return ret;
    }
    
    
    public static Graph coarsen( LayoutParams params, Graph src ) {
        if( src.mVertNo <= 1 || params.mCoarsenStrategy == null ) {
            return null;
        }
        
        Graph dst = new Graph();
        dst.mCoarseLevel = src.mCoarseLevel + 1;
        dst.mFinerGraph  = src;
        
        // Create list of new verts to be created.
        dst.mVerts  = params.mCoarsenStrategy.coarsenVerts( params, src.mVerts );
        dst.mVertNo = Graphs.size( dst.mVerts );
        
        // Reconstruct vert list.
        Vert loopVert = src.mVerts;
        src.mVerts    = null;
        src.mVertNo   = 0;
        src.mEdges    = null;
        src.mEdgeNo   = 0;
        
        while( loopVert != null ) {
            Vert v = loopVert;
            loopVert = loopVert.mGraphNext;
            
            if( v.mGraphOwner != null ) {
                // This vert is being collapsed.
                src.addVert( v );
            } else {
                // This vert is being promoted.
                dst.addVert( v );
            }

            // Rebuild edge list.
            rebuildEdges( v, src, dst );
        }

        // Push revert data to stack.
        //dst.mDiameter = Graphs.pseudoDiameter( dst.mVerts );
        return dst;
    }
    
    
    public static Graph uncoarsen( LayoutParams params, Graph src ) {
        Graph dst = src.mFinerGraph;
        if( dst == null ) {
            return null;
        }
        
        Vert nonce = new Vert();
        Edge reactivateEdges = dst.mEdges;
        
        // Loop through all the finel-level verts store in the dst graph and mark 
        // the owners with a unique nonce that indicates they will be discarded. 
        for( Vert v = dst.mVerts; v != null; v = v.mGraphNext ) {
            Vert owner = v.mGraphOwner;
            owner.mTempNext = nonce;
        }
        
        // Loop through source verts; skip those marked for removal.
        Edge loopEdge;
        Vert loopVert = src.mVerts;
        while( loopVert != null ) {
            Vert v = loopVert;
            loopVert  = loopVert.mGraphNext;

            if( v.mTempNext == nonce ) {
                v.mGraphNext = null;
            } else {
                dst.addVert( v );
                
                // Rebuild v.mEdges. Skip edges connected to nodes marked for removal.
                loopEdge = v.mEdges;
                v.mEdges = null;
                while( loopEdge != null ) {
                    Edge e   = loopEdge;
                    loopEdge = loopEdge.next( v );
                    
                    if( e.other( v ).mTempNext != nonce ) {
                        v.addEdge( e );
                        if( e.mA == v ) {
                            dst.addEdge( e );
                        }
                    }
                }
            }
        }
        
        // Re-install fine edges.
        loopEdge = reactivateEdges;
        while( loopEdge != null ) {
            Edge e = loopEdge;
            loopEdge = loopEdge.mGraphNext;
            e.mA.addEdge( e );
            e.mB.addEdge( e );
        }
        
        // Refine
        params.mRefine.refine( params, src, dst );
        
        // Clear out src.
        src.mVerts      = null;
        src.mVertNo     = 0;
        src.mEdges      = null;
        src.mEdgeNo     = 0;
        src.mFinerGraph = null;
        
        return dst;
    }
    
    /**
     * Overwrites mCellNext and mTempDist
     */
    public static int pseudoDiameter( Vert list ) {
        if( list == null ) {
            return 0;
        }
        
        int maxDiam = 0;
        Vert start = list;
        
        while( true ) {
            // Reset distance measure on each node.
            for( Vert v = list; v != null; v = v.mGraphNext ) {
                v.mTempDist = -1f;
            }
            
            // Initialize starting conditions and search queue.
            start.mTempDist = 0f;
            int diam = 1;
            Vert head = start;
            Vert tail = start;
            
            while( head != null ) {
                // Take vert off of queue.
                Vert v = head;
                head = head.mTempNext;
                v.mTempNext = null;
                
                diam = (int)( v.mTempDist + 1.5f );
                float childDist = diam;
                
                // Check all connected nodes.
                for( Edge e = v.mEdges; e != null; e = e.next( v ) ) {
                    Vert u = e.other( v );
                    if( u.mTempDist != -1f ) {
                        continue;
                    }
                    u.mTempDist   = childDist;
                    u.mTempNext = null;
                    if( head == null ) {
                        head = tail = u;
                    } else {
                        tail.mTempNext = u;
                        tail = u;
                    }   
                }
            }
            
            if( diam <= maxDiam ) {
                return maxDiam;
            }
            
            maxDiam = diam;
            start = tail;
        }
    }
    
    
    public static void labelIndex( Vert vertList ) {
        int label = 0;
        while( vertList != null ) {
            vertList.mTempDist = label++;
            vertList = vertList.mGraphNext;
        }
    }


    public static double computeScaledAtedgeLength( Vert verts ) {
        double et = 0.0;
        double vt = 0.0;
        
        for( Vert v = verts; v != null; v = v.mGraphNext ) {
            for( Edge e = v.mEdges; e != null; e = e.next( v ) ) {
                if( v != e.mA ) {
                    continue;
                }
                Vert u = e.mB;
                float dx = v.mX - u.mX;
                float dy = v.mY - u.mY;
                et += Math.sqrt( dx * dx + dy * dy );
            }
            
            for( Vert u = v.mGraphNext; u != null; u = u.mGraphNext ) {
                float dx = v.mX - u.mX;
                float dy = v.mY - u.mY;
                vt += Math.sqrt( dx * dx + dy * dy );
            }
        }

        return et / vt;
    }
    
    
    public static void assertValid( Graph graph ) {
        if( graph.mVertNo != size( graph.mVerts ) ) {
            throw new IllegalStateException( "Invalid vert list." );
        }
        if( graph.mEdgeNo != size( graph.mEdges ) ) {
            throw new IllegalStateException( "Invalid edge list." );
        }
        
        int edgeNo = 0;
        for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
            for( Edge e = v.mEdges; e != null; e = e.next( v ) ) {
                if( e.mA == v ) {
                    edgeNo++;
                }
            }
        }
        if( edgeNo != graph.mEdgeNo ) {
            throw new IllegalStateException( "Edge list for entire graph does not match per-vertex edge lists." );
        }
   }
    
    
    
    
    public static String formatEdges( Edge edgeList, int minDigits ) {
        StringBuilder s = new StringBuilder();
        String fmt = String.format( "(%%%dd,%%%dd) ", minDigits, minDigits );
        
        for( Edge e = edgeList; e != null; e = e.mGraphNext ) {
            int la = (int)( e.mA.mTempDist + 0.5f );
            int lb = (int)( e.mB.mTempDist + 0.5f );
            s.append( String.format( fmt, la, lb ) );
        }
        
        return s.toString();
    }
    
        
    public static String formatForwardEdges( Vert vertList, int minDigits ) {
        StringBuilder s = new StringBuilder();
        String fmt = String.format( "{%%%dd,%%%dd} ", minDigits, minDigits );
        
        for( Vert v = vertList; v != null; v = v.mGraphNext ) {
            Edge e = v.mEdges;
            while( e != null ) {
                if( e.mA == v ) {
                    int la = (int)( e.mA.mTempDist + 0.5f );
                    int lb = (int)( e.mB.mTempDist + 0.5f );
                    s.append( String.format( fmt, la, lb ) );
                }
                e = e.next( v );
            }
        }
        
        return s.toString();
    }
    
    
    public static String formatBackwardEdges( Vert vertList, int minDigits ) {
        StringBuilder s = new StringBuilder();
        String fmt = String.format( "{%%%dd,%%%dd} ", minDigits, minDigits );
        
        for( Vert v = vertList; v != null; v = v.mGraphNext ) {
            Edge e = v.mEdges;
            while( e != null ) {
                if( e.mB == v ) {
                    int la = (int)( e.mA.mTempDist + 0.5f );
                    int lb = (int)( e.mB.mTempDist + 0.5f );
                    s.append( String.format( fmt, la, lb ) );
                }
                e = e.next( v );
            }
        }
        
        return s.toString();
    }
    
    
    
    /**
     * Returns weighted average of two vertices.
     */
    static Vert combine( Vert a, Vert b, VertWeightModel weighter ) {
        final Vert c   = new Vert();
        final float aw = a.mWeight;
        final float bw = b.mWeight;
        final float scale = 1f / ( aw + bw );
        
        c.mX = scale * ( aw * a.mX + bw * b.mX );
        c.mY = scale * ( aw * a.mY + bw * b.mY );
        //c.mZ = scale * ( aw * a.mZ + bw * b.mZ );
        
        a.mGraphOwner = c;
        b.mGraphOwner = c;
        c.mWeight = weighter.combinedWeight( a, b );
        
        return c;
    }


    private static void rebuildEdges( Vert v, Graph src, Graph dst ) {
        Edge loopEdge = v.mEdges;
        v.mEdges = null;
        
        while( loopEdge != null ) {
            Edge e = loopEdge;
            loopEdge = loopEdge.next( v );
            
            // Check if responsible for replacing edge. Don't copy it twice.
            if( v != e.mA ) {
                // If neither nodes are being replaced, add the edge back.
                if( v.mGraphOwner == null && e.mA.mGraphOwner == null ) {
                    v.addEdge( e );
                }
                continue;
            }
            
            Vert father  = v.mGraphOwner;
            Vert brother = e.mB;
            Vert uncle   = brother.mGraphOwner;
            
            // Check if vert is being merged and is no longer valid.
            if( father != null ) {
                // Push edge.
                src.addEdge( e );
                
                // Check if other vertex in edge (sibling) is also being combined.
                if( uncle != null ) {
                    // Check if father is already linked to uncle.
                    if( father != uncle && !findAndAdd( father.mEdges, father, uncle, e.mWeight ) ) {
                        Edge newEdge = new Edge( father, uncle, e.mWeight );
                        father.addEdge( newEdge );
                        uncle.addEdge( newEdge );
                        dst.addEdge( newEdge );
                    }
                } else {
                    // Check if father is already linked to brother.
                    if( !findAndAdd( father.mEdges, father, brother, e.mWeight ) ) {
                        Edge newEdge = new Edge( father, brother, e.mWeight );
                        father.addEdge( newEdge );
                        brother.addEdge( newEdge );
                        dst.addEdge( newEdge );
                    }
                }
            } else {
                if( uncle != null ) {
                    // Push edge
                    src.addEdge( e );
                    
                    // Check if vert is already linked to uncle.
                    // Note that we are only checking edges already added back into vert.mEdges.
                    // This is fine because:
                    // 1. 'uncle' must be a new node to this level and must have been created on this coarsening event.
                    // 2. Because 'uncle' is new to this level, any edges to 'uncle' are also new to this level.
                    // 3. This method is allowed to remove edges only from the provided 'vert' Vert. For all other Verts,
                    //    'other', this method may only prepend new edges to 'other.mEdges.'
                    // 4. Therefore, if an earlier call to this method created an edge between 'vert' and 'uncle', it would
                    //    have been prepended to vert.mEdges.
                    // 5. All new edges are valid: 
                    //    edge.mA.mGraphCoarser == null && edge.mB.mGraphCoarser == null   ==>
                    //    father == null && uncle == null
                    // 6. Therefore, to reach this part of the code, all edges from 'vert'->'uncle' would already have been 
                    //    added back into vert.mEdges.
                    if( !findAndAdd( v.mEdges, v, uncle, e.mWeight ) ) {
                        Edge newEdge = new Edge( v, uncle, e.mWeight );
                        v.addEdge( newEdge );
                        uncle.addEdge( newEdge );
                        dst.addEdge( newEdge );
                    } 
                } else {
                    // Both nodes are valid. Promote edge to next level.
                    dst.addEdge( e );
                    v.addEdge( e );
                }
            }
        }
    }
    
    
    private static boolean findAndAdd( Edge edges, Vert owner, Vert target, float weight ) {
        while( edges != null ) {
            if( edges.contains( target ) ) {
                edges.mWeight += weight;
                return true;
            }
            
            edges = edges.next( owner );
        }
        
        return false;
    }
    
    
}
