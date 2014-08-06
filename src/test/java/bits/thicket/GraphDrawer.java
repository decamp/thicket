/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;



/**
 * @author decamp
 */
public class GraphDrawer {
    
    
    public static BufferedImage draw( Graph graph, int w, int h ) {
        float[] box = new float[4];
        Graphs.computeBounds2( graph.mVerts, box );
        return draw( graph, w, h, box );
    }
    
    
    public static BufferedImage draw( Graph graph, int w, int h, float[] box ) {
        BufferedImage ret = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g      = (Graphics2D)ret.getGraphics();
        Random rand       = new Random( 0 );
        
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
        g.setBackground( Color.BLACK );
        g.clearRect( 0, 0, w, h );
        
        float dim = Math.max( box[2] - box[0], box[3] - box[1] ) * 1.1f;
        float sx  = w / dim;
        float sy  = h / dim;
        float tx  = -0.5f * ( box[2] + box[0] - dim ) * sx; 
        float ty  = -0.5f * ( box[3] + box[1] - dim ) * sy;
        
        g.setColor( Color.GREEN );
        drawLines( graph.mVerts, sx, sy, tx, ty, g );
        
        g.setColor( Color.RED );
        drawVerts( graph.mVerts, sx, sy, tx, ty, g );
        //drawLabels( tree.vertList(), sx, sy, tx, ty, g );
        
        return ret;
    }
    
    
    private static void drawLines( Vert vert, float sx, float sy, float tx, float ty, Graphics2D out ) {
        while( vert != null ) {
            Edge e = vert.mEdges;
            
            while( e != null ) {
                if( e.mA != vert ) {
                    e = e.mBNext;
                } else {
                    Vert b = e.mB;
                
                    int x0 = (int)( vert.mX * sx + tx + 0.5f );
                    int x1 = (int)( b.mX * sx + tx + 0.5f );
                    int y0 = (int)( vert.mY * sy + ty + 0.5f );
                    int y1 = (int)( b.mY * sy + ty + 0.5f );
            
                    float width = Math.min( 2f, e.mWeight / 4f );
                    out.setStroke( new BasicStroke( width ) );
                    out.drawLine( x0, y0, x1, y1 );
                    
                    e = e.mANext;
                }
            }
            
            vert = vert.mGraphNext;
        }
    }


    private static void drawVerts( Vert v, float sx, float sy, float tx, float ty, Graphics2D out ) {
        Font font = new Font( "Verdana", Font.PLAIN, 12 );
        out.setFont( font );
        
        while( v != null ) {
            int x = (int)( v.mX * sx + tx + 0.5f );
            int y = (int)( v.mY * sy + ty + 0.5f );
            out.fillOval( x - 2, y - 2, 5, 5 );
            v = v.mGraphNext;
        }

    }
    
    
    private static void drawLabels( Vert v, float sx, float sy, float tx, float ty, Graphics2D out ) {
        Font font = new Font( "Verdana", Font.PLAIN, 12 );
        out.setFont( font );
        
        while( v != null ) {
            int x = (int)( v.mX * sx + tx + 0.5f );
            int y = (int)( v.mY * sy + ty + 0.5f );
            out.drawString( "" + (int)( v.mTempDist + 0.5f ), x - 3, y - 4 );
            out.fillOval( x - 2, y - 2, 5, 5 );
            v = v.mGraphNext;
        }

    }
    
}

        