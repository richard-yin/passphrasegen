import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	private List<String> words;
	private String password;
	
	private JButton genPassButton = new JButton("Create Password");
	private JButton enterPassButton = new JButton("Enter password");
	
	private Random randGen = new Random();
	private JPasswordField pwdField = new JPasswordField();
	private AncestorListener requestFocusListener = new AncestorListener() {
		@Override
		public void ancestorAdded(AncestorEvent event) {
			event.getComponent().requestFocusInWindow();
		}

		@Override public void ancestorMoved(AncestorEvent event) {}
		@Override public void ancestorRemoved(AncestorEvent event) {}
		
	};
	
	private ActionListener genPassAction = event -> {
		StringBuilder pwBuilder = new StringBuilder("");
		for (int i = 0; i < 3; i++) {
			String word = words.get(randGen.nextInt(words.size()));
			pwBuilder.append(word);
			if (i != 2) {
				pwBuilder.append(" ");
			}
		}
		password = pwBuilder.toString();
		printCSVLog("create, start");
		
		boolean passwordRemembered = false;
		while (!passwordRemembered) {
			pwdField.setText("");
			int action = JOptionPane.showConfirmDialog(this,
					new Object[]{"Your password is: " + password + "\n"
					+ "Please enter your password:", pwdField}, "Create Password",
					JOptionPane.OK_CANCEL_OPTION);
			if (action == 0) {
				if (password.equals(String.valueOf(pwdField.getPassword()))) {
					printCSVLog("create, re-enter");
					pwdField.setText("");
					action = JOptionPane.showConfirmDialog(this,
							new Object[]{"Please re-enter your password:", pwdField},
							"Re-enter Password", JOptionPane.OK_CANCEL_OPTION);
					if (action != 0) {
						printCSVLog("create, cancel");
						password = null;
						enterPassButton.setEnabled(false);
						return;
					} else if (password.equals(String.valueOf(pwdField.getPassword()))) {
						printCSVLog("create, success");
						passwordRemembered = true;
					} else {
						printCSVLog("create, re-enter_fail");
					}
				} else {
					printCSVLog("create, enter_fail");
				}
			} else {
				printCSVLog("create, cancel");
				password = null;
				enterPassButton.setEnabled(false);
				return;
			}
		}
		enterPassButton.setEnabled(true);
	};
	
	private ActionListener enterPassAction = event -> {
		pwdField.setText("");
		int action = JOptionPane.showConfirmDialog(this,
				new Object[]{"Please enter your password:", pwdField},
				"Enter Password", JOptionPane.OK_CANCEL_OPTION);
		if (action != 0) {
			printCSVLog("enter, cancel");
			return;
		} else if (password.equals(String.valueOf(pwdField.getPassword()))) {
			printCSVLog("enter, success");
			JOptionPane.showMessageDialog(this, "Password successfully entered.",
					"Success", JOptionPane.INFORMATION_MESSAGE);
		} else {
			printCSVLog("enter, fail");
			JOptionPane.showMessageDialog(this, "Password incorrect.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	};

	public PassPhraseUI() throws HeadlessException, IOException {
		super("Pass Phrase");

		setBounds(320, 240, 240, 180);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(null);

		words = Files.lines(Paths.get("wordlist.txt")).collect(Collectors.toList());
		
		pwdField.addAncestorListener(requestFocusListener);
		
		genPassButton.setBounds(10, 10, 200, 40);
		genPassButton.addActionListener(genPassAction);
		add(genPassButton);
		
		enterPassButton.setBounds(10, 70, 200, 40);
		enterPassButton.addActionListener(enterPassAction);
		enterPassButton.setEnabled(false);
		add(enterPassButton);
	}
	
	private void printCSVLog(String text) {
		System.out.print(System.currentTimeMillis() + ", ");
		if (password == null) {
			System.out.println("null, " + text);
		}
		else {
			System.out.println(Integer.toHexString(password.hashCode()) + ", " + text);
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("timestamp, passwordhash, mode, event");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new PassPhraseUI().setVisible(true);
	}

}
