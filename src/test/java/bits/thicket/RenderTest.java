/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

import java.awt.*;
import java.io.File;
import java.util.Random;

import bits.util.gui.ImagePanel;
import com.jogamp.opengl.*;
import javax.swing.JFrame;

import static com.jogamp.opengl.GL.*;

import bits.glui.*;


/**
 * @author decamp
 */
public class RenderTest {
    
    
    public static void main( String[] args ) throws Exception {
        testRender();
        //testLocalAdaptive();
    }
    
    
    static void testCoarsen() throws Exception {
        final int DIM = 3;
        final int MAX_LEVEL = 3;
        
        LayoutParams params = new LayoutParams();
        params.mDim    = DIM;
        params.mRand   = new Random( 0 );
        params.mRefine = RefineFunc.NOTHING;
        
        //Graph graph = ColParser.parse( new File( "../test/resources/aframe.col" ), DIM );
        Graph graph = ColParser.parse( new File( "../test/resources/ball.col" ), DIM );
        //Graph graph = ColParser.parse( new File( "../test/resources/latin_square.col" ), DIM );
        //Graph graph = ColParser.parse( new File( "../test/resources/celegans_n306.col" ), DIM );
        //Graph graph = new Graph( WattsStrogatzGenerator.generate( null, 2, 50, 6, 0.06 ) );
        
        int imSize = 1300;
        Graphs.assertValid( graph );
        
        while( true ) {
            Graphs.labelIndex( graph.mVerts );
            ImagePanel.showImage( GraphDrawer.draw( graph, imSize, imSize ) );
            
            //System.out.println();
            //System.out.println( Graphs.formatEdges( graph.edgeList(), 3 ) );
            //System.out.println( Graphs.formatForwardEdges( graph.vertList(), 3 ) );
            //System.out.println( Graphs.formatBackwardEdges( graph.vertList(), 3 ) );
            
            if( graph.mVertNo <= 1 || graph.mCoarseLevel >= MAX_LEVEL ) {
                break;
            }
            
            graph = Graphs.coarsen( params, graph );
            Graphs.assertValid( graph );
        }
        
        while( graph.mCoarseLevel > 0 ) {
            graph = Graphs.uncoarsen( params, graph );
            Graphs.assertValid( graph );
            Graphs.labelIndex( graph.mVerts );
            //ImagePanel.showImage( GraphDrawer.draw( graph, imSize, imSize ) );
            
            //System.out.println();
            //System.out.println( Graphs.formatEdges( graph.edgeList(), 3 ) );
            //System.out.println( Graphs.formatForwardEdges( graph.vertList(), 3 ) );
            //System.out.println( Graphs.formatBackwardEdges( graph.vertList(), 3 ) );
        }
        
        System.out.println( "Complete" );
    }
            
    
    static void testRender() throws Exception {
        final float lineAlpha = 1f;
        int dim = 3;
        int imSize = 720;
        
        LayoutParams params         = new LayoutParams();
        params.mDim                 = dim;
        params.mRand                = new Random( 1 );
        params.mMultilevel          = true;
        params.mVertWeightModel     = VertWeightModel.PREDEFINED;
        
        params.mGravityForce        = 0.1f;
        params.mAttractEq           = AttractEq.SQUARE_DIST;
        params.mRepulseEq           = RepulseEq.INV_SQUARE_DIST;
        params.mRepulseApprox       = true;
        
        params.mUpdateTol           = 0.004f;
        params.mUpdateCoarseTol     = 0.075f;
        params.mUpdateInitialStep   = 0.4f;
        params.mUpdateIncrementStep = 0.85f;
        
        float[] bounds = { -10f, -10f, -10f, 10f, 10f, 10f };
        
//        Graph graph = ColParser.parse( new File( "../test/resources/aframe.col" ), dim );
//        Graph graph = ColParser.parse( new File( "../test/resources/ball.col" ), dim );
//        Graph graph = ColParser.parse( new File( "../test/resources/us_powergrid_n4941.col" ), dim );
//        Graph graph = ColParser.parse( new File( "../test/resources/oclinks_w.col" ), dim );
//        Graph graph = ColParser.parse( new File( "../test/resources/celegans_n306.col" ), dim );
//        Graph graph = ColParser.parse( new File( "../test/resources/dsjr500.5.col" ), dim );
        Graph graph = GraphFileParser.parse( new File( "../test/resources/4elt.graph" ) );
//        Graph graph = WattsStrogatzGenerator.generate( params.mRand, dim, 10000, 20, 0.002 );

        System.out.println( "Graph Verts: " + graph.mVertNo );

        Graphs.assertValid( graph );
        Graphs.randomizePositions3( graph.mVerts, bounds, params.mRand );

        GLProfile profile = GLProfile.get( GLProfile.GL3 );
        GLCapabilities caps = new GLCapabilities( profile );
        caps.setNumSamples( 1 );
        caps.setSampleBuffers( false );
        caps.setDepthBits( 16 );
        caps.setDoubleBuffered( true );
        
        final GRootController cont = GRootController.create( caps );
        cont.setClearColor( 0.15f, 0.15f, 0.15f, 1f );
        cont.setClearBits( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
        GraphPanel panel = new GraphPanel( params, graph, lineAlpha );
        cont.rootPane().addChild( panel );
        cont.rootPane().setLayout( new GLayout() {
            @Override
            public void layoutPane( GComponent pane ) {
                int w = pane.width();
                int h = pane.height();
                for( GComponent c: pane.children() ) {
                    c.setBounds( 0, 0, w, h );
                }
            }
        });

        JFrame frame = new JFrame( "Graph Layout" );
        cont.component().setPreferredSize( new Dimension( imSize * 4 / 3, imSize ) );
        frame.add( cont.component(), BorderLayout.CENTER );
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );

        if( false ) {
            panel.exporter().addVideoWriter(
                    new File( "/tmp/thicket_4elt_multilevel.mp4" ),
                    20,
                    -1,
                    0,
                    Long.MAX_VALUE
            );
        }

        cont.startAnimator( 30.0 );
    }

    
    static void testQuality() throws Exception {
        final LayoutParams params = new LayoutParams();
        params.mDim = 2;
        params.mMultilevel = true;
        params.mRand = new Random( 0 );
        params.mRefine = RefineFunc.EXPAND_PERTURB;
        
        int imSize = 1300;
        float[] bounds = ( params.mDim == 2 ? 
                           new float[]{ -10f, -10f, 10f, 10f } : 
                           new float[]{ -10f, -10f, -10f, 10f, 10f, 10f } );
        
        //File file = new File( "resources_test/latin_square.col" );
        //File file = new File( "resources_test/us_powergrid_n4941.col" );
        //File file = new File( "resources_test/oclinks_w.col" );
        //Graph graph = ColParser.parse( new File( "resources_test/celegans_n306.col" ), params.mDim );
        Graph graph = WattsStrogatzGenerator.generate( params.mRand, params.mDim, 5000, 30, 0.001 );
        
        System.out.println( "Verts: " + graph.mVertNo + "    Edges: " + graph.mEdgeNo );
        System.out.println( "Q_start: " + Graphs.computeScaledAtEdgeLength( graph.mVerts ) * 1000.0 );
        Graphs.randomizePositions3( graph.mVerts, bounds, params.mRand );
        System.out.println( "Q_start: " + Graphs.computeScaledAtEdgeLength( graph.mVerts ) * 1000.0 );
        Graphs.assertValid( graph );
        
        //ImagePanel.showImage( GraphDrawer.draw( graph, imSize, imSize ) );
        LayoutSolver solver = new LayoutSolver();
        int step = 0;
        
        while( true ) {
            solver.init( params, graph );
            while( !solver.converged() ) {
                solver.step();
            }
            
            //ImagePanel.showImage( GraphDrawer.draw( graph, imSize, imSize ) );
            
            if( graph.mCoarseLevel == 0 ) {
                break;
            }
        
            System.out.println( "Uncoarsening" );
            graph = Graphs.uncoarsen( params, graph );
        }
        
        System.out.println( "Q_stop: " + Graphs.computeScaledAtEdgeLength( graph.mVerts ) * 1000.0 );
        
        //BufferedImage im = GraphDrawer.draw( graph, imSize, imSize );
        //ImagePanel.showImage( im );
    }

    
    static void testQuadtree() throws Exception {
        float[] bounds = { 0, 0, 10, 10 };
        int imSize = 800;
        
        //Graph graph = ColParser.parse( new File( "resources_test/celegans_n306.col" ), 2 );
        Random rand = new Random( 2 );
        
        Vert verts = null;
        for( int i = 0; i < 300; i++ ) {
            Vert v = new Vert();
            v.mGraphNext = verts;
            verts = v;
        }
        Graph graph = new Graph( verts );
        
        Graphs.randomizePositions3( graph.mVerts, bounds, rand );
        
        Quadtree2 tree = new Quadtree2();
        tree.rebuild( graph.mVerts, 10, null, 0f );
        
        ImagePanel.showImage( QuadtreePainter.draw( tree, imSize, imSize ) ).setLocationRelativeTo( null );
        
    }

    
    static void testSpeed() throws Exception {
        final LayoutParams params = new LayoutParams();
        params.mDim        = 3;
        params.mMultilevel = true;
        params.mRand       = new Random( 1 );
        params.mUpdateTol  = 0.01f;
        params.mUpdateCoarseTol = 0.1f;
        params.mUpdateIncrementStep = 0.85f;
        params.mUpdateInitialStep   = 0.4f;
        params.mRepulseApproxMaxTreeDepth = 8;
        
        float[] bounds = new float[]{ -10f, -10f, -10f, 10f, 10f, 10f };
        //File file = new File( "resources_test/latin_square.col" );
        //File file = new File( "resources_test/us_powergrid_n4941.col" );
        //File file = new File( "resources_test/oclinks_w.col" );
        //Graph graph = ColParser.parse( new File( "resources_test/celegans_n306.col" ), params.mDim );
        //Graph graph = new Graph( WattsStrogatzGenerator.generate( params.mRand, params.mDim, 3000, 30, 0.001 ) );
        Graph graph = WattsStrogatzGenerator.generate( params.mRand, params.mDim, 3000, 30, 0.001 );
        
        long total = 0;
        int trials = 3;
        
        for( int i = 0; i < trials; i++ ) {
            Graphs.randomizePositions3( graph.mVerts, bounds, params.mRand );
            
            LayoutSolver solver = new LayoutSolver();
            solver.init( params, graph );

            long startNanos = System.nanoTime();
            while( !solver.converged() ) {
                solver.step();
            }
            
            long dur = System.nanoTime() - startNanos;
            double qual = Graphs.computeScaledAtEdgeLength( graph.mVerts );
            System.out.println( ( dur / 1000000000.0 ) + "\t Q: " + qual );
            total += dur;
        }
        
        System.out.println( "Ave: " + ( total / 1000000000.0 / trials ) );
    }

    
    static void testLocalAdaptive() throws Exception {
        final float lineAlpha = 1f;
        int dim = 3;
        int imSize = 1300;
        
        LayoutParams params         = new LayoutParams();
        params.mDim                 = dim;
        params.mRand                = new Random( 1 );
        params.mMultilevel          = false;
        params.mVertWeightModel     = VertWeightModel.PREDEFINED;
        
        params.mGravityForce        = 0.1f;
        params.mAttractEq           = AttractEq.SQUARE_DIST;
        params.mRepulseEq           = RepulseEq.INV_SQUARE_DIST;
        params.mRepulseApprox       = true;
        
        params.mUpdatePhase         = new LocalAdaptiveUpdatePhase();
        params.mUpdateTol           = 0.004f;
        params.mUpdateCoarseTol     = 0.075f;
        params.mUpdateInitialStep   = 0.4f;
        params.mUpdateIncrementStep = 0.85f;
        
        float[] bounds = { -10f, -10f, -10f, 10f, 10f, 10f };
        
        //Graph graph = ColParser.parse( new File( "../test/resources/aframe.col" ), dim );
        //Graph graph = ColParser.parse( new File( "../test/resources/ball.col" ), dim );
        //Graph graph = ColParser.parse( new File( "../test/resources/us_powergrid_n4941.col" ), dim );
        //Graph graph = ColParser.parse( new File( "../test/resources/oclinks_w.col" ), dim );
        //Graph graph = ColParser.parse( new File( "../test/resources/celegans_n306.col" ), dim );
        //Graph graph = ColParser.parse( new File( "../test/resources/dsjr500.5.col" ), dim );
        Graph graph = GraphFileParser.parse( new File( "../test/resources/4elt.graph" ) );
        //Graph graph = WattsStrogatzGenerator.generate( params.mRand, dim, 10000, 20, 0.002 );
        //Graph graph = new Graph( WattsStrogatzGenerator.generate( null, dim, 200, 15, 0.05 ) );
        
        Graphs.assertValid( graph );
        Graphs.randomizePositions3( graph.mVerts, bounds, params.mRand );
        
        GLCapabilities caps = new GLCapabilities( GLProfile.get( GLProfile.GL3 ) );
        caps.setNumSamples( 1 );
        caps.setSampleBuffers( false );
        caps.setDepthBits( 16 );
        caps.setDoubleBuffered( true );
        
        final GRootController cont = GRootController.create( caps );
        cont.setClearColor( 0.15f, 0.15f, 0.15f, 1f );
        cont.setClearBits( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
        cont.rootPane().addChild( new GraphPanel( params, graph, lineAlpha ) );
        cont.rootPane().setLayout( new GLayout() {
            @Override
            public void layoutPane( GComponent pane ) {
                int w = pane.width();
                int h = pane.height();
                for( GComponent c: pane.children() ) {
                    c.setBounds( 0, 0, w, h );
                }
            }
        });

        JFrame frame = new JFrame( "Graph Layout" );
        frame.add( cont.component(), BorderLayout.CENTER );
        frame.setSize( imSize, imSize + 22 );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
        
        cont.startAnimator( 30.0 );
    }

    
}
