package net.specialattack.forge.core.sync;

public abstract class BaseSyncable implements ISyncable {

    protected boolean hasChanged;
    protected int id;
    private ISyncableObjectOwner owner;

    public BaseSyncable(ISyncableObjectOwner owner) {
        this.owner = owner;
        this.id = -1;
    }

    @Override
    public ISyncableObjectOwner getOwner() {
        return this.owner;
    }

    @Override
    public boolean hasChanged() {
        return this.hasChanged;
    }

    @Override
    public void setChanged(boolean value) {
        this.hasChanged = value;
    }

    @Override
    public int getId() {
        if (this.id == -1) {
            this.id = SyncHandler.Server.getNextFreeId();
        }
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

}
