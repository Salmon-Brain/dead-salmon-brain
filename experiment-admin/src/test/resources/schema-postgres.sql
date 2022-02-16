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
    metric_name VARCHAR(128) NOT NULL,
    ts timestamp NOT NULL,
    alpha double precision NOT NULL,
    beta double precision NOT NULL,
    category_name VARCHAR(128) NOT NULL,
    category_value VARCHAR(128) NOT NULL,
    central_tendency_type VARCHAR(128) NOT NULL,
    control_central_tendency double precision NOT NULL,
    control_size bigint NOT NULL,
    control_variance double precision NOT NULL,
    is_additive boolean NOT NULL,
    metric_source VARCHAR(128) NOT NULL,
    p_value double precision NOT NULL,
    percentage_left double precision NOT NULL,
    percentage_right double precision NOT NULL,
    required_sample_size_by_variant double precision NOT NULL,
    srm boolean NOT NULL,
    statistic double precision NOT NULL,
    test_type VARCHAR(128) NOT NULL,
    treatment_central_tendency double precision NOT NULL,
    treatment_size bigint NOT NULL,
    treatment_variance double precision NOT NULL,
    UNIQUE (experiment_id, metric_name, ts, category_name, category_value)
);