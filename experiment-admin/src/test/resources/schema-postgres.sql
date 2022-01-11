DROP TABLE IF EXISTS experiment CASCADE;
DROP TABLE IF EXISTS experiment_metric_data;

CREATE TABLE IF NOT EXISTS experiment
(
    id SERIAL PRIMARY KEY,
    exp_uid VARCHAR(128) NOT NULL,
    ts timestamp NOT NULL,
    UNIQUE(exp_uid)
);

CREATE TABLE IF NOT EXISTS experiment_metric_data (
    id SERIAL PRIMARY KEY,
    experiment_id bigint NOT NULL,
    metric_name VARCHAR(256) NOT NULL,
    ts timestamp NOT NULL,
    statistics_data jsonb NOT NULL,
    UNIQUE (experiment_id, metric_name, ts)
);