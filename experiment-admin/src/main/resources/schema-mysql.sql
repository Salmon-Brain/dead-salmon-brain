CREATE TABLE IF NOT EXISTS experiment (
    id bigint NOT NULL AUTO_INCREMENT,
    exp_uid VARCHAR(255) NOT NULL,
    timestamp timestamp NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (exp_uid)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS experiment_metric_data (
  id bigint NOT NULL AUTO_INCREMENT,
  alpha double NOT NULL,
  beta double NOT NULL,
  category_name varchar(255) NOT NULL,
  category_value varchar(255) NOT NULL,
  central_tendency_type varchar(255) NOT NULL,
  control_central_tendency double NOT NULL,
  control_size bigint NOT NULL,
  control_variance double NOT NULL,
  is_additive bit(1) NOT NULL,
  metric_name varchar(255) NOT NULL,
  metric_source varchar(255) NOT NULL,
  p_value double NOT NULL,
  percentage_left double NOT NULL,
  percentage_right double NOT NULL,
  required_sample_size_by_variant double NOT NULL,
  srm bit(1) NOT NULL,
  statistic double NOT NULL,
  test_type varchar(255) NOT NULL,
  treatment_central_tendency double NOT NULL,
  treatment_size bigint NOT NULL,
  treatment_variance double NOT NULL,
  timestamp timestamp NOT NULL,
  experiment_id bigint NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (experiment_id, metric_name, category_name, category_value, timestamp),
  CONSTRAINT FOREIGN KEY (experiment_id) REFERENCES experiment (id)
) ENGINE=InnoDB;