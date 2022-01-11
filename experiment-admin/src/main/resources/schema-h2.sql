CREATE TABLE IF NOT EXISTS experiment (
    id bigint NOT NULL AUTO_INCREMENT,
    exp_uid VARCHAR(128) NOT NULL,
    ts timestamp NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (exp_uid)
);

CREATE TABLE IF NOT EXISTS experiment_metric_data(
    id bigint NOT NULL AUTO_INCREMENT,
    experiment_id bigint NOT NULL,
    metric_name VARCHAR(256) NOT NULL,
    ts timestamp NOT NULL,
    statistics_data VARCHAR(1024) NOT NULL,
    PRIMARY KEY (id)
);