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
public class QuadtreePainter {
    
    public static BufferedImage draw( Quadtree2 tree, int w, int h ) {
        BufferedImage ret = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g      = (Graphics2D)ret.getGraphics();
        Random rand       = new Random( 0 );
        
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setBackground( Color.BLACK );
        g.clearRect( 0, 0, w, h );
        
        QuadtreeCell root = tree.root();
        if( root == null ) {
            return ret;
        }
    
        float sx = w / ( root.mHalfSize * 2.0f );
        float sy = h / ( root.mHalfSize * 2.0f );
        float tx = ( root.mHalfSize - root.mX ) * sx;
        float ty = ( root.mHalfSize - root.mY ) * sy;

        fillQuads( root, sx, sy, tx, ty, 0, g );
        
        g.setColor( Color.BLUE );
        drawLines( root, sx, sy, tx, ty, g );
        
        g.setColor( Color.RED );
        drawPoints( root, sx, sy, tx, ty, g );
        
        return ret;
    }
    
    
    private static void fillQuads( QuadtreeCell cell, float sx, float sy, float tx, float ty, int depth, Graphics2D out ) {
        
        if( cell.mVerts != null ) {
            float val = Math.min( 10, depth ) / 10.0f;
            out.setColor( new Color( 0, val, 0 ) );

            int x0 = (int)( ( cell.mX - cell.mHalfSize ) * sx + tx + 0.5f );
            int x2 = (int)( ( cell.mX + cell.mHalfSize ) * sx + tx + 0.5f );
            int y0 = (int)( ( cell.mY - cell.mHalfSize ) * sy + ty + 0.5f );
            int y2 = (int)( ( cell.mY + cell.mHalfSize ) * sy + ty + 0.5f );
            out.fillRect( x0, y0, x2 - x0, y2 - y0 );
        }
        
        for( QuadtreeCell n: cell.mChildren ) {
            if( n != null ) {
                fillQuads( n, sx, sy, tx, ty, depth + 1, out );
            }
        }
    }
    
    
    private static void drawLines( QuadtreeCell cell, float sx, float sy, float tx, float ty, Graphics2D out ) {
        if( cell.mVerts != null ) {
            return;
        }
        
        int x0 = (int)( ( cell.mX - cell.mHalfSize ) * sx + tx + 0.5f );
        int x1 = (int)( ( cell.mX             ) * sx + tx + 0.5f );
        int x2 = (int)( ( cell.mX + cell.mHalfSize ) * sx + tx + 0.5f );
        int y0 = (int)( ( cell.mY - cell.mHalfSize ) * sy + ty + 0.5f );
        int y1 = (int)( ( cell.mY             ) * sy + ty + 0.5f );
        int y2 = (int)( ( cell.mY + cell.mHalfSize ) * sy + ty + 0.5f );
        
        out.drawLine( x0, y1, x2, y1 );
        out.drawLine( x1, y0, x1, y2 );
        
        for( QuadtreeCell n: cell.mChildren ) {
            if( n != null ) {
                drawLines( n, sx, sy, tx, ty, out );
            }
        }
    }

    
    private static void drawPoints( QuadtreeCell cell, float sx, float sy, float tx, float ty, Graphics2D out ) {
        Vert p = cell.mVerts;
        
        while( p != null ) {
            int x = (int)( p.mX * sx + tx + 0.5f );
            int y = (int)( p.mY * sy + ty + 0.5f );
            out.fillOval( x - 3, y - 3, 7, 7 );
            p = p.mTempNext;
        }
        
        for( QuadtreeCell n: cell.mChildren ) {
            if( n != null ) {
                drawPoints( n, sx, sy, tx, ty, out );
            }
        }
    }

}
