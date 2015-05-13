package libsidplay.components.pla;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import libsidplay.common.Event;
import libsidplay.common.EventScheduler;
import libsidplay.common.SIDListener;
import libsidplay.common.SIDEmu;
import libsidplay.components.cart.Cartridge;
import libsidplay.components.mos6510.MOS6510;
import libsidplay.components.mos656x.VIC;
import libsidplay.components.ram.ColorRAMBank;

/**
 * The C64 MMU chip. This handles the coordination between the various chips in
 * the system.
 * 
 * The chip is thought to originate the AEC (cpu/vic select) signal, and though
 * electrically incorrect, it is given the role of routing IRQ and NMI signals
 * as well. In truth the IRQ and NMI signals are generated by a number of chips
 * on the system.
 * 
 * @author Antti Lankila
 */
public final class PLA {
	/** Maximum number of supported SIDs (mono and stereo) */
	public final static int MAX_SIDS = 3;

	private static final String CHAR_ROM = "/libsidplay/roms/char.bin";
	private static final String BASIC_ROM = "/libsidplay/roms/basic.bin";
	private static final String KERNAL_ROM = "/libsidplay/roms/kernal.bin";

	private static final byte[] CHAR = new byte[0x1000];
	private static final byte[] BASIC = new byte[0x2000];
	private static final byte[] KERNAL = new byte[0x2000];
	static {
		try (DataInputStream isChar = new DataInputStream(
				PLA.class.getResourceAsStream(CHAR_ROM));
				DataInputStream isBasic = new DataInputStream(
						PLA.class.getResourceAsStream(BASIC_ROM));
				DataInputStream isKernal = new DataInputStream(
						PLA.class.getResourceAsStream(KERNAL_ROM))) {
			isChar.readFully(CHAR);
			isBasic.readFully(BASIC);
			isKernal.readFully(KERNAL);
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	private static final Bank characterRomBank = new Bank() {
		@Override
		public byte read(final int address) {
			return CHAR[address & 0xfff];
		}

		@Override
		public void write(final int address, final byte value) {
			throw new RuntimeException(
					"This bank should never be mapped to W mode");
		}
	};

	private static final Bank basicRomBank = new Bank() {
		@Override
		public byte read(final int address) {
			return BASIC[address & 0x1fff];
		}

		@Override
		public void write(final int address, final byte value) {
			throw new RuntimeException(
					"This bank should never be mapped to W mode");
		}
	};

	private static final Bank kernalRomBank = new Bank() {
		@Override
		public byte read(final int address) {
			return KERNAL[address & 0x1fff];
		}

		@Override
		public void write(final int address, final byte value) {
			throw new RuntimeException(
					"This bank should never be mapped to W mode");
		}
	};

	/** Replacement of the Kernal ROM */
	private Bank customKernalRomBank;

	/**
	 * Set custom Kernal ROM.
	 * 
	 * @param kernalRom
	 *            custom Kernal ROM
	 */
	public void setCustomKernalRomBank(final Bank kernalRom) {
		this.customKernalRomBank = kernalRom;
	}

	/** SID chip memory bank maps reads and writes to the assigned SID chip */
	private class SIDBank extends Bank {
		/**
		 * Size of mapping table. Each 32 bytes another SID chip base address
		 * can be assigned to.
		 */
		private final static int MAPPER_SIZE = 32;
		/** Number of SID chip registers */
		private final static int REG_COUNT = 32;

		/**
		 * Mapping table in d400-d7ff. Maps a SID chip base address to a SID
		 * chip number.
		 */
		private final int sidmapper[] = new int[MAPPER_SIZE];

		/** Contains a SID chip implementation for each SID chip number. */
		private final SIDEmu[] sidemu = new SIDEmu[MAX_SIDS];

		/** SIDs assigned to bank numbers */
		private boolean[] sidBankUsed = new boolean[16];

		/** SID listener for detecting SID writes */
		private final SIDListener[] sidWriteListener = new SIDListener[MAX_SIDS];

		/** Reset SID chips using highest volume setting. */
		private void reset() {
			Arrays.fill(sidWriteListener, null);
			clearSIDAddresses();
		}

		/** Clear SID address mapping */
		private void clearSIDAddresses() {
			Arrays.fill(sidmapper, 0);
			Arrays.fill(sidBankUsed, false);
		}

		/** Assign SID chip base address to a SID chip number */
		private void setSIDAddress(final int chipNum, final int address) {
			sidmapper[address >> 5 & MAPPER_SIZE - 1] = chipNum;
			sidBankUsed[address >> 8 & 0xf] = true;
		}

		/** Is a specific memory bank in use by SID? */
		private boolean isUsed(int bankNum) {
			return sidBankUsed[bankNum];
		}

		/**
		 * Install a SID register write listener for a specific SID chip number.
		 */
		public void setSidWriteListener(final int chipNum,
				final SIDListener listener) {
			sidWriteListener[chipNum] = listener;
		}

		/**
		 * SID register read access redirected to a specific SID chip number
		 * configured earlier.
		 */
		@Override
		public byte read(final int address) {
			final int chipNum = sidmapper[address >> 5 & MAPPER_SIZE - 1];
			if (sidemu[chipNum] != null) {
				return sidemu[chipNum].read(address & REG_COUNT - 1);
			} else {
				return (byte) 0xff;
			}
		}

		/**
		 * SID register write access redirected to a specific SID chip number
		 * configured earlier.
		 */
		@Override
		public void write(final int address, final byte value) {
			final int chipNum = sidmapper[address >> 5 & MAPPER_SIZE - 1];
			if (sidemu[chipNum] != null) {
				sidemu[chipNum].write(address & REG_COUNT - 1, value);
			}
			if (sidWriteListener[chipNum] != null) {
				final long time = context.getTime(Event.Phase.PHI2);
				sidWriteListener[chipNum].write(time, chipNum, address
						& REG_COUNT - 1, value);
			}
		}

		/** Get SID chip implementation of a specific SID chip number. */
		private SIDEmu getSID(final int chipNum) {
			return sidemu[chipNum];
		}

		/** Set SID chip implementation of a specific SID chip number. */
		private void setSID(final int chipNum, final SIDEmu s) {
			sidemu[chipNum] = s;
		}
	}

	/** SID chip memory bank */
	private final SIDBank sidBank = new SIDBank();

	protected final ColorRAMBank colorRamBank = new ColorRAMBank();

	private final Bank colorRamDisconnectedBusBank = new Bank() {
		@Override
		public byte read(final int address) {
			return (byte) (colorRamBank.read(address) | disconnectedBusBank
					.read(address) & 0xf0);
		}

		@Override
		public void write(final int address, final byte value) {
			colorRamBank.write(address, value);
		}
	};

	/**
	 * IO region handler. 4k region, 16 chips, 256b banks.
	 * 
	 * @author Antti Lankila
	 */
	public static class IOBank extends Bank {
		private final Bank[] map = new Bank[16];

		public void setBank(final int num, final Bank b) {
			map[num] = b;
		}

		@Override
		public byte read(final int address) {
			return map[address >> 8 & 0xf].read(address);
		}

		@Override
		public void write(final int address, final byte value) {
			map[address >> 8 & 0xf].write(address, value);
		}
	}

	private final IOBank ioBank = new IOBank();

	/** CPU port signals */
	private boolean basic, kernal, io;

	/** CPU read memory mapping in 4k chunks */
	private final Bank[] cpuReadMap = new Bank[16];

	/** CPU write memory mapping in 4k chunks */
	private final Bank[] cpuWriteMap = new Bank[16];

	/** VIC memory bank mapping in 4k chunks */
	private final Bank[] vicMapPHI1 = new Bank[16];

	/** VIC memory top bits from CIA 2 */
	private int vicMemBase;

	/** BA state */
	private boolean oldBAState;

	/** AEC state @ PHI2 */
	protected boolean aecDuringPhi2;

	/** Event to change the BA state */
	private final Event aecDisableEvent = new Event("BA transitions") {
		@Override
		public void event() {
			aecDuringPhi2 = false;
		}
	};

	/** Event Scheduler for delayed actions */
	private final EventScheduler context;

	/** Main CPU instance */
	private MOS6510 cpu;

	/** RAM */
	private final Bank ramBank;

	/** Connected cartridge */
	private Cartridge cartridge;

	private final Cartridge nullCartridge;

	/** Disconnected data bus support */
	protected DisconnectedBusBank disconnectedBusBank;

	/** Cartridge GAME and EXROM signal state (active low) at PHI1 and PHI2 */
	private boolean gamePHI1, exromPHI1, gamePHI2, exromPHI2;

	/** Number of sources asserting NMI */
	private int nmiCount;

	/** Number of sources asserting IRQ */
	private int irqCount;

	/** Cartridge DMA */
	private boolean cartridgeDma;

	public PLA(final EventScheduler context, final Bank zeroRAMBank,
			final Bank ramBank) {
		this.context = context;
		this.ramBank = ramBank;
		nullCartridge = Cartridge.nullCartridge(this);

		ioBank.setBank(4, sidBank);
		ioBank.setBank(5, sidBank);
		ioBank.setBank(6, sidBank);
		ioBank.setBank(7, sidBank);
		ioBank.setBank(8, colorRamDisconnectedBusBank);
		ioBank.setBank(9, colorRamDisconnectedBusBank);
		ioBank.setBank(10, colorRamDisconnectedBusBank);
		ioBank.setBank(11, colorRamDisconnectedBusBank);

		Arrays.fill(cpuReadMap, ramBank);
		Arrays.fill(cpuWriteMap, ramBank);
		cpuReadMap[0] = cpuWriteMap[0] = zeroRAMBank;

		Arrays.fill(vicMapPHI1, ramBank);

		setCartridge(null);
	}

	public void reset() {
		gamePHI1 = exromPHI1 = gamePHI2 = exromPHI2 = true;
		vicMemBase = 0;
		aecDuringPhi2 = false;
		oldBAState = true;
		nmiCount = 0;
		irqCount = 0;

		sidBank.reset();
		colorRamBank.reset();
		/* Cartridge-related banks are not reset() */
		cartridge.reset();

		updateMappingPHI1();
		updateMappingPHI2();
	}

	public void setCpuPort(final int state) {
		basic = (state & 1) != 0;
		kernal = (state & 2) != 0;
		io = (state & 4) != 0;
		updateMappingPHI2();
	}

	public void setGameExrom(final boolean game, final boolean exrom) {
		setGameExrom(game, exrom, game, exrom);
	}

	public void setGameExrom(final boolean gamephi1, final boolean exromphi1,
			final boolean gamephi2, final boolean exromphi2) {
		gamePHI1 = gamephi1;
		exromPHI1 = exromphi1;
		updateMappingPHI1();

		exromPHI2 = exromphi2;
		gamePHI2 = gamephi2;
		updateMappingPHI2();
	}

	/**
	 * BA signal.
	 * 
	 * Calls permitted during PHI1.
	 * 
	 * @param state
	 *            BA state.
	 */
	public void setBA(final boolean state) {
		/* only react to changes in state */
		if (state ^ oldBAState == false) {
			return;
		}
		oldBAState = state;

		/* Signal changes in BA to interested parties */
		if (!cartridgeDma) {
			cpu.setRDY(state);
		}
		cartridge.changedBA(state);

		if (state) {
			aecDuringPhi2 = true;
			context.cancel(aecDisableEvent);
		} else {
			context.schedule(aecDisableEvent, 3, Event.Phase.PHI1);
		}
	}

	/**
	 * Expansion port DMA signal.
	 * 
	 * Calls permitted during PHI1.
	 * 
	 * @param state
	 *            DMA state.
	 */
	public void setDMA(final boolean state) {
		cartridgeDma = state;
		if (cartridgeDma) {
			/* Was bus available? Stall CPU. */
			if (oldBAState) {
				cpu.setRDY(false);
			}
		} else {
			/* is bus available? Resume CPU. */
			if (oldBAState) {
				cpu.setRDY(true);
			}
		}
	}

	/**
	 * NMI trigger signal.
	 * 
	 * Calls permitted any time, but normally originated by chips at PHI1.
	 * 
	 * @param state
	 *            NMI state.
	 */
	public void setNMI(final boolean state) {
		if (state) {
			if (nmiCount == 0) {
				cpu.triggerNMI();
				cartridge.changedNMI(true);
			}
			nmiCount++;
		} else {
			nmiCount--;
			if (nmiCount == 0) {
				cartridge.changedNMI(false);
			}
		}
	}

	/**
	 * IRQ trigger signal.
	 * 
	 * Calls permitted any time, but normally originated by chips at PHI1.
	 * 
	 * @param state
	 *            IRQ state.
	 */
	public void setIRQ(final boolean state) {
		if (state) {
			if (irqCount == 0) {
				cpu.triggerIRQ();
				cartridge.changedIRQ(true);
			}
			irqCount++;
		} else {
			irqCount--;
			if (irqCount == 0) {
				cpu.clearIRQ();
				cartridge.changedIRQ(false);
			}
		}
	}

	private void updateMappingPHI2() {
		/* Ultimax mode ignores the CPU port, so it's best treated separately. */
		if (exromPHI2 && !gamePHI2) {
			for (int i : new int[] { 1, 2, 3, 4, 5, 6, 7, 0xa, 0xb, 0xc }) {
				cpuReadMap[i] = cpuWriteMap[i] = cartridge.getUltimaxMemory();
			}
			cpuReadMap[0x8] = cpuReadMap[0x9] = cpuWriteMap[0x8] = cpuWriteMap[0x9] = cartridge
					.getRoml();
			cpuReadMap[0xd] = ioBank;
			cpuWriteMap[0xd] = ioBank;
			cpuReadMap[0xe] = cpuReadMap[0xf] = cpuWriteMap[0xe] = cpuWriteMap[0xf] = cartridge
					.getRomh();
		} else {
			for (int i : new int[] { 1, 2, 3, 4, 5, 6, 7, 0xc }) {
				cpuReadMap[i] = cpuWriteMap[i] = ramBank;
			}
			cpuWriteMap[0x8] = cpuWriteMap[0x9] = ramBank;
			cpuWriteMap[0xa] = cpuWriteMap[0xb] = ramBank;
			cpuWriteMap[0xe] = cpuWriteMap[0xf] = ramBank;

			// !ROML = (_LORAM & _HIRAM & A15 & !A14 & !A13 & !_AEC & R__W &
			// !_EXROM
			// # A15 & !A14 & !A13 & !_AEC & _EXROM & !_GAME );
			if (basic && kernal && !exromPHI2) {
				cpuReadMap[0x8] = cpuReadMap[0x9] = cartridge.getRoml();
			} else {
				cpuReadMap[0x8] = cpuReadMap[0x9] = ramBank;
			}

			// !ROMH = (_HIRAM & A15 & !A14 & A13 & !_AEC & R__W & !_EXROM &
			// !_GAME // $a000-$bfff
			// # A15 & A14 & A13 & !_AEC & _EXROM & !_GAME // $e000-$ffff
			// # _AEC & _EXROM & !_GAME & VA13 & VA12);
			if (kernal && !exromPHI2 && !gamePHI2) {
				cpuReadMap[0xa] = cpuReadMap[0xb] = cartridge.getRomh();
				// !BASIC = (_LORAM & _HIRAM & A15 & !A14 & A13 & !_AEC & R__W &
				// _GAME );
			} else if (basic && kernal & gamePHI2) {
				cpuReadMap[0xa] = cpuReadMap[0xb] = basicRomBank;
			} else {
				cpuReadMap[0xa] = cpuReadMap[0xb] = ramBank;
			}

			// !I_O = (_HIRAM & _CHAREN & A15 & A14 & !A13 & A12 & BA & !_AEC &
			// R__W & _GAME
			// # _HIRAM & _CHAREN & A15 & A14 & !A13 & A12 & !_AEC & !R__W &
			// _GAME
			// # _LORAM & _CHAREN & A15 & A14 & !A13 & A12 & BA & !_AEC & R__W &
			// _GAME
			// # _LORAM & _CHAREN & A15 & A14 & !A13 & A12 & !_AEC & !R__W &
			// _GAME
			// # _HIRAM & _CHAREN & A15 & A14 & !A13 & A12 & BA & !_AEC & R__W &
			// !_EXROM & !_GAME
			// # _HIRAM & _CHAREN & A15 & A14 & !A13 & A12 & !_AEC & !R__W &
			// !_EXROM & !_GAME
			// # _LORAM & _CHAREN & A15 & A14 & !A13 & A12 & BA & !_AEC & R__W &
			// !_EXROM & !_GAME
			// # _LORAM & _CHAREN & A15 & A14 & !A13 & A12 & !_AEC & !R__W &
			// !_EXROM & !_GAME
			// # A15 & A14 & !A13 & A12 & BA & !_AEC & R__W & _EXROM & !_GAME
			// # A15 & A14 & !A13 & A12 & !_AEC & !R__W & _EXROM & !_GAME);

			/* i/o or character */
			if (io && (basic || kernal)
					&& (gamePHI2 || !gamePHI2 && !exromPHI2)) {
				cpuReadMap[0xd] = cpuWriteMap[0xd] = ioBank;
				// !CHAROM = (_HIRAM & !_CHAREN & A15 & A14 & !A13 & A12 & !_AEC
				// & R__W & _GAME
				// # _LORAM & !_CHAREN & A15 & A14 & !A13 & A12 & !_AEC & R__W &
				// _GAME
				// # _HIRAM & !_CHAREN & A15 & A14 & !A13 & A12 & !_AEC & R__W &
				// !_EXROM & !_GAME
				// # _VA14 & _AEC & _GAME & !VA13 & VA12
				// # _VA14 & _AEC & !_EXROM & !_GAME & !VA13 & VA12 );
			} else if (!io
					&& ((basic || kernal) && gamePHI2 || kernal && !gamePHI2
							&& !exromPHI2)) {
				cpuReadMap[0xd] = characterRomBank;
				cpuWriteMap[0xd] = ramBank;
			} else {
				cpuReadMap[0xd] = ramBank;
				cpuWriteMap[0xd] = ramBank;
			}

			// !KERNAL = (_HIRAM & A15 & A14 & A13 & !_AEC & R__W & _GAME
			// #_HIRAM & A15 & A14 & A13 & !_AEC & R__W & !_EXROM & !_GAME );
			if (kernal & (gamePHI2 || !gamePHI2 && !exromPHI2)) {
				cpuReadMap[0xe] = cpuReadMap[0xf] = customKernalRomBank != null ? customKernalRomBank
						: kernalRomBank;
			} else {
				cpuReadMap[0xe] = cpuReadMap[0xf] = ramBank;
			}
		}

		Bank io1 = cartridge.getIO1();
		ioBank.setBank(14, sidBank.isUsed(14) ? sidBank : io1);

		Bank io2 = cartridge.getIO2();
		ioBank.setBank(15, sidBank.isUsed(15) ? sidBank : io2);

		cartridge.installBankHooks(cpuReadMap, cpuWriteMap);
	}

	private void updateMappingPHI1() {
		/* VIC bank mapping Character ROM */
		if (gamePHI1 || !gamePHI1 && !exromPHI1) {
			vicMapPHI1[0x1] = vicMapPHI1[0x9] = characterRomBank;
		} else {
			vicMapPHI1[0x1] = vicMapPHI1[0x9] = ramBank;
		}

		/* vic bank mapping ROMH */
		if (exromPHI1 && !gamePHI1) {
			for (int i = 3; i < 16; i += 4) {
				vicMapPHI1[i] = cartridge.getRomh();
			}
		} else {
			for (int i = 3; i < 16; i += 4) {
				vicMapPHI1[i] = ramBank;
			}
		}
	}

	/**
	 * Access memory as seen by CPU
	 * 
	 * @param address
	 * @return value at address
	 */
	public byte cpuRead(final int address) {
		return cpuReadMap[address >> 12].read(address);
	}

	/**
	 * Access memory as seen by CPU.
	 * 
	 * @param address
	 * @param value
	 */
	public void cpuWrite(final int address, final byte value) {
		cpuWriteMap[address >> 12].write(address, value);
	}

	/**
	 * Set VIC address lines VA14 and VA15. Value for base should be one of
	 * $0000, $4000, $8000, $c000.
	 * 
	 * @param base
	 */
	public void setVicMemBase(final int base) {
		vicMemBase = base;
	}

	/**
	 * Access memory as seen by VIC. The address should only contain the bottom
	 * 14 bits.
	 */
	public byte vicReadMemoryPHI1(int addr) {
		addr |= vicMemBase;
		return vicMapPHI1[addr >> 12].read(addr);
	}

	/**
	 * Access memory as seen by VIC. The address should only contain the bottom
	 * 14 bits.
	 * 
	 * If AEC is still high (CPU is connected to the bus), the 0xff read is
	 * emulated, as the VIC has tristated itself from the bus. Otherwise, the
	 * access goes like in PHI1.
	 */
	public byte vicReadMemoryPHI2(final int addr) {
		if (aecDuringPhi2) {
			return (byte) 0xff;
		}

		return vicReadMemoryPHI1(addr);
	}

	/**
	 * Access color RAM from VIC. The address should be between 0 - 0x3ff.
	 * 
	 * If AEC is still high, the bottom 4 bits of the value CPU is stalled on
	 * reading will be acquired instead. These data lines are not tristated.
	 */
	public byte vicReadColorMemoryPHI2(final int addr) {
		if (aecDuringPhi2) {
			return (byte) (cpu.getStalledOnByte() & 0x0f);
		} else {
			return colorRamBank.read(addr);
		}
	}

	/**
	 * Set currently connected cartridge.
	 * 
	 * @param cartridge
	 */
	public void setCartridge(Cartridge cartridge) {
		if (cartridge == null) {
			cartridge = nullCartridge;
		}
		this.cartridge = cartridge;
	}

	public void setCpu(final MOS6510 cpu) {
		this.cpu = cpu;
	}

	public void setCia1(final Bank cia1) {
		ioBank.setBank(0xc, cia1);
	}

	public void setCia2(final Bank cia2) {
		ioBank.setBank(0xd, cia2);
	}

	public void setVic(final VIC vic) {
		ioBank.setBank(0, vic);
		ioBank.setBank(1, vic);
		ioBank.setBank(2, vic);
		ioBank.setBank(3, vic);

		disconnectedBusBank = new DisconnectedBusBank(vic);
	}

	/**
	 * Return the requested SID
	 * 
	 * @param chipNo
	 *            (0..MAX_SIDS-1)
	 * @return the SID
	 */
	public SIDEmu getSID(final int chipNo) {
		return sidBank.getSID(chipNo);
	}

	/**
	 * Set the requested SID
	 * 
	 * @param chipNo
	 *            (0..MAX_SIDS-1)
	 * @param sidemu
	 *            the sid to set
	 */
	public void setSID(final int chipNo, final SIDEmu sidemu) {
		sidBank.setSID(chipNo, sidemu);
	}

	/**
	 * Clear SID address mapping.
	 */
	public void clearSIDAddresses() {
		sidBank.clearSIDAddresses();
	}

	/**
	 * Set the base address of a stereo SID chip
	 * 
	 * @param secondSidChipBase
	 *            base address (e.g. 0xd420)
	 */
	public void setSIDAddress(int sidNum, final int base) {
		sidBank.setSIDAddress(sidNum, base);
	}

	/**
	 * Set a SID write listener to debug SID register writes.
	 * 
	 * @param chipNo
	 *            (0..MAX_SIDS-1)
	 * @param listener
	 *            listener to debug SID register writes
	 */
	public void setSidWriteListener(final int chipNo,
			final SIDListener listener) {
		sidBank.setSidWriteListener(chipNo, listener);
	}

	public Bank getDisconnectedBusBank() {
		return disconnectedBusBank;
	}

	public MOS6510 getCPU() {
		return cpu;
	}

	public Cartridge getCartridge() {
		return cartridge;
	}

}