package net.sf.drftpd;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * @author mog
 * @version $Id: Bytes.java,v 1.11 2004/01/13 20:30:53 mog Exp $
 */
public class Bytes {
	//yotta
	//zetta
	/**
	 * 1,000,000,000,000,000,000 = exa
	 */
	public static final long EXA = 1000000000000000000L;
	/**
	 * 1,000,000,000,000,000 = peta
	 */
	public static final long PETA = 1000000000000000L;
	/**
	 * 1,000,000,000,000 = terra
	 */
	public static final long TERRA = 1000000000000L;
	/**
	 * 1,000,000,000 GB
	 */
	public static final long GIGA = 1000000000L;
	/**
	 * 1,000,000 MB
	 */
	public static final long MEGA = 1000000L;
	/**
	 * 1,000 KB
	 */
	public static final long KILO = 1000L;

	/**
	 * @return human readable string representation of a number of bytes.
	 */
	public static String formatBytes(long bytes) {
		long absbytes = Math.abs(bytes);
		DecimalFormatSymbols formatsymbols = new DecimalFormatSymbols();
		formatsymbols.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("0.0", formatsymbols);
		format.setDecimalSeparatorAlwaysShown(true);
		if (absbytes >= TERRA) {
			return format.format((float) bytes / TERRA) + "TB";

		} else if (absbytes >= GIGA) {
			return format.format((float) bytes / GIGA) + "GB";

		} else if (absbytes >= MEGA) {
			return format.format((float) bytes / MEGA) + "MB";

		} else if (absbytes >= KILO) {
			return format.format((float) bytes / KILO) + "KB";
		}
		return Long.toString(bytes) + "B";
	}


	/**
	 * Parse a string representation of an amount of bytes. The suffix b is optional and makes no different, this method is case insensitive.
	 * <p>
	 * For example:
	 * 1000 = 1000 bytes
	 * 1000b = 1000 bytes
	 * 1000B = 1000 bytes
	 * 1k = 1000 bytes
	 * 1kb = 1000 bytes
	 * 1t = 1 terrabyte
	 */
	public static long parseBytes(String str) throws NumberFormatException {
		str = str.toUpperCase();
		if (str.endsWith("B"))
			str = str.substring(0, str.length() - 1);
		if (str.endsWith("K"))
			return (long)(Double.parseDouble(str.substring(0, str.length() - 1)) * KILO);

		if (str.endsWith("M"))
			return (long)(Double.parseDouble(str.substring(0, str.length() - 1)) * MEGA);

		if (str.endsWith("G"))
			return (long)(Double.parseDouble(str.substring(0, str.length() - 1)) * GIGA);

		if (str.endsWith("T"))
			return (long)(Double.parseDouble(str.substring(0, str.length() - 1)) * TERRA);

		return Long.parseLong(str);
	}
}
