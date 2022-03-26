export class MetricBlock {
  pValue: ChartData;
  percentage: ChartData;
  controlCentralTendency: ChartData;
  treatmentCentralTendency: ChartData;
  name: string;
  categoryName: string;
  categoryValue: string;

  constructor(name: string, categoryName: string, categoryValue: string) {
    this.name = name;
    this.categoryName = categoryName;
    this.categoryValue = categoryValue;
    this.pValue = new ChartData();
    this.percentage = new ChartData();
    this.controlCentralTendency = new ChartData();
    this.treatmentCentralTendency = new ChartData();
  }
}

export class ChartData {
  lines: Map<String, LineData>

  constructor() {
    this.lines = new Map<String, LineData>();
  }
}

export class LineData {
  dots: number[][];
  name: string;

  constructor(name: string) {
    this.name = name;
    this.dots = new Array<number[]>();
  }
}
