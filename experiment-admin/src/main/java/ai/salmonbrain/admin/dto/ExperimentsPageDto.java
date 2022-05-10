package ai.salmonbrain.admin.dto;

import java.util.List;

public class ExperimentsPageDto {
    private List<ExperimentDto> experiments;
    private long totalCount;

    public List<ExperimentDto> getExperiments() {
        return experiments;
    }

    public void setExperiments(List<ExperimentDto> experiments) {
        this.experiments = experiments;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }


    @Override
    public String toString() {
        return "ExperimentsPageDto{" +
                "experiments=" + experiments +
                ", totalCount=" + totalCount +
                '}';
    }
}
