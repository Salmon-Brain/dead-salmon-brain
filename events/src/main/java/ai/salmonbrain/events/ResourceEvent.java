package ai.salmonbrain.events;


public class ResourceEvent extends PurchaseEvent{
    public static final EventType TYPE = EventType.RESOURCE;
    public ResourceEvent(){}

    public ResourceEvent(
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
        super(entityUid, categoryName, categoryValue, variantId, experimentUid, place, itemType, itemId, amount, currency);
    }


    @Override
    public String toString() {
        return "ResourceEvent{" +
                "place='" + getPlace() + '\'' +
                ", itemType='" + getItemType() + '\'' +
                ", itemId=" + getItemId() +
                ", amount=" + getAmount() +
                ", currency='" + getCurrency() + '\'' +
                '}';
    }
}

