package ai.salmonbrain.events;

public class AdEvent extends ExperimentEvent{
    public final EventType TYPE = EventType.AD;
    private String sdkName;
    private String placement;
    private String adType;
    private String action;
    private String error;
    private long duration;
    private boolean isFirst;

    public AdEvent() {

    }

    public AdEvent(
            String entityUid,
            String categoryName,
            String categoryValue,
            String variantId,
            String experimentUid,
            String sdkName,
            String placement,
            String adType,
            String action,
            String error,
            Long duration,
            boolean isFirst
    ) {
        super(entityUid, categoryName, categoryValue, variantId, experimentUid);
        this.sdkName = sdkName;
        this.placement = placement;
        this.adType = adType;
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

    public String getAdType() {
        return adType;
    }

    public void setAdType(String type) {
        this.adType = type;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
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
                "TYPE=" + TYPE +
                ", sdkName='" + sdkName + '\'' +
                ", placement='" + placement + '\'' +
                ", adType='" + adType + '\'' +
                ", action='" + action + '\'' +
                ", error='" + error + '\'' +
                ", duration=" + duration +
                ", isFirst=" + isFirst +
                "} " + super.toString();
    }
}
