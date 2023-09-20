import javax.swing.*;

public class MenuFrame extends JFrame {
    private JPanel _mainPanel, _headerPanel, _menuPanel, _bottomPanel, _sortPanel;
    private JLabel _title, _createTask, _showTasks;

    public MenuFrame(){
        super("tasky");

        setDefaultParameters();

        setVisible(true);
    }

    private void setDefaultParameters(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setResizable(false);

    }



}
