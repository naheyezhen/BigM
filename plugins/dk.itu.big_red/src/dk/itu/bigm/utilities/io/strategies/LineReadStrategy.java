package dk.itu.bigm.utilities.io.strategies;

public class LineReadStrategy extends DelimitedReadStrategy {
	@Override
	protected boolean shouldBreak(byte b) {
		return (b == '\n');
	}
}
