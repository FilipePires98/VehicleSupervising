/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

/**
 * Interface created to standardize the controls available to the public in the Load Balancer and Tactic Manager GUI.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public interface UiController {
    public void defineUpServers(String[] servers);
    public void defineClients(String[] clients);
    public void addProcessingMessage(String[] messages);
    public void addProcessedMessage(String[] messages);
}
