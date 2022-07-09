package ai.salmonbrain.events;


public class PurchaseEvent extends ExperimentEvent {
    public static final EventType TYPE = EventType.PURCHASE;
    private String place;
    private String itemType;
    private String itemId;
    private Long amount;
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

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "PurchaseEvent{" +
                "place='" + place + '\'' +
                ", itemType='" + itemType + '\'' +
                ", itemId=" + itemId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
