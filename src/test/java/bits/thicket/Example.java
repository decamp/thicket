/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

import java.util.*;


/**
 * @author Philip DeCamp
 */
public class Example {

    public static void main( String[] args ) throws Exception {

        // Let's say you have some list of Things. Each Thing should be represented by a Vert object.
        List<Thing> things = new ArrayList<Thing>( 3 );
        for( int i = 0; i < 3; i++ ) {
            things.add( new Thing() );
        }

        // Create a graph and add all verts and edges.
        Graph graph = new Graph();
        for( Thing t: things ) {
            graph.addVert( t.mVert );
        }

        // Add some edges.
        graph.connect( things.get( 0 ).mVert, things.get( 1 ).mVert, 1.0f );
        graph.connect( things.get( 1 ).mVert, things.get( 2 ).mVert, 1.0f );
        graph.connect( things.get( 2 ).mVert, things.get( 0 ).mVert, 0.5f );

        // Initialize graph with randomized positions.
        Graphs.randomizePositions2( graph.mVerts, new float[]{ -1f, -1f, 1f, 1f }, null );

        // Configure layout parameters.
        LayoutParams params         = new LayoutParams();
        params.mDim                 = 2;
        params.mRand                = new Random( 1 );
        params.mMultilevel          = false;
        params.mVertWeightModel     = VertWeightModel.PREDEFINED;

        params.mGravityForce        = 0.1f;
        params.mAttractEq           = AttractEq.SQUARE_DIST;
        params.mRepulseEq           = RepulseEq.INV_SQUARE_DIST;
        params.mRepulseApprox       = true;

        params.mUpdateTol           = 0.004f;
        params.mUpdateCoarseTol     = 0.075f;
        params.mUpdateInitialStep   = 0.4f;
        params.mUpdateIncrementStep = 0.85f;


        LayoutSolver solver = new LayoutSolver();
        solver.init( params, graph );

        while( !solver.converged() ) {
            solver.step();
        }

        for( Thing t: things ) {
            System.out.println( t.mVert.formatPosition() );
        }

    }


    public static class Thing {
        Vert mVert = new Vert();
    }

}
