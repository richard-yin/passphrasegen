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

@SuppressWarnings("serial")
public class PassPhraseUI extends JFrame {
	private List<String> words;
	private String password;
	
	private JButton genPassButton = new JButton("Create Password");
	private JButton enterPassButton = new JButton("Enter password");
	
	private Random randGen = new Random();
	
	private ActionListener genPassAction = event -> {
		StringBuilder pwBuilder = new StringBuilder("");
		for (int i = 0; i < 3; i++) {
			// TODO read nextInt javadoc
			String word = words.get(randGen.nextInt(words.size() - 1));
			pwBuilder.append(word);
			if (i != 2) {
				pwBuilder.append(" ");
			}
		}
		password = pwBuilder.toString();
		
		boolean passwordRemembered = false;
		while (!passwordRemembered) {
			String input = JOptionPane.showInputDialog(this, "Your password is: " + password + ".\n"
					+ "Please enter your password:");
			if (input == null) {
				password = null;
				enterPassButton.setEnabled(false);
				return;
			} else if (password.equals(input)) {
				input = JOptionPane.showInputDialog(this, "Please re-enter your password:");
				if (input == null) {
					password = null;
					enterPassButton.setEnabled(false);
					return;
				} else if (password.equals(input)) {
					passwordRemembered = true;
				}
			}
		}
		enterPassButton.setEnabled(true);
	};
	
	private ActionListener enterPassAction = event -> {
		String input = JOptionPane.showInputDialog("Please enter your password: ");
		if (input == null) {
			return;
		} else if (password.equals(input)) {
			JOptionPane.showMessageDialog(this, "Password successfully entered.",
					"Success", JOptionPane.INFORMATION_MESSAGE);
		} else {
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
		
		genPassButton.setBounds(10, 10, 200, 40);
		genPassButton.addActionListener(genPassAction);
		add(genPassButton);
		
		enterPassButton.setBounds(10, 70, 200, 40);
		enterPassButton.addActionListener(enterPassAction);
		enterPassButton.setEnabled(false);
		add(enterPassButton);
	}

	public static void main(String[] args) throws Exception {
		new PassPhraseUI().setVisible(true);
	}

}
