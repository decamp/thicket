/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

/**
 * @author decamp
 */
public interface UpdatePhase extends SolverPhase {
    public boolean converged();
    public double cost();
}
