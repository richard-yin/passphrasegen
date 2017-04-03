import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

@SuppressWarnings("serial")
public class PassPhraseUI extends JFrame {
	private static final String[] PASS_TYPE_STRINGS = {
			"<font color=#ff0000>desktop</font>",
			"<font color=#0000ff>email</font>",
			"<font color=#007f00>bank</font>" };

	private List<String> words;
	private String[] passwords = new String[3];

	private JButton genPassButton = new JButton("Create Password");
	private JButton enterPassButton = new JButton("Enter password");

	private Random randGen = new Random();
	private JPasswordField pwdField = new JPasswordField();
	private AncestorListener requestFocusListener = new AncestorListener() {
		@Override
		public void ancestorAdded(AncestorEvent event) {
			event.getComponent().requestFocusInWindow();
		}
		
		@Override
		public void ancestorMoved(AncestorEvent event) {}
		@Override
		public void ancestorRemoved(AncestorEvent event) {}
	};

	private ActionListener genPassAction = event -> {
		passwords = new String[3];
		for (int passType = 0; passType < 3; passType++) {
			printCSVLog("create, start", passType);
			String[] choices = new String[4];
			for (int i = 0; i < 4; i++) {
				choices[i] = generatePassword();
			}
			String password = (String) JOptionPane.showInputDialog(this,
					"<html>Please select a " + PASS_TYPE_STRINGS[passType] + " password.",
					"Create Password", JOptionPane.QUESTION_MESSAGE,
					null, choices, choices[0]);
			if (password == null) {
				printCSVLog("enter, cancel", passType);
				return;
			} else {
				passwords[passType] = password;
			}

			if (!verifyPassword(passType)) {
				return;
			}
		}
	};

	private ActionListener enterPassAction = event -> {
		List<Integer> passTypes = new ArrayList<>(Arrays.asList(0, 1, 2));
		Collections.shuffle(passTypes);
		int successes = 0;
		for (Integer passType : passTypes) {
			for (int tries = 0; tries < 3; tries++) {
				printCSVLog("enter, start", passType);
				pwdField.setText("");
				int action = JOptionPane.showConfirmDialog(this, new Object[] {
						"<html>Please enter your "
								+ PASS_TYPE_STRINGS[passType] + " password:",
						pwdField }, "Enter Password",
						JOptionPane.OK_CANCEL_OPTION);
				if (action != 0) {
					printCSVLog("enter, cancel", passType);
					break;
				} else if (passwords[passType].equals(String.valueOf(pwdField
						.getPassword()))) {
					printCSVLog("enter, success", passType);
					successes++;
					break;
				} else if (tries == 2) {
					printCSVLog("enter, fail", passType);
				} else {
					printCSVLog("enter, retry", passType);
				}
			}
		}
		JOptionPane.showMessageDialog(this, successes
				+ " of 3 passwords successfully entered.", "Result",
				JOptionPane.INFORMATION_MESSAGE);
	};

	public PassPhraseUI() throws HeadlessException, IOException {
		super("Pass Phrase");

		setBounds(320, 240, 240, 180);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);

		words = Files.lines(Paths.get("wordlist.txt")).map(String::toLowerCase).
				collect(Collectors.toList());

		pwdField.addAncestorListener(requestFocusListener);

		genPassButton.setBounds(10, 10, 200, 40);
		genPassButton.addActionListener(genPassAction);
		add(genPassButton);

		enterPassButton.setBounds(10, 70, 200, 40);
		enterPassButton.addActionListener(enterPassAction);
		enterPassButton.setEnabled(false);
		add(enterPassButton);
	}

	private String generatePassword() {
		StringBuilder pwBuilder = new StringBuilder("");
		for (int i = 0; i < 3; i++) {
			String word = words.get(randGen.nextInt(words.size()));
			pwBuilder.append(word);
			if (i != 2) {
				pwBuilder.append(" ");
			}
		}
		return pwBuilder.toString();
	}

	private void printCSVLog(String text, int passType) {
		String line = System.currentTimeMillis() + ", "
				+ Integer.toHexString(passwords.hashCode()) + ", "
				+ passType + ", " + text + "\n";
		System.out.print(line);
		try {
			Files.write(Paths.get("out.txt"), line.getBytes(StandardCharsets.UTF_8),
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean verifyPassword(int passType) {
		boolean passwordRemembered = false;
		printCSVLog("create, re-enter", passType);
		while (!passwordRemembered) {
			pwdField.setText("");
			int action = JOptionPane.showConfirmDialog(this, new Object[] {
					"<html>Your " + PASS_TYPE_STRINGS[passType]
									+ " password is: " + passwords[passType] +
									"\n<html>Please re-enter your " + PASS_TYPE_STRINGS[passType] +
									" password.", pwdField
								}, "Re-enter Password", JOptionPane.OK_CANCEL_OPTION);
			if (action != 0) {
				printCSVLog("create, cancel", passType);
				passwords = new String[3];
				enterPassButton.setEnabled(false);
				return false;
			} else if (passwords[passType].equals(String
					.valueOf(pwdField.getPassword()))) {
				printCSVLog("create, success", passType);
				passwordRemembered = true;
			} else {
				printCSVLog("create, verify_fail", passType);
			}

		}
		enterPassButton.setEnabled(true);
		return true;
	}

	public static void main(String[] args) throws Exception {
		Files.write(Paths.get("out.txt"),
				"timestamp, passarrayhash, passtype, mode, event\n"
				.getBytes(StandardCharsets.UTF_8));
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new PassPhraseUI().setVisible(true);
	}

}
