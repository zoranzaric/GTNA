/* ===========================================================
 * GTNA : Graph-Theoretic Network Analyzer
 * ===========================================================
 *
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors
 *
 * Project Info:  http://www.p2p.tu-darmstadt.de/research/gtna/
 *
 * GTNA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GTNA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * ---------------------------------------
 * GraphPlotter.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: Nico;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.plot;

import gtna.graph.Graph;

/**
 * @author Nico
 *
 */
public class GraphPlotter {
	private Gephi gephi;
	private String basename, extension;

	public String getBasename() {
		return this.basename;
	}

	public GraphPlotter( String basename, String extension) {
		this.gephi = new Gephi();
		this.basename = basename;
		this.extension = extension;
	}
	
	public void plot(Graph g) {
		
	}
	
	public void plot(Graph g, String filename) {
		gephi.Plot(g, filename + "." + extension);
	}
	
	public void plotIteration(Graph g, int iteration) {
		plot(g, basename + "-" + iteration);
	}
	
	public void plotFinalGraph(Graph g) {
		plot(g, basename + "-final");
	}
}
