package fi;

/**
 * Class for the Farmer Thread for the agricultural harvest.
 * @author Filipe Pires (85122) and João Alegria (85048)
 */
public class Farmer extends Thread {
    
    private FarmerState state;
    private Storehouse storeHouse;
    private Standing standing;
    private Path path;
    private Granary granary;
    private final int id;

    public Farmer(int id,Storehouse storeHouse,Standing standing,Path path,Granary granary) {
        this.storeHouse=storeHouse;
        this.standing=standing;
        this.path=path;
        this.granary=granary;
        this.id=id;
    }
    
    @Override
    public void run() {
        while(true){
            this.state=FarmerState.INITIAL;
            this.storeHouse.hold();
            this.state=FarmerState.PREPARE;
            this.standing.prepare();
            this.state=FarmerState.WALK;
            this.path.walkThroughPath();
            this.state=FarmerState.WAITTOCOLLECT;
            this.granary.waitToCollect();
            this.state=FarmerState.COLLECT;
            this.granary.collect();
            this.state=FarmerState.WAITTORETURN;
            this.granary.waitToReturn();
            this.state=FarmerState.RETURN;
            this.path.returnThroughPath();
            this.state=FarmerState.STORE;
            this.storeHouse.store();
        }
    }

    public int getID() {
        return id;
    }
    
    
    
}
