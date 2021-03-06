package gtna.io;

import gtna.util.Config;
import gtna.util.Timer;

import java.sql.Date;
import java.sql.Time;

public class Output {
	public static String DELIMITER;

	public static final int DELIMITER_LINE_LENGTH = 75;

	private static Filewriter fw;

	private static Timer timer;

	private static final boolean writeToConsole = true;

	private static final boolean writeToFile = true;

	public static void open(String filename) {
		DELIMITER = Config.get("OUTPUT_DELIMITER");
		if (writeToFile) {
			fw = new Filewriter(filename);
		}
		timer = new Timer();
		Time time = new Time(System.currentTimeMillis());
		Date date = new Date(System.currentTimeMillis());
		writelnDelimiter();
		writeIndent();
		String start = Config.get("OUTPUT_START");
		start = start.replace("%DATE", date.toString());
		start = start.replace("%TIME", time.toString());
		writeln(start);
		writelnDelimiter();
	}

	public static void close() {
		Time time = new Time(System.currentTimeMillis());
		Date date = new Date(System.currentTimeMillis());
		writelnDelimiter();
		writeIndent();
		String end = Config.get("OUTPUT_END");
		end = end.replace("%DATE", date.toString());
		end = end.replace("%TIME", time.toString());
		writeln(end);
		writeIndent();
		String mb = "" + Runtime.getRuntime().totalMemory() / (1024 * 1024);
		if (timer.sec() == 1) {
			writeln(Config.get("OUTPUT_SECOND").replace("%MEM", mb));
		} else {
			writeln(Config.get("OUTPUT_SECONDS").replace("%SEC",
					timer.sec() + "").replace("%MEM", mb));
		}
		writelnDelimiter();
		if (writeToFile && fw != null) {
			fw.close();
			fw = null;
		}
	}

	public static void write(String data) {
		if (writeToFile && fw != null) {
			fw.write(data);
		}
		if (writeToConsole) {
			System.out.print(data);
		}
	}

	public static void writeln(String line) {
		if (writeToFile && fw != null) {
			fw.writeln(line);
		}
		if (writeToConsole) {
			System.out.println(line);
		}
	}

	public static void writeDelimiter() {
		write(delimiter(DELIMITER_LINE_LENGTH));
	}

	public static void writelnDelimiter() {
		writeln(delimiter(DELIMITER_LINE_LENGTH));
	}

	public static void writeIndent() {
		write(delimiter(3) + " ");
	}

	public static String delimiter(int times) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < times; i++) {
			buff.append(DELIMITER);
		}
		return buff.toString();
	}
}
