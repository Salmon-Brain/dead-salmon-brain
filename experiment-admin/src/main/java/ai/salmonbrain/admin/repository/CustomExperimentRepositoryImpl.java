package ai.salmonbrain.admin.repository;

import ai.salmonbrain.admin.model.Experiment;
import ai.salmonbrain.admin.model.ExperimentMetricData;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class CustomExperimentRepositoryImpl implements CustomExperimentRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Lazy
    ExperimentRepository experimentRepository;

    @Transactional
    public Experiment findOrCreate(String expUid) {
        TypedQuery<Experiment> query = em.createQuery(
                "Select e FROM Experiment e where e.expUid = ?1",
                Experiment.class
        );
        List<Experiment> list = query.setParameter(1, expUid).getResultList();
        if (list.isEmpty()) {
            Experiment entity = new Experiment(expUid, getTs());
            em.persist(entity);
            return entity;
        } else {
            Experiment experiment = list.get(0);
            Hibernate.initialize(experiment.getMetricData());
            return experiment;
        }
    }

    private static Timestamp getTs() {
        long millis = System.currentTimeMillis();
        // drop ms
        return new Timestamp(millis / 1000 * 1000);
    }

    @Transactional
    public void addStatToExperiment(String expUid, ExperimentMetricData data) {
        Experiment experiment = findOrCreate(expUid);
        experiment.append(data);
        Timestamp timestamp = experiment.getTimestamp();
        if (data.getTimestamp().after(timestamp)) {
            timestamp = data.getTimestamp();
        }
        experiment.setTimestamp(timestamp);
        data.setExperiment(experiment);
        em.merge(experiment);
        em.persist(data);
    }

    @Transactional
    public void addStatToExperiment(String expUid, List<ExperimentMetricData> datas) {
        Experiment experiment = findOrCreate(expUid);
        for (ExperimentMetricData data : datas) {
            experiment.append(data);
            Timestamp timestamp = experiment.getTimestamp();
            if (data.getTimestamp().after(timestamp)) {
                timestamp = data.getTimestamp();
            }
            experiment.setTimestamp(timestamp);
            data.setExperiment(experiment);
            em.merge(experiment);
            em.persist(data);
        }
    }
}
