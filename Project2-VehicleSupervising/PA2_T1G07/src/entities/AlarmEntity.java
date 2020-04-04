package entities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import kafkaUtils.EntityAction;
import message.Message;

/**
 * Class for the Alarm Entity for the car supervising system.
 * 
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class AlarmEntity extends JFrame implements EntityAction {
    
    private String topicName="AlarmTopic";
    private FileWriter file;
    private boolean isAlarmOn=false;

    /**
     * Creates new form CollectEntity
     */
    public AlarmEntity() {
        this.setTitle("Alarm Entiry");
        initComponents();
        
        try {
            this.file=new FileWriter("data/ALARM.TXT");
        } catch (IOException ex) {
            Logger.getLogger(ReportEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.out.println("[Alarm] Running...");
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CollectEntity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CollectEntity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CollectEntity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CollectEntity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AlarmEntity().setVisible(true);
            }
        });
    }

    @Override
    public void processMessage(String topic, Object key, Object value) {
        try {
            Message data = (Message)value;
            if(data.getType()==1){//message is of type speed
                String tmp="";
                if(!isAlarmOn && data.getSpeed()>120){
                    tmp=data.toString()+" | ON |";
                }else if(isAlarmOn && data.getSpeed()<120){
                    tmp=data.toString()+" | OFF |";
                }
                
                if(tmp.length()>0){
                    file.write(tmp);
                    System.out.println("[ALARM] Processed message: "+tmp);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ReportEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
