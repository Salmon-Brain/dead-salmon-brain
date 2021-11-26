CREATE TABLE IF NOT EXISTS experiment_metric_data (
    exp_uid VARCHAR(128) NOT NULL,
    metric_name VARCHAR(256) NOT NULL,
    ts timestamp NOT NULL,
    statistics_data TEXT NOT NULL,
    PRIMARY KEY (exp_uid, metric_name, ts)
);