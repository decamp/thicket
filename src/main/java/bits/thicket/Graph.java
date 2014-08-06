/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

import java.util.Collection;


/**
 * @author decamp
 */
public class Graph {
    
    /**
     * Vertices for this graph. Verts are connected by 'mGraphNext' field as singly-linked list.
     */
    public Vert mVerts;
    
    /**
     * Number of vertices in graph. MUST match actual length of mVerts.
     */
    public int mVertNo;
    
    /**
     * Edges for this graph. Edges are connected by 'mGraphNext' field as singly-linked list.
     */
    public Edge mEdges;
    
    /**
     * Number of edges in graph. MUST match actual length of mEdges.
     */
    public int mEdgeNo;
    
    /**
     * Number of times this graph has been coarsened.
     */
    public int mCoarseLevel;
    
    /**
     * Link to the next finer level of this graph, or <code>null</code> if <code>mDetailLevel == 0</code>.
     */
    public Graph mFinerGraph;
    
    
    
    public Graph() {}
    
    
    public Graph( Vert vertList ) {
        install( vertList );
    }
 
    
    public Graph( Collection<Vert> verts ) {
        install( verts );
    }


    /**
     * Adds a Vert to the graph.
     * NOTE: Adding a Vert to a graph multiple times will result in undefined behavior.
     */
    public void addVert( Vert v ) {
        v.mGraphNext = mVerts;
        mVerts = v;
        mVertNo++;
    }
    
    /**
     * Creates edge to connect two vertices, adds that edge
     * to both vertices, and adds the edge to the graph.
     * NOTE: Both vertices MUST already be members of the graph.
     * Connecting a Vert to itself causes undefined behavior.
     * 
     * @param a      Some Vert in the graph.
     * @param b      Some Vert in the graph. 
     * @param weight Weight of edge.
     */
    public Edge connect( Vert a, Vert b, float weight ) {
        Edge e = new Edge( a, b, weight );
        addEdge( e );
        a.addEdge( e );
        b.addEdge( e );
        return e;
    }


    /**
     * WARNING: Does not update edge lists of individual Verts.
     * May result in inconsistent graph.
     *
     * Adds an edge to the graph directly.
     */
    void addEdge( Edge e ) {
        e.mGraphNext = mEdges;
        mEdges = e;
        mEdgeNo++;
    }

    
    void install( Vert verts ) {
        mVerts       = verts;
        mVertNo      = 0;
        mEdges       = null;
        mEdgeNo      = 0;
        mCoarseLevel = 0;
        mFinerGraph  = null;
        
        for( Vert v = verts; v != null; v = v.mGraphNext ) {
            mVertNo++;
            
            for( Edge e = v.mEdges; e != null; e = e.next( v ) ) {
                addEdge( e );
            }
        }
    }
    
    
    void install( Collection<Vert> verts ) {
        mVertNo      = 0;
        mEdges       = null;
        mEdgeNo      = 0;
        mCoarseLevel = 0;
        mFinerGraph  = null;
        
        for( Vert v: verts ) {
            addVert( v );
            for( Edge e = v.mEdges; e != null; e = e.next( v ) ) {
                if( e.mA == v ) {
                    addEdge( e );
                }
            }
        }
    }
    

}
