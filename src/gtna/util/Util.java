package gtna.util;

import gtna.data.Series;
import gtna.data.Singles;
import gtna.data.Value;
import gtna.graph.Edge;
import gtna.graph.Node;
import gtna.graph.NodeImpl;
import gtna.networks.Network;
import gtna.plot.PlotData;
import gtna.routing.RoutingAlgorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

public class Util {

	// ///////////////////////
	// STATISTICS
	// ///////////////////////

	public static final boolean isBiasCorrected = false;

	public static double getStandardDeviation(double[] values) {
		StandardDeviation sd = new StandardDeviation(isBiasCorrected);
		double value = sd.evaluate(values);
		if (!Double.isNaN(value)) {
			return value;
		}
		int counter = 0;
		for (int i = 0; i < values.length; i++) {
			if (!Double.isNaN(values[i])) {
				counter++;
			}
		}
		double[] newValues = new double[counter];
		int index = 0;
		for (int i = 0; i < values.length; i++) {
			if (!Double.isNaN(values[i])) {
				newValues[index++] = values[i];
			}
		}
		double newValue = sd.evaluate(newValues);
		return newValue;
	}

	public static double[] getConfidenceInterval(double mean,
			double standardDeviation, int n, double alpha) {
		double standardError = standardDeviation / Math.sqrt(n);
		NormalDistributionImpl nd = new NormalDistributionImpl();
		double z;
		try {
			z = nd.inverseCumulativeProbability((double) 1 - (double) alpha
					/ (double) 2);
			double from = mean - z * standardError;
			double to = mean + z * standardError;
			return new double[] { from, to };
		} catch (MathException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ///////////////////////
	// toFolderString
	// ///////////////////////
	public static String toFolderString(int[] values) {
		StringBuffer buff = new StringBuffer("" + values[0]);
		for (int i = 1; i < values.length; i++) {
			buff.append("-" + values[i]);
		}
		return buff.toString();
	}

	public static String toFolderString(int[][] values) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				if (i == 0 && j == 0) {
					buff.append(values[i][j]);
				} else if (j == 0) {
					buff.append("--" + values[i][j]);
				} else {
					buff.append("-" + values[i][j]);
				}
			}
		}
		return buff.toString();
	}

	public static String toFolderStringDouble(double[][] values) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				if (i == 0 && j == 0) {
					buff.append(values[i][j]);
				} else if (j == 0) {
					buff.append("--" + values[i][j]);
				} else {
					buff.append("-" + values[i][j]);
				}
			}
		}
		return buff.toString();
	}

	public static String toFolderString(double[] values) {
		StringBuffer buff = new StringBuffer("" + values[0]);
		for (int i = 1; i < values.length; i++) {
			buff.append("-" + values[i]);
		}
		return buff.toString();
	}

	public static String toFolderString(double[][] values) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				if (i == 0 && j == 0) {
					buff.append(values[i][j]);
				} else if (j == 0) {
					buff.append("--" + values[i][j]);
				} else {
					buff.append("-" + values[i][j]);
				}
			}
		}
		return buff.toString();
	}

	// ///////////////////////
	// randomize int array
	// ///////////////////////
	public static int[] random(int[] values, Random rand) {
		int[] sorted = values.clone();
		randomize(sorted, rand, 0, sorted.length - 1);
		return sorted;
	}

	private static void randomize(int[] values, Random rand, int from, int to) {
		for (int i = to - from; i > 1; i--) {
			int index = rand.nextInt(i) + from;
			int temp = values[index];
			values[index] = values[i + from];
			values[i + from] = temp;
		}
	}

	// ///////////////////////
	// array from index
	// ///////////////////////
	public static double[] arrayFromIndex(double[][] values, int index) {
		double[] array = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			array[i] = values[i][index];
		}
		return array;
	}

	// ///////////////////////
	// combine
	// ///////////////////////
	public static RoutingAlgorithm[] combine(RoutingAlgorithm[] ra1,
			RoutingAlgorithm[] ra2) {
		RoutingAlgorithm[] ra = new RoutingAlgorithm[ra1.length + ra2.length];
		for (int i = 0; i < ra1.length; i++) {
			ra[i] = ra1[i];
		}
		for (int i = 0; i < ra2.length; i++) {
			ra[i + ra1.length] = ra2[i];
		}
		return ra;
	}

	public static Network[] combine(Network[] nw1, Network[] nw2) {
		return combine(new Network[][] { nw1, nw2 });
	}

	public static Network[] combine(Network[][] nws) {
		int counter = 0;
		for (int i = 0; i < nws.length; i++) {
			counter += nws[i].length;
		}
		Network[] nw = new Network[counter];
		int index = 0;
		for (int i = 0; i < nws.length; i++) {
			for (int j = 0; j < nws[i].length; j++) {
				nw[index++] = nws[i][j];
			}
		}
		return nw;
	}

	public static Series[] combine(Series[] s1, Series[] s2) {
		return combine(new Series[][] { s1, s2 });
	}

	public static Series[] combine(Series[][] s) {
		int counter = 0;
		for (int i = 0; i < s.length; i++) {
			counter += s[i].length;
		}
		Series[] c = new Series[counter];
		int index = 0;
		for (int i = 0; i < s.length; i++) {
			for (int j = 0; j < s[i].length; j++) {
				c[index++] = s[i][j];
			}
		}
		return c;
	}

	public static Series[][] combine(Series[][] s1, Series[][] s2) {
		Series[][] s = new Series[s1.length + s2.length][];
		for (int i = 0; i < s1.length; i++) {
			s[i] = s1[i];
		}
		for (int j = 0; j < s2.length; j++) {
			s[j + s1.length] = s2[j];
		}
		return s;
	}

	public static Series[][] combine(Series[][][] s1) {
		int counter = 0;
		for (int i = 0; i < s1.length; i++) {
			counter += s1[i].length;
		}
		Series[][] s = new Series[counter][];
		int index = 0;
		for (int i = 0; i < s1.length; i++) {
			for (int j = 0; j < s1[i].length; j++) {
				s[index++] = s1[i][j];
			}
		}
		return s;
	}

	// ///////////////////////
	// contains
	// ///////////////////////

	public static boolean contains(String[] keys, String key) {
		for (int i = 0; i < keys.length; i++) {
			if (key.equals(keys[i])) {
				return true;
			}
		}
		return false;
	}

	// ///////////////////////
	// Casting
	// ///////////////////////

	public static double[] toDouble(int[] values) {
		double[] array = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			array[i] = (double) values[i];
		}
		return array;
	}

	// ///////////////////////
	// ArrayList to Array
	// ///////////////////////
	public static Node[] toNodeArray(ArrayList<Node> list) {
		Node[] array = new Node[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static Node[] toNodeArray(HashSet<Node> set) {
		Iterator<Node> iter = set.iterator();
		Node[] array = new Node[set.size()];
		int i = 0;
		while (iter.hasNext()) {
			array[i++] = iter.next();
		}
		return array;
	}

	public static String[] toStringArray(Vector<String> v) {
		String[] array = new String[v.size()];
		Iterator<String> iter = v.listIterator();
		int index = 0;
		while (iter.hasNext()) {
			array[index++] = iter.next();
		}
		return array;
	}

	public static String[] toStringArray(ArrayList<String> list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static double[] toDoubleArray(ArrayList<Double> list) {
		double[] array = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static int[] toIntegerArray(ArrayList<Integer> list) {
		int[] array = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static long[] toLongArray(ArrayList<Long> list) {
		long[] array = new long[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static PlotData[] toPlotDataArray(ArrayList<PlotData> list) {
		PlotData[] array = new PlotData[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static Singles[] toSinglesArray(ArrayList<Singles> list) {
		Singles[] array = new Singles[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static Edge[] toEdgeArray(ArrayList<Edge> list) {
		Edge[] array = new Edge[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static NodeImpl[] toNodeImplArray(ArrayList<NodeImpl> list) {
		NodeImpl[] array = new NodeImpl[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static Value[] toValueArray(ArrayList<Value> list) {
		Value[] array = new Value[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	// ///////////////////////
	// Round
	// ///////////////////////

	public static double round(double value, int digits) {
		double temp = value * Math.pow(10, digits);
		temp = Math.round(temp);
		return (double) temp / (double) Math.pow(10, digits);
	}

	// ///////////////////////
	// Distribution
	// ///////////////////////

	public static int[] count(int[] values) {
		int[] counter = new int[max(values) + 1];
		for (int i = 0; i < values.length; i++) {
			counter[values[i]]++;
		}
		return counter;
	}

	public static double[] distribution(int[] values) {
		return distribution(values, sum(values));
	}

	public static double[] distribution(int[] values, int sum) {
		double[] distribution = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			distribution[i] = (double) values[i] / (double) sum;
		}
		return distribution;
	}

	public static double[] cumulative(double[] distribution) {
		double[] cumulative = new double[distribution.length];
		cumulative[0] = distribution[0];
		for (int i = 1; i < distribution.length; i++) {
			cumulative[i] = cumulative[i - 1] + distribution[i];
		}
		return cumulative;
	}

	// ///////////////////////
	// Sum
	// ///////////////////////

	public static int sum(int[] values) {
		int sum = 0;
		for (int i = 0; i < values.length; i++) {
			sum += values[i];
		}
		return sum;
	}

	public static double sum(double[] values) {
		double sum = 0;
		for (int i = 0; i < values.length; i++) {
			sum += values[i];
		}
		return sum;
	}

	// ///////////////////////
	// Avg
	// ///////////////////////

	public static double[] avg(int[][] values) {
		int max = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i].length > max) {
				max = values[i].length;
			}
		}
		double[] avg = new double[max];
		for (int i = 0; i < avg.length; i++) {
			for (int j = 0; j < values.length; j++) {
				if (values[j].length > i) {
					avg[i] += values[j][i];
				}
			}
			avg[i] /= (double) values.length;
		}
		return avg;
	}

	public static double[] avg(double[][] values) {
		int max = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i].length > max) {
				max = values[i].length;
			}
		}
		double[] avg = new double[max];
		for (int i = 0; i < avg.length; i++) {
			for (int j = 0; j < values.length; j++) {
				if (values[j].length > i) {
					avg[i] += values[j][i];
				}
			}
			avg[i] /= (double) values.length;
		}
		return avg;
	}

	public static double avg(int[] values) {
		int sum = 0;
		for (int i = 0; i < values.length; i++) {
			sum += values[i];
		}
		return (double) sum / (double) values.length;
	}

	public static double avg(double[] values) {
		double sum = 0;
		for (int i = 0; i < values.length; i++) {
			sum += values[i];
		}
		return sum / (double) values.length;
	}

	public static double[] avgArray(double[] values, int maxLength) {
		if (maxLength >= values.length) {
			return values;
		}
		double[] array = new double[maxLength];
		int index = 0;
		int counter = 0;
		for (int i = 0; i < values.length; i++) {
			int newIndex = (int) Math.floor((double) i * (double) array.length
					/ (double) values.length);
			if (newIndex != index) {
				array[index] /= counter;
				counter = 0;
				index = newIndex;
			}
			array[index] += values[i];
			counter++;
		}
		array[index] /= counter;
		return array;
	}

	public static double[] add(double[] v1, double[] v2) {
		if (v1.length != v2.length) {
			return null;
		}
		double[] v = new double[v1.length];
		for (int i = 0; i < v.length; i++) {
			v[i] = v1[i] + v2[i];
		}
		return v;
	}

	public static void divide(double[] v, double div) {
		for (int i = 0; i < v.length; i++) {
			v[i] /= div;
		}
	}

	// ///////////////////////
	// Min / Max DOUBLE
	// ///////////////////////

	public static int min(int[] values) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (min > values[i]) {
				min = values[i];
			}
		}
		return min;
	}

	public static double min(double[] values) {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (min > values[i]) {
				min = values[i];
			}
		}
		return min;
	}

	// ///////////////////////
	// Min / Max INT
	// ///////////////////////

	public static int max(int[] values) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (max < values[i]) {
				max = values[i];
			}
		}
		return max;
	}

	public static double max(double[] values) {
		double max = Double.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (max < values[i]) {
				max = values[i];
			}
		}
		return max;
	}

	public static double max(double[][] values, int index) {
		double max = Double.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (max < values[i][index]) {
				max = values[i][index];
			}
		}
		return max;
	}

	// ///////////////////////
	// FLIP
	// ///////////////////////

	public static String[][] flip(String[][] data) {
		String[][] flipped = new String[data[0].length][data.length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				flipped[j][i] = data[i][j];
			}
		}
		return flipped;
	}

	// ///////////////////////
	// TRANSFORM
	// ///////////////////////

	public static int toInt(byte[] b) {
		int i = 0;
		for (int j = 0; j < 4; j++) {
			i <<= 8;
			i ^= (int) b[j] & 0xFF;
		}
		return i;
	}

	// ///////////////////////
	// REMOVE
	// ///////////////////////

	public static NodeImpl[] removeHighestDegree(NodeImpl[] nodes) {
		int largestIndex = 0;
		int largestDegree = nodes[0].in().length + nodes[0].out().length;
		for (int i = 1; i < nodes.length; i++) {
			int degree = nodes[i].in().length + nodes[i].out().length;
			if (degree > largestDegree) {
				largestDegree = degree;
				largestIndex = i;
			}
		}
		NodeImpl[] removed = new NodeImpl[nodes.length - 1];
		for (int i = 0; i < nodes.length; i++) {
			if (i < largestIndex) {
				removed[i] = nodes[i];
			} else if (i > largestIndex) {
				removed[i - 1] = nodes[i];
			}
		}
		NodeImpl largest = nodes[largestIndex];
		for (int i = 0; i < removed.length; i++) {
			if (removed[i].hasIn(largest)) {
				removed[i].removeIn(largest);
			}
			if (removed[i].hasOut(largest)) {
				removed[i].removeOut(largest);
			}
		}
		for (int i = 0; i < removed.length; i++) {
			removed[i].setIndex(i);
		}
		return removed;
	}

	public static NodeImpl[] removeIsolatedClusters(NodeImpl[] users) {
		ArrayList<ArrayList<NodeImpl>> clusters = new ArrayList<ArrayList<NodeImpl>>();
		boolean[] visited = new boolean[users.length];
		for (int i = 0; i < users.length; i++) {
			if (visited[users[i].index()]) {
				continue;
			}
			clusters.add(new ArrayList<NodeImpl>());

			Queue<NodeImpl> queueFW = new LinkedList<NodeImpl>();
			boolean[] visitedFW = new boolean[users.length];
			visitedFW[users[i].index()] = true;
			queueFW.add(users[i]);
			while (!queueFW.isEmpty()) {
				NodeImpl current = queueFW.poll();
				Node[] out = current.out();
				for (int j = 0; j < out.length; j++) {
					if (!visitedFW[out[j].index()]) {
						queueFW.add((NodeImpl) out[j]);
						visitedFW[out[j].index()] = true;
					}
				}
			}

			Queue<NodeImpl> queueBW = new LinkedList<NodeImpl>();
			boolean[] visitedBW = new boolean[users.length];
			visitedBW[users[i].index()] = true;
			queueBW.add(users[i]);
			while (!queueBW.isEmpty()) {
				NodeImpl current = queueBW.poll();
				Node[] in = current.in();
				for (int j = 0; j < in.length; j++) {
					if (!visitedBW[in[j].index()]) {
						queueBW.add((NodeImpl) in[j]);
						visitedBW[in[j].index()] = true;
					}
				}
			}

			ArrayList<NodeImpl> currentCluster = clusters
					.get(clusters.size() - 1);
			for (int j = 0; j < users.length; j++) {
				if (visitedFW[users[j].index()] && visitedBW[users[j].index()]) {
					currentCluster.add(users[j]);
					visited[users[j].index()] = true;
				}
			}
		}
		int maxClusterIndex = 0;
		for (int i = 1; i < clusters.size(); i++) {
			if (clusters.get(i).size() > clusters.get(maxClusterIndex).size()) {
				maxClusterIndex = i;
			}
		}
		ArrayList<NodeImpl> remove = new ArrayList<NodeImpl>();
		for (int i = 0; i < clusters.size(); i++) {
			if (i != maxClusterIndex) {
				remove.addAll(clusters.get(i));
			}
		}
		return remove(users, remove);
	}

	private static NodeImpl[] remove(NodeImpl[] nodes,
			ArrayList<NodeImpl> remove) {
		ArrayList<NodeImpl> removed = new ArrayList<NodeImpl>(nodes.length
				- remove.size());
		for (int i = 0; i < nodes.length; i++) {
			if (!remove.contains(nodes[i])) {
				removed.add(nodes[i]);
			}
		}
		for (int i = 0; i < removed.size(); i++) {
			for (int j = 0; j < remove.size(); j++) {
				if (removed.get(i).hasIn(remove.get(j))) {
					removed.get(i).removeIn(remove.get(j));
				}
				if (removed.get(i).hasOut(remove.get(j))) {
					removed.get(i).removeOut(remove.get(j));
				}
			}
		}
		for (int i = 0; i < removed.size(); i++) {
			removed.get(i).setIndex(i);
		}
		return Util.toNodeImplArray(removed);
	}
}
