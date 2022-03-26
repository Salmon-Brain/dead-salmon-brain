export interface ExperimentPage {
  experiments: ExperimentData[];
  totalCount: number;
}

export interface ExperimentData {
  id: number;
  expUid: string;
  timestamp: Date;
  metricData: MetricData[];
}

export interface MetricData {
  metricName: string;
  timestamp: Date;
  statisticsData: StatisticsData;
}

export interface StatisticsData {
  statResult: StatResult;
  srm: boolean;
  controlSize: number;
  treatmentSize: number;
  testType: string;
  alpha: number;
  beta: number;
  metricSource: string;
  additive: boolean;
  categoryName: string;
  categoryValue: string;
}

export interface StatResult {
  statistic: number;
  requiredSampleSizeByVariant: number;
  controlCentralTendency: number;
  treatmentCentralTendency: number;
  controlVariance: number;
  treatmentVariance: number;
  percentageLeft: number;
  percentageRight: number;
  centralTendencyType: string;
  pvalue: number;
}
