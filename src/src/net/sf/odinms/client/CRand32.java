package net.sf.odinms.client;

import java.util.Random;

import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
* @ Author - Novak
*
* Special credits to:
* BMS/ Eric/ Tyler(xepheus)
*/

public class CRand32 {

	private transient long seed1, seed2, seed3;

	public CRand32() {
		final int mod = 5;
		int toSeed = 1170746341 * mod - 755606699;
		Random rng = new Random();
		this.generateSeed(rng.nextLong(), toSeed, toSeed);
	}

	public final void generateSeed(final long s1, final long s2, final long s3) {
		seed1 = s1 | 0x100000;
		seed2 = s2 | 0x1000;
		seed3 = s3 | 0x10;
	}

	public final long Randomize() {
		// IDA copy-paste from BMS basically. IDK either.
		long firstShuffle = ((this.seed1 & 0xFFFFFFFE) << 12) ^ ((this.seed1 & 0x7FFC0 ^ (this.seed1 >> 13)) >> 6);
		long secondShuffle = 16 * (this.seed2 & 0xFFFFFFF8) ^ (((this.seed2 >> 2) ^ this.seed2 & 0x3F800000) >> 23);
		long thirdShuffle = ((this.seed3 & 0xFFFFFFF0) << 17) ^ (((this.seed3 >> 3) ^ this.seed3 & 0x1FFFFF00) >> 8);
		return (firstShuffle ^ secondShuffle ^ thirdShuffle) & 0xffffffffL;
	}

	public final void connectData(MaplePacketLittleEndianWriter mplew) {
		long rand1 = Randomize();
		long rand2 = Randomize();
		long rand3 = Randomize();

		generateSeed(rand1, rand2, rand3);

		mplew.writeInt((int) rand1);
		mplew.writeInt((int) rand2);
		mplew.writeInt((int) rand3);
	}
}
