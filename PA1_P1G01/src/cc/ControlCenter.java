package cc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Class for the Control Center for the agricultural harvest.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class ControlCenter {
    
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    
    public ControlCenter(int windowSizeReduction){
        prepareGUI(windowSizeReduction);
    }
    public static void main(String[] args){
        System.out.println("[CC]: Initializing Control Center... ");
        
        ControlCenter fi = new ControlCenter(Integer.valueOf(args[2])); 
        fi.showControlPanel();
    }
    private void prepareGUI(int windowSizeReduction){
        mainFrame = new JFrame("Farm Infrastructure UI");
        mainFrame.setSize(1920/windowSizeReduction,1080/windowSizeReduction);
        mainFrame.setLayout(new GridLayout(3, 1));

        headerLabel = new JLabel("",JLabel.CENTER );
        statusLabel = new JLabel("",JLabel.CENTER);        
        statusLabel.setSize(350,100);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }        
        });    
        controlPanel = new ControlPanel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);  
    }
    private void showControlPanel(){
        
        /*
        headerLabel.setText("Control in action: Button"); 

        JButton okButton = new JButton("OK");
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        okButton.setActionCommand("OK");
        submitButton.setActionCommand("Submit");
        cancelButton.setActionCommand("Cancel");

        okButton.addActionListener(new ButtonClickListener()); 
        submitButton.addActionListener(new ButtonClickListener()); 
        cancelButton.addActionListener(new ButtonClickListener()); 

        controlPanel.add(okButton);
        controlPanel.add(submitButton);
        controlPanel.add(cancelButton);   
        */

        mainFrame.setVisible(true);  
    }
    private class ButtonClickListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();  

            if( command.equals( "OK" ))  {
                statusLabel.setText("Ok Button clicked.");
            } else if( command.equals( "Submit" ) )  {
                statusLabel.setText("Submit Button clicked."); 
            } else {
                statusLabel.setText("Cancel Button clicked.");
            }  	
        }		
    }
    
}
