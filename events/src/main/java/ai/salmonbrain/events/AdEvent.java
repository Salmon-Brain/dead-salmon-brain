package ai.salmonbrain.events;

public class AdEvent extends ExperimentEvent{
    public static final EventType TYPE = EventType.AD;
    private String sdkName;
    private String placement;
    private String type;
    private String action;
    private String error;
    private Long duration;
    private boolean isFirst;


    public AdEvent(
            String entityUid,
            String categoryName,
            String categoryValue,
            String variantId,
            String experimentUid,
            String sdkName,
            String placement,
            String type,
            String action,
            String error,
            Long duration,
            boolean isFirst
    ) {
        super(entityUid, categoryName, categoryValue, variantId, experimentUid);
        this.sdkName = sdkName;
        this.placement = placement;
        this.type = type;
        this.action = action;
        this.error = error;
        this.duration = duration;
        this.isFirst = isFirst;
    }

    public String getSdkName() {
        return sdkName;
    }

    public void setSdkName(String sdkName) {
        this.sdkName = sdkName;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    @Override
    public String toString() {
        return "AdEvent{" +
                "sdkName='" + sdkName + '\'' +
                ", placement='" + placement + '\'' +
                ", type='" + type + '\'' +
                ", action='" + action + '\'' +
                ", error='" + error + '\'' +
                ", duration=" + duration +
                ", isFirst=" + isFirst +
                '}';
    }
}
