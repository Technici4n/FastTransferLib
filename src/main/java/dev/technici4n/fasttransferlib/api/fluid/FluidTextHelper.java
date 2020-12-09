package dev.technici4n.fasttransferlib.api.fluid;

/**
 * A few helpers to display fluids in unicode.
 */
public class FluidTextHelper {
	/**
	 * Return a unicode string representing a fraction, like ¹⁄₈₁.
	 */
	public static String getUnicodeFraction(long numerator, long denominator, boolean simplify) {
		if (numerator < 0 || denominator < 0) throw new IllegalArgumentException("Numerator and denominator must be non negative.");

		if (simplify && denominator != 0) {
			long g = gcd(numerator, denominator);
			numerator /= g;
			denominator /= g;
		}

		StringBuilder numString = new StringBuilder();

		while (numerator > 0) {
			numString.append(SUPERSCRIPT[(int) (numerator % 10)]);
			numerator /= 10;
		}

		StringBuilder denomString = new StringBuilder();

		while (denominator > 0) {
			denomString.append(SUBSCRIPT[(int) (denominator % 10)]);
			denominator /= 10;
		}

		return numString.reverse().toString() + FRACTION_BAR + denomString.reverse().toString();
	}

	/**
	 * Convert a non negative fluid amount in droplets to a unicode string representing the amount in millibuckets.
	 * For example, passing 163 will result in <pre>2 ¹⁄₈₁</pre>.
	 */
	public static String getUnicodeMillibuckets(long droplets, boolean simplify) {
		String result = "" + droplets / 81;

		if (droplets % 81 != 0) {
			result += " " + getUnicodeFraction(droplets % 81, 81, simplify);
		}

		return result;
	}

	private static final char[] SUPERSCRIPT = new char[] { '\u2070', '\u00b9', '\u00b2', '\u00b3', '\u2074', '\u2075', '\u2076', '\u2077', '\u2078', '\u2079' };
	private static final char FRACTION_BAR = '\u2044';
	private static final char[] SUBSCRIPT = new char[] { '\u2080', '\u2081', '\u2082', '\u2083', '\u2084', '\u2085', '\u2086', '\u2087', '\u2088', '\u2089' };

	private static long gcd(long a, long b) {
		if (a > b) {
			long t = b;
			b = a;
			a = t;
		}

		while (a != 0) {
			long t = a;
			a = b % a;
			b = t;
		}

		return b;
	}

	private FluidTextHelper() {
	}
}
