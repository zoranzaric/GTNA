package gtna.networks.model;

import gtna.graph.Edges;
import gtna.graph.Graph;
import gtna.networks.Network;
import gtna.networks.NetworkImpl;
import gtna.routing.RoutingAlgorithm;
import gtna.routing.node.GridNode;
import gtna.routing.node.identifier.GridID;
import gtna.transformation.Transformation;
import gtna.util.Timer;

import java.util.Random;

public class UnitDisc extends NetworkImpl implements Network {
	private int AREA_WIDTH;

	private int AREA_HEIGHT;

	private int RADIUS_MIN;

	private int RADIUS_MAX;

	public UnitDisc(int nodes, int AREA_WIDTH, int AREA_HEIGHT, int RADIUS_MIN,
			int RADIUS_MAX, RoutingAlgorithm ra, Transformation[] t) {
		super("UNIT_DISC", nodes, new String[] { "AREA_WIDTH", "AREA_HEIGHT",
				"RADIUS_MIN", "RADIUS_MAX" }, new String[] { "" + AREA_WIDTH,
				"" + AREA_HEIGHT, "" + RADIUS_MIN, "" + RADIUS_MAX }, ra, t);
		this.AREA_WIDTH = AREA_WIDTH;
		this.AREA_HEIGHT = AREA_HEIGHT;
		this.RADIUS_MIN = RADIUS_MIN;
		this.RADIUS_MAX = RADIUS_MAX;
	}

	public Graph generate() {
		Timer timer = new Timer();
		Random rand = new Random(System.currentTimeMillis());
		UDNode[] nodes = new UDNode[this.nodes()];
		for (int i = 0; i < nodes.length; i++) {
			double x = rand.nextDouble() * this.AREA_WIDTH;
			double y = rand.nextDouble() * this.AREA_HEIGHT;
			double radius = this.RADIUS_MIN + rand.nextDouble()
					* (this.RADIUS_MAX - this.RADIUS_MIN);
			nodes[i] = new UDNode(i, x, y, radius, nodes);
		}
		Edges edges = new Edges(nodes, 100);
		for (int i = 0; i < nodes.length; i++) {
			for (int j = i + 1; j < nodes.length; j++) {
				if (nodes[i].reaches(nodes[j])) {
					edges.add(nodes[i], nodes[j]);
					edges.add(nodes[j], nodes[i]);
				}
			}
		}
		edges.fill();
		timer.end();
		Graph graph = new Graph(this.description(), nodes, timer);
		return graph;
	}

	private class UDNode extends GridNode {
		private double x;

		private double y;

		private double radius;

		private UDNode(int index, double x, double y, double radius,
				UDNode[] nodes) {
			super(index, new GridID(new double[] { x, y }));
			this.x = x;
			this.y = y;
			this.radius = radius;
		}

		private boolean reaches(UDNode node) {
			double xd = this.x - node.x;
			double yd = this.y - node.y;
			double d = Math.sqrt(xd * xd + yd * yd);
			return d <= this.radius && d <= node.radius;
		}
	}
}
