package ai.salmonbrain.events;

import java.util.Objects;

public abstract class ExperimentEvent extends Event {
    private String entityUid;
    private String categoryName;
    private String categoryValue;
    private String variantId;
    private String experimentUid;

    public ExperimentEvent() {
    }

    public ExperimentEvent(String entityUid, String categoryName, String categoryValue, String variantId, String experimentUid) {
        this.entityUid = entityUid;
        this.categoryName = categoryName;
        this.categoryValue = categoryValue;
        this.variantId = variantId;
        this.experimentUid = experimentUid;
    }

    public String getEntityUid() {
        return entityUid;
    }

    public void setEntityUid(String entityUid) {
        this.entityUid = entityUid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryValue() {
        return categoryValue;
    }

    public void setCategoryValue(String categoryValue) {
        this.categoryValue = categoryValue;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public String getExperimentUid() {
        return experimentUid;
    }

    public void setExperimentUid(String experimentUid) {
        this.experimentUid = experimentUid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExperimentEvent)) return false;
        if (!super.equals(o)) return false;
        ExperimentEvent that = (ExperimentEvent) o;
        return Objects.equals(getEntityUid(), that.getEntityUid()) && Objects.equals(getCategoryName(), that.getCategoryName()) && Objects.equals(getCategoryValue(), that.getCategoryValue()) && Objects.equals(getVariantId(), that.getVariantId()) && Objects.equals(getExperimentUid(), that.getExperimentUid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEntityUid(), getCategoryName(), getCategoryValue(), getVariantId(), getExperimentUid());
    }

    @Override
    public String toString() {
        return "ExperimentEvent{" +
                "entityUid='" + entityUid + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryValue='" + categoryValue + '\'' +
                ", variantId='" + variantId + '\'' +
                ", experimentUid='" + experimentUid + '\'' +
                "} " + super.toString();
    }
}


