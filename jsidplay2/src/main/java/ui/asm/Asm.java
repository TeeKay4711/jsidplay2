package ui.asm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import libsidplay.Player;
import libsidutils.assembler.KickAssembler;
import ui.common.C64Window;
import ui.common.UIPart;
import ui.common.UIUtil;

public class Asm extends Tab implements UIPart {

	private static final String ASM_RESOURCE = "jsidplay2";
	private static final String ASM_EXAMPLE = "/ui/asm/Asm.asm";

	@FXML
	private TextArea contents;

	private UIUtil util;

	private KickAssembler assembler;
	private Map<String, Integer> globals;

	public Asm(C64Window window, Player player) {
		util = new UIUtil(window, player, this);
		setContent((Node) util.parse());
	}

	@FXML
	private void initialize() {
		globals = new HashMap<String, Integer>();
		assembler = new KickAssembler();
		try {
			java.net.URL url = Asm.class.getResource(ASM_EXAMPLE);
			java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
			String asm = new String(java.nio.file.Files.readAllBytes(resPath),
					"UTF-8");
			contents.setText(asm);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void compile() {
		try {
			InputStream asm = new ByteArrayInputStream(contents.getText()
					.getBytes("UTF-8"));
			byte[] assembly = assembler.assemble(ASM_RESOURCE, asm, globals);
			int startAddress = (assembly[0] & 0xff)
					+ ((assembly[1] & 0xff) << 8);
			byte[] ram = util.getPlayer().getC64().getRAM();
			System.arraycopy(assembly, 2, ram, startAddress,
					assembly.length - 2);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
