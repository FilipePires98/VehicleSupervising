/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.workers;

/**
 *
 * @author joaoalegria
 */
public enum FarmerState {
    INITIAL,
    PREPARE,
    WALK,
    WAITTOCOLLECT,
    COLLECT,
    WAITTORETURN,
    RETURN,
    STORE
}
