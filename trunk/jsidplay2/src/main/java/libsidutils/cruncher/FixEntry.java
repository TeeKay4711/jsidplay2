package libsidutils.cruncher;

public class FixEntry {
	public FixEntry(FixType t, int off) {
		type = t;
		offset = off;
	}

	FixType type;
	int offset;

}
