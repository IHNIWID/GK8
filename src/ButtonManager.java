
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class ButtonManager implements ActionListener {
    JButton[] buttons = new JButton[0];

    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        switch (ac) {
            case "add":
                getButton("remove").setEnabled(false);
                enable(true);
                break;
            case "remove":
                getButton("add").setEnabled(false);
                enable(true);
                break;
            default:
                reset();
                break;
        }
    }

    public void reset() {
        getButton("add").setEnabled(true);
        getButton("remove").setEnabled(true);
        enable(false);
    }

    private void enable(boolean enable) {
        getButton("cancel").setEnabled(enable);
    }

    public void add(JButton button) {
        button.addActionListener(this);
        int size = buttons.length;
        JButton[] temp = new JButton[size+1];
        System.arraycopy(buttons, 0, temp, 0, size);
        temp[size] = button;
        buttons = temp;
    }

    private JButton getButton(String target) {
        for (JButton button : buttons) {
            if (button.getActionCommand().equals(target)) {
                return button;
            }
        }
        return null;
    }    
}