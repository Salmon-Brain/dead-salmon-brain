package ai.salmonbrain.events;


import java.util.Objects;

public class PurchaseEvent extends ExperimentEvent {
    public final EventType TYPE = EventType.PURCHASE;
    private String place;
    private String itemType;
    private String itemId;
    private long amount;
    private String currency;

    public PurchaseEvent(){}

    public PurchaseEvent(
            String entityUid,
            String categoryName,
            String categoryValue,
            String variantId,
            String experimentUid,
            String place,
            String itemType,
            String itemId,
            Long amount,
            String currency
    ) {
        super(entityUid, categoryName, categoryValue, variantId, experimentUid);
        this.place = place;
        this.itemType = itemType;
        this.itemId = itemId;
        this.amount = amount;
        this.currency = currency;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseEvent)) return false;
        if (!super.equals(o)) return false;
        PurchaseEvent that = (PurchaseEvent) o;
        return TYPE == that.TYPE && Objects.equals(getPlace(), that.getPlace()) && Objects.equals(getItemType(), that.getItemType()) && Objects.equals(getItemId(), that.getItemId()) && Objects.equals(getAmount(), that.getAmount()) && Objects.equals(getCurrency(), that.getCurrency());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), TYPE, getPlace(), getItemType(), getItemId(), getAmount(), getCurrency());
    }

    @Override
    public String toString() {
        return "PurchaseEvent{" +
                "TYPE=" + TYPE +
                ", place='" + place + '\'' +
                ", itemType='" + itemType + '\'' +
                ", itemId='" + itemId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                "} " + super.toString();
    }
}
