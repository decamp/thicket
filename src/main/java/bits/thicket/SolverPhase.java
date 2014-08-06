/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

/**
 * @author decamp
 */
public interface SolverPhase {
    public void init( LayoutParams params, Graph graph );
    public void step( LayoutParams params, Graph graph );
    public void dispose( LayoutParams params, Graph graph );
}
