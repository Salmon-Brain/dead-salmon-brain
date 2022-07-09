package ai.salmonbrain.events;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentEvent extends Event{
    private String entityUid;
    private String categoryName;
    private String categoryValue;
    private String variantId;
    private String experimentUid;

    public ExperimentEvent(){
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
    public String toString() {
        return "ExperimentEvent{" +
                "entityUid='" + entityUid + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryValue='" + categoryValue + '\'' +
                ", variantId='" + variantId + '\'' +
                ", experimentUid='" + experimentUid + '\'' +
                '}';
    }
}


